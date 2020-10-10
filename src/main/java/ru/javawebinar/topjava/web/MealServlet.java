package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);
    private MealRestController controller;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try (ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml")) {
            System.out.println("Bean definition names: " + Arrays.toString(appCtx.getBeanDefinitionNames()));
            controller = appCtx.getBean(MealRestController.class);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action == null ? "all" : action) {
            case "delete":
                controller.delete(getId(request));
                response.sendRedirect("meals");
                break;
            case "create":
                LocalDateTime roundedNow = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
                request.setAttribute("meal", new Meal(roundedNow, "", 1000));
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "update":
                request.setAttribute("meal", controller.get(getId(request)));
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "all":
            default:
                List<MealTo> meals = MealsUtil.getTos(controller.getAll(), MealsUtil.DEFAULT_CALORIES_PER_DAY);
                request.setAttribute("meals", meals);
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String filter = request.getParameter("filter");
        if (Boolean.parseBoolean(filter)) {
            log.info("Filter meals");
            LocalDate startDate = LocalDate.parse(request.getParameter("startDate"));
            LocalDate endDate = LocalDate.parse(request.getParameter("endDate"));
            LocalTime startTime = LocalTime.parse(request.getParameter("startTime"));
            LocalTime endTime = LocalTime.parse(request.getParameter("endTime"));

            List<Meal> daysFiltredMeals = controller.getAll().stream().filter(
                    meal -> meal.getDate().isAfter(startDate) && meal.getDate().isBefore(endDate)).collect(
                    Collectors.toList());
            List<MealTo> filtered = MealsUtil.getFilteredTos(daysFiltredMeals, MealsUtil.DEFAULT_CALORIES_PER_DAY, startTime, endTime);
            request.setAttribute("meals", filtered);
            request.getRequestDispatcher("/meals.jsp").forward(request, response);
        } else {
            String id = request.getParameter("id");
            Meal meal = new Meal(id.isEmpty() ? null : Integer.valueOf(id),
                                 LocalDateTime.parse(request.getParameter("dateTime")),
                                 request.getParameter("description"),
                                 Integer.parseInt(request.getParameter("calories")));
            controller.save(meal);
            response.sendRedirect("meals");
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}