package de.medmanagement.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SpringBootTest
public class UserDataServiceTest {

    UserDataService userDataService;
    DrugDTO drugDataA;
    DrugDTO drugDataB;

    @Before
    public void setUp() throws Exception {
        userDataService = new UserDataService();
        drugDataA = new DrugDTO(new Drug("MedA", 250, new Date((new Date()).getTime()-96*60*60*1000), 1,0,1, false, 100, null, "TestUser"));
        drugDataB = new DrugDTO(new Drug("MedB", 62, new Date((new Date()).getTime()-96*60*60*1000), 0.5f,0,0.5f, false, 100,null, "TestUser"));
    }

    @After
    public void tearDown() throws Exception {
        userDataService = null;
        drugDataA = null;
        drugDataB = null;
    }

    @Test
    public void addDrug() {
        userDataService.createDrug(drugDataA, "cbo");

        assertTrue(userDataService.containsDrug(drugDataA.getName(), "cbo"));
        assertFalse(userDataService.containsDrug(drugDataB.getName(), "cbo"));
    }

    @Test
    public void removeDrug() {
        userDataService.createDrug(drugDataA, "cbo");
        userDataService.createDrug(drugDataB, "cbo");

        assertTrue(userDataService.containsDrug(drugDataA.getName(), "cbo"));
        assertTrue(userDataService.containsDrug(drugDataB.getName(), "cbo"));

//        userDataService.deleteDrug(drugDataA.getName(), "cbo");

//        assertFalse(userDataService.containsDrug(drugDataA.getName(), "cbo"));
        assertTrue(userDataService.containsDrug(drugDataB.getName(), "cbo"));
    }

    @Test
    public void removeDrugByName() {
        userDataService.createDrug(drugDataA, "cbo");
        userDataService.createDrug(drugDataB, "cbo");

        assertTrue(userDataService.containsDrug(drugDataA.getName(), "cbo"));
        assertTrue(userDataService.containsDrug(drugDataB.getName(), "cbo"));

//        userDataService.deleteDrug(drugDataA.getName(), "userA");

//        assertFalse(userDataService.containsDrug(drugDataA.getName(), "cbo"));
        assertTrue(userDataService.containsDrug(drugDataB.getName(), "cbo"));
    }

    /*
    @Test
    public void testToString() {
        drugPlan.addDrug(drugA);
        drugPlan.addDrug(drugB);

        StringBuilder expectedOutput = new StringBuilder();
        expectedOutput.append("MedA Thu Jun 25 12:31:06 CEST 2020 250 1.0|0.0|1.0 -> 121\n");
        expectedOutput.append("MedB Thu Jun 25 12:31:06 CEST 2020 62 0.5|0.0|0.5 -> 58");

        System.out.println(expectedOutput.toString());
        System.out.println(drugPlan);

        assertTrue(expectedOutput.toString().equals(drugPlan));
    }
    */
}
