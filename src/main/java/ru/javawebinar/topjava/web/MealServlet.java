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
        if (Boolean.parseBoolean(request.getParameter("filter"))) {
            log.info("Filter meals");
            LocalDate startDate = LocalDate.parse(request.getParameter("startDate"));
            LocalDate endDate = LocalDate.parse(request.getParameter("endDate"));
            LocalTime startTime = LocalTime.parse(request.getParameter("startTime"));
            LocalTime endTime = LocalTime.parse(request.getParameter("endTime"));
            // request.setAttribute("meals", filtered);
            meals = controller.getAllFilteredByDates(startDate, endDate, startTime, endTime);
            // request.getRequestDispatcher("/meals.jsp").forward(request, response);
        }
        String action = request.getParameter("action");
        switch (action == null ? "all" : action) {
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
}