package ru.javawebinar.topjava.web;

import org.slf4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class UserServlet extends HttpServlet {
    private static final Logger log = getLogger(UserServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("forward to users");
        request.getRequestDispatcher("/users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("authUser".equals(request.getParameter("action"))) {
            log.debug("forward to user auth");
            String userId = request.getParameter("userId");
            SecurityUtil.setAuthUserId(Integer.parseInt(userId));
            request.setAttribute("userId", userId);
            request.getRequestDispatcher("/users.jsp").forward(request, response);
        }
    }
}