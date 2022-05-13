package dao;

import models.Foodtype;
import models.Restaurant;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class Sql2oRestaurantDaoTest {
    private Connection conn;
    String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
    Sql2o sql2o = new Sql2o(connectionString, "", "");
    private Sql2oRestaurantDao restaurantDao = new Sql2oRestaurantDao(sql2o);
    private Sql2oFoodtypeDao foodtypeDao = new Sql2oFoodtypeDao(sql2o);
    private Sql2oReviewDao reviewDao;

    @Before
    public void setUp() throws Exception {
        try {
            restaurantDao = new Sql2oRestaurantDao(sql2o);
            foodtypeDao = new Sql2oFoodtypeDao(sql2o);
            reviewDao = new Sql2oReviewDao(sql2o);
            conn = sql2o.open();

        } catch (Exception e){

        }


    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void addingFoodSetsId() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        assertNotEquals(0, testRestaurant.getId());
    }

    @Test
    public void addedRestaurantsAreReturnedFromGetAll() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        assertEquals(1, restaurantDao.getAll().size());
    }

    @Test
    public void noRestaurantsReturnsEmptyList() throws Exception {
        assertEquals(0, restaurantDao.getAll().size());
    }

//    @Test
//    public void findByIdReturnsCorrectRestaurant() throws Exception {
//        Restaurant testRestaurant = setupRestaurant();
////        Restaurant otherRestaurant = setupRestaurant();
//        restaurantDao.add(testRestaurant);
//        Restaurant otherRestaurant; //retrieve
//        otherRestaurant = restaurantDao.findById(otherRestaurant.getId());
//        assertEquals(testRestaurant, otherRestaurant);
//        assertEquals(otherRestaurant, restaurantDao.findById(otherRestaurant.getId()));
//    }


    @Test//failed
    public void existingTasksCanBeFoundById() throws Exception {
        Restaurant restaurant = new Restaurant("Fish Omena", "214 NE Ngara", "97232", "254-402-9874");
        restaurantDao.add(restaurant); //add to dao (takes care of saving)
        Restaurant foundRestaurant = restaurantDao.findById(restaurant.getId()); //retrieve
        assertEquals(restaurant, foundRestaurant); //should be the same
    }



    @Test
    public void updateCorrectlyUpdatesAllFields() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.update(testRestaurant.getId(), "a", "b", "c", "d", "e", "f");
        Restaurant foundRestaurant = restaurantDao.findById(testRestaurant.getId());
        assertEquals("a", foundRestaurant.getName());
        assertEquals("b", foundRestaurant.getAddress());
        assertEquals("c", foundRestaurant.getZipcode());
        assertEquals("d", foundRestaurant.getPhone());
        assertEquals("e", foundRestaurant.getWebsite());
        assertEquals("f", foundRestaurant.getEmail());
    }

    @Test
    public void deleteByIdDeletesCorrectRestaurant() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        Restaurant otherRestaurant = setupRestaurant();
        restaurantDao.deleteById(testRestaurant.getId());
        assertEquals(0, restaurantDao.getAll().size());
    }

    @Test
    public void clearAll() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        Restaurant otherRestaurant = setupRestaurant();
        restaurantDao.clearAll();
        assertEquals(0, restaurantDao.getAll().size());
    }

    @Test//failed
    public void RestaurantReturnsFoodtypesCorrectly() throws Exception {
        Foodtype testFoodtype  = new Foodtype("Seafood");
        foodtypeDao.add(testFoodtype);

        Foodtype otherFoodtype  = new Foodtype("Bar Food");
        foodtypeDao.add(otherFoodtype);

        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);
        restaurantDao.addRestaurantToFoodtype(testRestaurant,testFoodtype);
        restaurantDao.addRestaurantToFoodtype(testRestaurant,otherFoodtype);

        Foodtype[] foodtypes = {testFoodtype, otherFoodtype}; //oh hi what is this?

        assertEquals(Arrays.asList(foodtypes), restaurantDao.getAllFoodtypesByRestaurant(testRestaurant.getId()));
    }





    //helpers

    public Restaurant setupRestaurant (){
        Restaurant restaurant = new Restaurant("Fish Omena", "214 NE Ngara", "97232", "254-402-9874", "http://fishwitch.com", "hellofishy@fishwitch.com");
        restaurantDao.add(restaurant);
        return restaurant;
    }

    public Restaurant setupAltRestaurant (){
        Restaurant restaurant = new Restaurant("Fish Omena", "214 NE Ngara", "97232", "254-402-9874");
        restaurantDao.add(restaurant);
        return restaurant;
    }
}