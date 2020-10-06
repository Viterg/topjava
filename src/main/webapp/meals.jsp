<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="ru">
<head>
    <title>Meals</title>
    <h2>Meals</h2>
    <h3><a href="index.html">Home</a></h3>
</head>
<body>
<table border="1">
    <thead>
    <tr>
        <th>Date/time</th>
        <th>Description</th>
        <th>Calories</th>
        <th colspan=2>Action</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${requestScope.meals}" var="meal">
        <c:set var="rowColor" value="${meal.excess == true ? 'red' : 'green'}"/>
        <tr style="color:${rowColor}">
            <td>${localDateTimeFormat.format(meal.dateTime)}</td>
            <td>${meal.description}</td>
            <td>${meal.calories}</td>
            <td><a href="meals?action=edit&mealId=<c:out value="${meal.id}" />">Update</a></td>
            <td><a href="meals?action=delete&mealId=<c:out value="${meal.id}" />">Delete</a></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<p><a href="meals?action=insert">Add meal</a></p>
</body>
</html>