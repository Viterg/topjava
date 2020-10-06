<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
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
    <c:out value="${meal.id}"/>
    <label for="dateTime">Date|Time : </label>
    <input type="datetime-local" id="dateTime" name="dateTime" value="<fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${meal.dateTime}" />"/> <br/>
    <label for="description">Description : </label>
    <input type="text" id="description" name="description" value="<c:out value="${meal.description}" />"/> <br/>
    <label for="calories">Calories : </label>
    <input type="text" id="calories" name="calories" value="<c:out value="${meal.calories}" />"/> <br/>
    <input type="submit" value="Save"/>
</form>
</body>
</html>
