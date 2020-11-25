## REST API Test Commands
### Get all meals
curl -X GET -H 'Accept: application/json' -i http://localhost:8080/topjava/meals
### Get meal by id
curl -X GET -H 'Accept: application/json' -i http://localhost:8080/topjava/meals/100002
### Get filtered meals
curl -X GET -H 'Accept: application/json' -i http://localhost:8080/topjava/meals/filter?startDate=2020-01-30&startTime=09:00:00&endDate=2020-01-30&endTime=11:00:00
### Update meal by id
curl -X PUT -H 'Content-Type: application/json' -i http://localhost:8080/topjava/meals/100002 --data '{
    "id":100002,
    "dateTime":"2020-01-30T10:02:00",
    "description":"Обновленный завтрак",
    "calories":200
}'
### Delete meal by id
curl -X DELETE -i http://localhost:8080/topjava/meals/100002
### Create new meal
curl -X POST -H 'Content-Type: application/json' -i http://localhost:8080/topjava/meals --data '{
    "dateTime":"2020-02-01T18:00:00",
    "description":"Созданный ужин",
    "calories":300
}'