package org.example.controller.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.model.entities.Post;
import org.example.model.entities.User;
import org.example.model.managers.DatabaseManager;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.nonNull;

@WebServlet (urlPatterns = "/userpage")
public class userPageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession()!=null) {
            List<Post> list;
            User user = (User) req.getSession().getAttribute("User");
            if (nonNull(req.getParameter("id"))) {
                req.getSession().setAttribute("Username", DatabaseManager.getUsername(Integer.parseInt(req.getParameter("id"))));
                list = DatabaseManager.getFeed(Integer.parseInt(req.getParameter("id")));
                if (user.getId()!=Integer.parseInt(req.getParameter("id"))){
                    if (DatabaseManager.isFollower(user.getId(),Integer.parseInt(req.getParameter("id")))) {
                        req.getSession().setAttribute("toFollow", "Unfollow");
                    }
                    else {
                        req.getSession().setAttribute("toFollow", "To follow");
                    }
                }
                else {
                    req.getSession().removeAttribute("toFollow");
                }
            }
            else {
                req.getSession().setAttribute("Username", user.getUsername());
                list = DatabaseManager.getFeed(user.getId());
            }
            req.getSession().setAttribute("feed", list);
            req.getRequestDispatcher("/WEB-INF/view/userPage.jsp").forward(req, resp);
        }
        else {
            resp.sendRedirect(req.getContextPath()+"/main");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession().getAttribute("User");
        if ("delete".equals(req.getParameter("action"))) {
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/main");
        }
        else if ("follow".equals(req.getParameter("action"))) {
            if (DatabaseManager.isFollower(user.getId(),Integer.parseInt(req.getParameter("id")))) {
                DatabaseManager.unfollow(user,Integer.parseInt(req.getParameter("id")));
            }
            else {
                DatabaseManager.toFollow(user, Integer.parseInt(req.getParameter("id")));
            }
            resp.sendRedirect(req.getContextPath() + "/userpage?id=" + req.getParameter("id"));
        }
    }
}
