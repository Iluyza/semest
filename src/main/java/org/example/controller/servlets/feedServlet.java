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

@WebServlet (urlPatterns = "/feed")
public class feedServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession()!=null) {
            User user = (User) req.getSession().getAttribute("User");
            List<Post> list = DatabaseManager.getAllFeed(user.getId());
            req.getSession().setAttribute("feed",list);
            req.getRequestDispatcher("/WEB-INF/view/feed.jsp").forward(req, resp);
        }
        else {
            resp.sendRedirect(req.getContextPath()+"/main");
        }
    }
}
