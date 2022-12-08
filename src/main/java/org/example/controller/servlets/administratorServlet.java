package org.example.controller.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.model.managers.PermissionManager;

import java.io.IOException;
import java.util.Objects;

@WebServlet (urlPatterns = "/admin")
public class administratorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/view/admin.jsp").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getParameter("userToGivePermission")!=null
                &&
                !Objects.equals(req.getParameter("userToGivePermission"), "")
        ){
            PermissionManager.setPermissions(Integer.parseInt(req.getParameter("permission")),
                    Integer.parseInt(req.getParameter("userToGivePermission")));
        }
        resp.sendRedirect(req.getContextPath()+"/admin");
    }
}