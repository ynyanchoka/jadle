package dao;

import models.Foodtype;
import models.Restaurant;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.ArrayList;
import java.util.List;

public class Sql2oFoodtypeDao implements FoodtypeDao{
    private final Sql2o sql2o;
    public Sql2oFoodtypeDao(Sql2o sql2o){ this.sql2o = sql2o; } //making the sql2o object available everywhere so we can call methods in it

    @Override
    public void add(Foodtype foodtype) {
        String sql = "INSERT INTO foodtypes (name) VALUES (:name)";
        try(Connection con = sql2o.open()){ //try to open a connection
            int id = (int) con.createQuery(sql, true) //make a new variable
                    .bind(foodtype) //map my argument onto the query, so we can use information from it
                    .executeUpdate()//run it all
                    .getKey();//int id is now the row number (row “key”) of db
            foodtype.setId(id);//update object to set id now from database
        } catch (Sql2oException ex) {
            System.out.println(ex);// error!
        }
    }

    @Override
    public List<Foodtype> getAll() {
        try(Connection con = sql2o.open()){
            return con.createQuery("SELECT * FROM foodtypes")
                    .executeAndFetch(Foodtype.class);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE from foodtypes WHERE id=:id";
        String deleteJoin = "DELETE from restaurants_foodtypes WHERE foodtypeid = :foodtypeId";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("id", id) //key/value pair, key must match above
                    .executeUpdate();  //fetch an individual item

            con.createQuery(deleteJoin)
                    .addParameter("foodtypeId", id)
                    .executeUpdate();
        } catch (Sql2oException ex){
            System.out.println(ex);
        }
    }

    @Override
    public void clearAll() {
        String sql = "DELETE from foodtypes";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql).executeUpdate();
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void addFoodtypeToRestaurant(Foodtype foodtype, Restaurant restaurant){
        String sql = "INSERT INTO restaurants_foodtypes (restaurantid, foodtypeid) VALUES (:restaurantId, :foodtypeId)";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("restaurantId", restaurant.getId())
                    .addParameter("foodtypeId", foodtype.getId())
                    .executeUpdate();
        } catch (Sql2oException ex){
            System.out.println(ex);
        }
    }



    @Override
    public List<Restaurant> getAllRestaurantsForAFoodtype(int foodtypeId) {

        ArrayList<Restaurant> restaurants = new ArrayList<>();

        String joinQuery = "SELECT restaurantid FROM restaurants_foodtypes WHERE foodtypeid = :foodtypeId";

        try (Connection con = sql2o.open()) {
            List<Integer> allRestaurantIds = con.createQuery(joinQuery)
                    .addParameter("foodtypeId", foodtypeId)
                    .executeAndFetch(Integer.class); //what is happening in the lines above?
            for (Integer restaurantId : allRestaurantIds){
                String restaurantQuery = "SELECT * FROM restaurants WHERE id = :restaurantId";
                restaurants.add(
                        con.createQuery(restaurantQuery)
                                .addParameter("restaurantId", restaurantId)
                                .executeAndFetchFirst(Restaurant.class));
            } //why are we doing a second sql query - set?
        } catch (Sql2oException ex){
            System.out.println(ex);
        }
        return restaurants;
    }

    @Override
    public Foodtype findById(int id) {
        try(Connection con = sql2o.open()){
            return con.createQuery("SELECT * FROM foodtypes WHERE id = :id")
                    .addParameter("id", id)
                    .executeAndFetchFirst(Foodtype.class);
        }
    }

}