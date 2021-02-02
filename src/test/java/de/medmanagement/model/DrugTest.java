package de.medmanagement.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DrugTest {

    @BeforeEach
    public void setUp() throws Exception {
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    public void getDaysOff() {
        // set last count update 4 days back
        int daysInMilliSeconds = 96*60*60*1000;

        Drug drugA = new Drug("MedA", 250, new Date((new Date()).getTime()-daysInMilliSeconds), 1,0,1, false, 100,null, "TestUser");
        // System.out.println(drugA.getDaysOff());
        assertTrue(drugA.getDaysOff() == daysInMilliSeconds/1000/60/60/24);
    }

    @Test
    public void getDaysLeft() {
        // set last count update 4 days back
        int daysInMilliSeconds = 96*60*60*1000;

        Drug drugA = new Drug("MedA", 250, new Date((new Date()).getTime()-daysInMilliSeconds), 1,0,1, false, 100,null, "TestUser");
        // System.out.println(drugA.getDaysLeft());
        assertTrue(drugA.getDaysLeft() == 121);
    }

    @Test
    public void testToString() {
        // set last count update 4 days back
        int daysInMilliSeconds = 96*60*60*1000;
        Date lastCountUpdate = new Date((new Date()).getTime()-daysInMilliSeconds);

        Drug drugA = new Drug("MedA", 250, lastCountUpdate, 1,0,1, false, 100,null, "TestUser");
        // System.out.println(drugA);
        assertTrue(drugA.toString().equals("MedA 1.0|0.0|1.0 : 4 -> 121"));
    }
}