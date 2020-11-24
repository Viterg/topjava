package ru.javawebinar.topjava.web.meal;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.datetime.DateFormat;
import ru.javawebinar.topjava.util.datetime.TimeFormat;

import java.net.URI;
import java.time.*;
import java.util.List;

@RestController
@RequestMapping(value = MealRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class MealRestController extends AbstractMealController {

    static final String REST_URL = "/meals";

    @Override
    @GetMapping
    public List<MealTo> getAll() {
        return super.getAll();
    }

    @Override
    @GetMapping("/{id}")
    public Meal get(@PathVariable int id) {
        return super.get(id);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Meal> createWithLocation(@RequestBody Meal meal) {
        Meal created = create(meal);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                          .path(REST_URL + "/{id}")
                                                          .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Override
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody Meal meal, @PathVariable int id) {
        super.update(meal, id);
    }

    @GetMapping("/between")
    public List<MealTo> getBetween(
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam LocalDateTime startDateTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam LocalDateTime endDateTime) {
        return getBetween(startDateTime.toLocalDate(), startDateTime.toLocalTime(), endDateTime.toLocalDate(),
                          endDateTime.toLocalTime());
    }

    @GetMapping("/filter")
    public List<MealTo> getFiltered(@DateFormat @RequestParam LocalDate startDate,
                                    @DateFormat @RequestParam LocalDate endDate,
                                    @TimeFormat @RequestParam LocalTime startTime,
                                    @TimeFormat @RequestParam LocalTime endTime) {
        return getBetween(startDate, startTime, endDate, endTime);
    }
}