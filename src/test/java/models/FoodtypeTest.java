package models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FoodtypeTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void getName() {
        Foodtype testFoodtype = setupFoodtype();
        assertEquals("dessert", testFoodtype.getName());
    }

    @Test
    public void setName() {
        Foodtype testFoodtype = setupFoodtype();
        testFoodtype.setName("breakfast");
        assertNotEquals("dessert", testFoodtype.getName());
    }

    @Test
    public void setId() {
        Foodtype testFoodtype = setupFoodtype();
        testFoodtype.setId(5);
        assertEquals(5, testFoodtype.getId());
    }

    // helper
    public Foodtype setupFoodtype(){
        return new Foodtype("dessert");
    }
}