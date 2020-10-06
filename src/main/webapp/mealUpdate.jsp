<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="ru">
<head>
    <title>Meals</title>
    <hr>
    <h2>Meal changing</h2>
    <h3><a href="index.html">Home</a></h3>
</head>
<body>
<style type='text/css'>
    label {
        display: inline-block;
        width: 140px;
        text-align: right;
    }
</style>
<form method="POST" action='meals' name="frmUpdateMeal">
    <input type="hidden" id="mealId" name="mealId" value="${meal.id}">
    <label for="dateTime">Date|Time : </label>
    <input type="datetime" id="dateTime" name="dateTime"
           value="${meal.dateTime == null ? '' : localDateTimeFormat.format(meal.dateTime)}"/> <br/>
    <label for="description">Description : </label>
    <input type="text" id="description" name="description" value="${meal.description}"/> <br/>
    <label for="calories">Calories : </label>
    <input type="number" id="calories" name="calories" value="${meal.calories}"/> <br/>
    <input type="submit" value="Save"/>
</form>
</body>
</html>