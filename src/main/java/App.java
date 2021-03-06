import com.google.gson.Gson;
import dao.FoodtypeDao;
import dao.Sql2oFoodtypeDao;
import dao.Sql2oRestaurantDao;
import dao.Sql2oReviewDao;
import exceptions.ApiException;
import models.Foodtype;
import models.Restaurant;
import models.Review;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class App {

    public static void main(String[] args) {

        Sql2oFoodtypeDao foodtypeDao;
        Sql2oRestaurantDao restaurantDao;
        Sql2oReviewDao reviewDao;
        Connection conn;
        Gson gson = new Gson();


        staticFileLocation("/public");
//        String connectionString = "jdbc:h2:~/jadle.db;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
//        Sql2o sql2o = new Sql2o(connectionString, "", "");

        String connectionString = "jdbc:postgresql://localhost:5432/jadle";
        Sql2o sql2o = new Sql2o(connectionString, "ymelda", "chocolate");

        restaurantDao = new Sql2oRestaurantDao(sql2o);
        foodtypeDao = new Sql2oFoodtypeDao(sql2o);
        reviewDao = new Sql2oReviewDao(sql2o);
        conn = sql2o.open();
        //CREATE

        //not found
        post("/restaurants/:restaurantId/foodtype/:foodtypeId", "application/json", (req, res) -> {

            int restaurantId = Integer.parseInt(req.params("restaurantId"));
            int foodtypeId = Integer.parseInt(req.params("foodtypeId"));
            Restaurant restaurant = restaurantDao.findById(restaurantId);
            Foodtype foodtype = foodtypeDao.findById(foodtypeId);


            if (restaurant != null && foodtype != null) {
                //both exist and can be associated
                foodtypeDao.addFoodtypeToRestaurant(foodtype, restaurant);
                res.status(201);
                return gson.toJson(String.format("Restaurant '%s' and Foodtype '%s' have been associated", foodtype.getName(), restaurant.getName()));
            } else {
                throw new ApiException(404, String.format("Restaurant or Foodtype does not exist"));
            }
        });

        get("/restaurants/:id/foodtypes", "application/json", (req, res) -> {
            int restaurantId = Integer.parseInt(req.params("id"));
            Restaurant restaurantToFind = restaurantDao.findById(restaurantId);
            if (restaurantToFind == null) {
                throw new ApiException(404, String.format("No restaurant with the id: \"%s\" exists", req.params("id")));
            } else if (restaurantDao.getAllFoodtypesByRestaurant(restaurantId).size() == 0) {
                return "{\"message\":\"I'm sorry, but no foodtypes are listed for this restaurant.\"}";
            } else {
                return gson.toJson(restaurantDao.getAllFoodtypesByRestaurant(restaurantId));
            }
        });

        get("/foodtypes/:id/restaurants", "application/json", (req, res) -> {
            int foodtypeId = Integer.parseInt(req.params("id"));
            Foodtype foodtypeToFind = foodtypeDao.findById(foodtypeId);
            if (foodtypeToFind == null) {
                throw new ApiException(404, String.format("No foodtype with the id: \"%s\" exists", req.params("id")));
            } else if (foodtypeDao.getAllRestaurantsForAFoodtype(foodtypeId).size() == 0) {
                return "{\"message\":\"I'm sorry, but no restaurants are listed for this foodtype.\"}";
            } else {
                return gson.toJson(foodtypeDao.getAllRestaurantsForAFoodtype(foodtypeId));
            }
        });


        post("/restaurants/:restaurantId/reviews/new", "application/json", (req, res) -> {
            int restaurantId = Integer.parseInt(req.params("restaurantId"));
            Review review = gson.fromJson(req.body(), Review.class);
            review.setCreatedat();
            review.setFormattedCreatedAt();
            review.setRestaurantId(restaurantId); //we need to set this separately because it comes from our route, not our JSON input.
            reviewDao.add(review);
            res.status(201);
            return gson.toJson(review);
        });

        post("/foodtypes/new", "application/json", (req, res) -> {
            Foodtype foodtype = gson.fromJson(req.body(), Foodtype.class);
            foodtypeDao.add(foodtype);
            res.status(201);
            return gson.toJson(foodtype);
        });

        //READ
        get("/restaurants", "application/json", (req, res) -> {
            System.out.println(restaurantDao.getAll());

            if (restaurantDao.getAll().size() > 0) {
                return gson.toJson(restaurantDao.getAll());
            } else {
                return "{\"message\":\"I'm sorry, but no restaurants are currently listed in the database.\"}";
            }

        });

        get("/restaurants/:id", "application/json", (req, res) -> { //accept a request in format JSON from an app
            int restaurantId = Integer.parseInt(req.params("id"));
            Restaurant restaurantToFind = restaurantDao.findById(restaurantId);
            if (restaurantToFind == null) {
                throw new ApiException(404, String.format("No restaurant with the id: \"%s\" exists", req.params("id")));
            }
            return gson.toJson(restaurantToFind);
        });

        get("/restaurants/:id/reviews", "application/json", (req, res) -> {
            int restaurantId = Integer.parseInt(req.params("id"));

            Restaurant restaurantToFind = restaurantDao.findById(restaurantId);
            List<Review> allReviews;

            if (restaurantToFind == null) {
                throw new ApiException(404, String.format("No restaurant with the id: \"%s\" exists", req.params("id")));
            }

            allReviews = reviewDao.getAllReviewsByRestaurant(restaurantId);

            return gson.toJson(allReviews);
        });

        get("/foodtypes", "application/json", (req, res) -> {
            return gson.toJson(foodtypeDao.getAll());
        });


        //CREATE
        post("/restaurants/new", "application/json", (req, res) -> {
            Restaurant restaurant = gson.fromJson(req.body(), Restaurant.class);
            restaurantDao.add(restaurant);
            res.status(201);
            return gson.toJson(restaurant);
        });

//        FILTERS
        exception(ApiException.class, (exception, req, res) -> {// generated an exception object
            ApiException err = exception;//this object
            Map<String, Object> jsonMap = new HashMap<>();//Make a new hashmap to store some information - as hashmap is the closest data structure we have to a JSON object as it is key/value pairs.
            jsonMap.put("status", err.getStatusCode());//Add in the status as a key, and the code as the value to show it to our user.
            jsonMap.put("errorMessage", err.getMessage());//Add the error message as a key, and the code as the value to show it to our user
            res.type("application/json");//Set the output. We need to do this here explicitly, as our after filter does not run in the event of an exception.(remember: we are using our after filter to automatically set all outputs to data type: JSON.)</string,>
            res.status(err.getStatusCode());//set the status
            res.body(gson.toJson(jsonMap)); //set the output.
        });


        after((req, res) -> {
            res.type("application/json");
        });

    }
}
