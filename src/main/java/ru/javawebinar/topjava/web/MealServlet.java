package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.service.MemMealService;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class MealServlet extends HttpServlet {
    private static final Logger            log        = LoggerFactory.getLogger(MealServlet.class);
    private static final String            DT_PATTERN = "yyyy.MM.dd HH:mm";
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DT_PATTERN, Locale.getDefault());
    private              MealService       service;

    public MealServlet() {
    }

    @Override
    public void init() throws ServletException {
        super.init();
        service = new MemMealService();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String forward = "meals.jsp";
        String action = request.getParameter("action");
        if (action == null) {
            setOuterMealsAsAttribute(request);
            request.setAttribute("localDateTimeFormat", dateFormat);
            log.debug("Forward to {}", forward);
            request.getRequestDispatcher(forward).forward(request, response);
        } else {
            switch (action) {
                case "delete":
                    forward = "meals";
                    service.delete(getMealIdParameter(request));
                    log.debug("Redirect to {}", forward);
                    response.sendRedirect(forward);
                    break;
                case "edit":
                    request.setAttribute("meal", service.getById(getMealIdParameter(request)));
                case "insert":
                    forward = "mealUpdate.jsp";
                    request.setAttribute("localDateTimeFormat", dateFormat);
                    log.debug("Forward to {}", forward);
                    request.getRequestDispatcher(forward).forward(request, response);
                    break;
                default:
                    log.debug("Redirect to {}", forward);
                    response.sendRedirect(forward);
                    break;
            }
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        LocalDateTime dateTime = dateFormat.parse(request.getParameter("dateTime"), LocalDateTime::from);
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        Meal meal = new Meal(dateTime, description, calories);
        String mealId = request.getParameter("mealId");
        if (mealId == null || mealId.isEmpty()) {
            service.add(meal);
        } else {
            meal.setId(Integer.parseInt(mealId));
            service.update(meal);
        }
        log.debug("Redirect to meals");
        response.sendRedirect("meals");
    }

    public int getMealIdParameter(HttpServletRequest request) {
        return Integer.parseInt(request.getParameter("mealId"));
    }

    public void setOuterMealsAsAttribute(HttpServletRequest request) {
        List<MealTo> outMeals = MealsUtil.filteredByStreams(service.getAll(), LocalTime.MIN, LocalTime.MAX, 2000);
        request.setAttribute("meals", outMeals);
    }
}