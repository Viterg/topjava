package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.service.MemMealService;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class MealServlet extends HttpServlet {
    private static final Logger            log        = LoggerFactory.getLogger(MealServlet.class);
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM,
                                                                                              FormatStyle.SHORT);
    private final        MealService       service;

    public MealServlet() {
        service = new MemMealService();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("localDateTimeFormat", dateFormat);
        String forward= "mealUpdate.jsp";
        String action = request.getParameter("action");
        if (action == null) {
            forward = "meals.jsp";
            request.setAttribute("meals", service.getAll());
            log.debug("Forward to " + forward);
            request.getRequestDispatcher(forward).forward(request, response);
        } else if (action.equalsIgnoreCase("delete")) {
            int mealId = Integer.parseInt(request.getParameter("mealId"));
            service.delete(mealId);
            forward = "meals";
            request.setAttribute("meals", service.getAll());
            log.debug("Redirect to " + forward);
            response.sendRedirect(forward);
        } else if (action.equalsIgnoreCase("edit")) {
            int mealId = Integer.parseInt(request.getParameter("mealId"));
            request.setAttribute("meal", service.getById(mealId));
            log.debug("Forward to " + forward);
            request.getRequestDispatcher(forward).forward(request, response);
        } else {
            log.debug("Forward to " + forward);
            request.getRequestDispatcher(forward).forward(request, response);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        LocalDateTime dateTime = dateFormat.parse(request.getParameter("dateTime"), LocalDateTime::from);
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        Meal meal = new Meal(dateTime, description, calories);
        Meal attrMeal = (Meal) request.getAttribute("meal");
        if (attrMeal == null) {
            service.add(meal);
        } else {
            meal.setId(attrMeal.getId());
            service.update(meal);
        }
        request.setAttribute("meals", service.getAll());
        log.debug("Forward to meals.jsp");
        request.getRequestDispatcher("meals.jsp").forward(request, response);
    }
}