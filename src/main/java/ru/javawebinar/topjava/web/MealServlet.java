package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class MealServlet extends HttpServlet {
    private static final Logger                         log = LoggerFactory.getLogger(MealServlet.class);
    private              MealRestController             controller;
    private              ConfigurableApplicationContext appCtx;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml");
        log.info("Bean definition names: {}", Arrays.toString(appCtx.getBeanDefinitionNames()));
        controller = appCtx.getBean(MealRestController.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<MealTo> meals = null;
        String action = request.getParameter("action");
        switch (action == null ? Boolean.parseBoolean(request.getParameter("filter")) ? "filter" : "all" : action) {
            case "delete":
                controller.delete(getId(request));
                response.sendRedirect("meals");
                break;
            case "create":
                LocalDateTime roundedNow = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
                request.setAttribute("meal", controller.create(new Meal(roundedNow, "", 1000)));
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "update":
                request.setAttribute("meal", controller.get(getId(request)));
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "filter":
                String startDateParam = request.getParameter("startDate");
                LocalDate startDate = isValidParameter(startDateParam) ? LocalDate.MIN
                                                                       : LocalDate.parse(startDateParam);
                String endDateParam = request.getParameter("endDate");
                LocalDate endDate = isValidParameter(endDateParam) ? LocalDate.MAX
                                                                   : LocalDate.parse(endDateParam);
                String startTimeParam = request.getParameter("startTime");
                LocalTime startTime = isValidParameter(startTimeParam) ? LocalTime.MIN
                                                                       : LocalTime.parse(startTimeParam);
                String endTimeParam = request.getParameter("endTime");
                LocalTime endTime = isValidParameter(endTimeParam) ? LocalTime.MAX
                                                                   : LocalTime.parse(endTimeParam);
                meals = controller.getAllFilteredByDates(startDate, endDate, startTime, endTime);
                request.setAttribute("startDate", startDateParam);
                request.setAttribute("endDate", endDateParam);
                request.setAttribute("startTime", startTimeParam);
                request.setAttribute("endTime", endTimeParam);
            case "all":
            default:
                meals = meals == null ? controller.getAll() : meals;
                request.setAttribute("meals", meals);
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        log.info("post Meal");
        String id = request.getParameter("id");
        Meal meal = new Meal(id.isEmpty() ? null : Integer.valueOf(id),
                             LocalDateTime.parse(request.getParameter("dateTime")), request.getParameter("description"),
                             Integer.parseInt(request.getParameter("calories")));
        if (meal.isNew()) {
            controller.create(meal);
        } else {
            controller.update(meal, meal.getId());
        }
        response.sendRedirect("meals");
    }

    @Override
    public void destroy() {
        appCtx.close();
        super.destroy();
    }

    private int getId(HttpServletRequest request) {
        return Integer.parseInt(Objects.requireNonNull(request.getParameter("id")));
    }

    private boolean isValidParameter(String parameter) {
        return parameter == null || parameter.isEmpty();
    }
}