package org.example.model.managers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.model.database.PostgresConnectionProvider;
import org.example.model.entities.Message;
import org.example.model.entities.Post;
import org.example.model.entities.User;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DatabaseManager {

    public static boolean createUser(String username, String password, String email) {
        try (Connection connection = PostgresConnectionProvider.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("insert into users (username, password," +
                    " email) values (?, ?, ?)");
            statement.setString(1, username);
            statement.setInt(2, password.hashCode());
            statement.setString(3, email);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean getUser(String username, String password, HttpServletRequest req) {
        try (Connection connection = PostgresConnectionProvider.getConnection()) {
            HttpSession session = req.getSession(true);
            PreparedStatement statement = connection.prepareStatement("select id, username, password," +
                    " email from users where username = ? and password = ?;");
            statement.setString(1, username);
            statement.setInt(2, password.hashCode());
            ResultSet set = statement.executeQuery();
            set.next();
            session.setAttribute("User", User.builder()
                    .id(set.getInt(1))
                    .username(set.getString(2))
                    .password(set.getInt(3))
                    .email(set.getString(4))
                    .build());
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static List<Post> getFeed(int id) {
        List<Post> list = new LinkedList<>();
        try (Connection connection = PostgresConnectionProvider.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("select posts.id, username, postname, post from users right join posts on owner_id = users.id where owner_id = ?;");
            getList(id, list, statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static List<Post> getAllFeed(int id) {
        List<Post> list = new LinkedList<>();
        try (Connection connection = PostgresConnectionProvider.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("select posts.id, username, postname, post from (select id_of_author from follower_list where id_of_follower = ?) as table1 left join (users left join posts on owner_id = users.id) on id_of_author = owner_id;");
            getList(id, list, statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private static void getList(int id, List<Post> list, PreparedStatement statement) {
        try (Connection connection = PostgresConnectionProvider.getConnection()) {
            statement.setInt(1, id);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                PreparedStatement st = connection.prepareStatement("select exists(select * from likes where id_of_user = ? and id_of_pos = ?)");
                PreparedStatement checkOwner = connection.prepareStatement("select exists(select * from posts where owner_id = ? and id = ?)");
                st.setInt(1, id);
                checkOwner.setInt(1, id);
                st.setInt(2, set.getInt(1));
                checkOwner.setInt(2,set.getInt(1));
                list.add(
                        Post.builder()
                                .id(set.getInt(1))
                                .owner(set.getString(2))
                                .name(set.getString(3))
                                .text(set.getString(4))
                                .liked(exists(st))
                                .isEditable(exists(checkOwner)||PermissionManager.checkPermission(id))
                                .build()
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean exists(PreparedStatement statement) throws SQLException {
        ResultSet set = statement.executeQuery();
        set.next();
        return set.getBoolean(1);
    }

    public static boolean createMessage(String AuthorName, String ReceiverName, String text) {
        try (Connection connection = PostgresConnectionProvider.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("insert into messages (name_of_author, name_of_receiver, message_text) VALUES (?,?,?)");
            statement.setString(1, AuthorName);
            statement.setString(2, ReceiverName);
            statement.setString(3, text);
            statement.execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static List<Message> getMessages(String AuthorName, String ReceiverName) {
        try (Connection connection = PostgresConnectionProvider.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("select name_of_author, message_text from messages where" +
                    "                     (name_of_author = ? AND name_of_receiver = ?) or (name_of_receiver = ? and name_of_author = ?) order by id;");
            statement.setString(1, AuthorName);
            statement.setString(2, ReceiverName);
            statement.setString(3, AuthorName);
            statement.setString(4, ReceiverName);
            ResultSet set = statement.executeQuery();
            LinkedList<Message> list = new LinkedList<>();
            while (set.next()) {
                list.add(
                        Message.builder()
                                .author(set.getString(1))
                                .text(set.getString(2))
                                .build()
                );
            }
            return list;
        } catch (SQLException e) {
            return null;
        }
    }

    public static boolean deletePost(int id){
        try (Connection connection = PostgresConnectionProvider.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("delete from posts where id=?;");
            statement.setInt(1,id);
            return statement.execute();
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean postUpdate(Post post){
        try(Connection connection = PostgresConnectionProvider.getConnection()){
            PreparedStatement statement = connection.prepareStatement("update posts set postname = ? and post = ? where id = ?;");
            statement.setString(1,post.getName());
            statement.setString(2,post.getText());
            statement.setInt(3, post.getId());
            return statement.execute();
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean toFollow(User user, int id){
        try(Connection connection = PostgresConnectionProvider.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("insert into follower_list (id_of_follower, id_of_author) VALUES (?, ?)");
            statement.setInt(1, user.getId());
            statement.setInt(2, id);
            statement.execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean unfollow(User user, int id){
        try(Connection connection = PostgresConnectionProvider.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("delete from follower_list where id_of_follower = ? and id_of_author = ?;");
            statement.setInt(1, user.getId());
            statement.setInt(2, id);
            statement.execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private static int getId(String nickname){
        try(Connection connection = PostgresConnectionProvider.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("select id from users where username = ?");
            statement.setString(1, nickname);
            ResultSet set = statement.executeQuery();
            set.next();
            return set.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean createPost(int id, String name, String text){
        try(Connection connection = PostgresConnectionProvider.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("insert into posts (owner_id, postname, post) VALUES (?,?,?)");
            statement.setInt(1,id);
            statement.setString(2,name);
            statement.setString(3,text);
            statement.execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static String getUsername(int id){
        try (Connection connection = PostgresConnectionProvider.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("select username from users where id = ?");
            statement.setInt(1,id);
            ResultSet set = statement.executeQuery();
            set.next();
            return set.getString(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isFollower(int follower_id, int author_id){
        try (Connection connection = PostgresConnectionProvider.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("select exists(select * from follower_list where id_of_author = ? and id_of_follower = ?)");
            statement.setInt(1,author_id);
            statement.setInt(2, follower_id);
            return exists(statement);
        } catch (SQLException e) {
            return false;
        }
    }
}
