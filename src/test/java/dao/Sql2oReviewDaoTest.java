package dao;

import models.Restaurant;
import models.Review;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static org.junit.jupiter.api.Assertions.*;

public class Sql2oReviewDaoTest {
    private Connection conn;
    String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
    Sql2o sql2o = new Sql2o(connectionString, "", "");
    private Sql2oReviewDao reviewDao = new Sql2oReviewDao(sql2o);
    private Sql2oRestaurantDao restaurantDao = new Sql2oRestaurantDao(sql2o);

    @BeforeEach
    public void setUp() throws Exception {


        reviewDao = new Sql2oReviewDao(sql2o);
        restaurantDao = new Sql2oRestaurantDao(sql2o);
        conn = sql2o.open();
    }

    @AfterEach
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void addingReviewSetsId() throws Exception {
        Review testReview = setupReview();
        assertEquals(1, testReview.getId());
    }

    @Test
    public void getAll() throws Exception {
        Review review1 = setupReview();
        Review review2 = setupReview();
        assertEquals(2, reviewDao.getAll().size());
    }

    @Test
    public void getAllReviewsByRestaurant() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        Restaurant otherRestaurant = setupRestaurant(); //add in some extra data to see if it interferes
        Review review1 = setupReviewForRestaurant(testRestaurant);
        Review review2 = setupReviewForRestaurant(testRestaurant);
        Review reviewForOtherRestaurant = setupReviewForRestaurant(otherRestaurant);
        assertEquals(2, reviewDao.getAllReviewsByRestaurant(testRestaurant.getId()).size());
    }

    @Test
    public void deleteById() throws Exception {
        Review testReview = setupReview();
        Review otherReview = setupReview();
        assertEquals(2, reviewDao.getAll().size());
        reviewDao.deleteById(testReview.getId());
        assertEquals(1, reviewDao.getAll().size());
    }

    @Test
    public void clearAll() throws Exception {
        Review testReview = setupReview();
        Review otherReview = setupReview();
        reviewDao.clearAll();
        assertEquals(0, reviewDao.getAll().size());
    }


    @Test
    public void timeStampIsReturnedCorrectly() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);
        Review testReview = new Review("Captain Kirk", "foodcoma!", 3, testRestaurant.getId());
        reviewDao.add(testReview);

        long creationTime = testReview.getCreatedat();
        long savedTime = reviewDao.getAll().get(0).getCreatedat();
        String formattedCreationTime = testReview.getFormattedCreatedAt();
        String formattedSavedTime = reviewDao.getAll().get(0).getFormattedCreatedAt();
        assertEquals(formattedCreationTime,formattedSavedTime);
        assertEquals(creationTime, savedTime);
    }



    //helpers

    public Review setupReview() {
        Review review = new Review("great", "Kim", 4, 555);
        reviewDao.add(review);
        return review;
    }

    public Review setupReviewForRestaurant(Restaurant restaurant) {
        Review review = new Review("great", "Kim", 4, restaurant.getId());
        reviewDao.add(review);
        return review;
    }

    public Restaurant setupRestaurant() {
        Restaurant restaurant = new Restaurant("Fish Witch", "214 NE Broadway", "97232", "503-402-9874", "http://fishwitch.com", "hellofishy@fishwitch.com");
        restaurantDao.add(restaurant);
        return restaurant;
    }
}