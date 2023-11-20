package com.example.findaseat_csci310;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class UserActivityTest {

    @Mock
    private UserActivity myActivity;

    @Before
    public void setUp() {
        myActivity = new UserActivity();
    }

    @Test
    public void testUserConsistency() {
        User u = new User("email", "id", "name", "affiliation", "pwd");

        assertTrue(u.getName().equals("name"));
        assertTrue(u.getUsc_id().equals("id"));
        assertTrue(u.getEmail().equals("email"));
        assertTrue(u.getAffiliation().equals("affiliation"));
        assertTrue(u.getPwd().equals("pwd"));
        assertTrue(u.history.size() == 0);
        assertTrue(u.currentReservation == null);
    }

    @Test
    public void testCancelReservation() {
        User u = new User("email", "id", "name", "affiliation", "pwd");
        Reservation r1 = new Reservation("cur_building", 0, 1, "indoor");
        Reservation r2 = new Reservation("hist_building", 0, 1, "indoor");
        u.currentReservation = r1;
        u.history.add(r2);
        myActivity.user = u;
        myActivity.cancel();
        assertTrue(u.currentReservation == null);
        assertTrue(u.history.size() == 2);
        assertTrue(u.history.get(0).building_name.equals("cur_building"));
        assertTrue(u.history.get(1).building_name.equals("hist_building"));
    }

    @Test
    public void testUpdateNewAvail() {
        // Add dummy current reserved building
        ArrayList<Integer> indoor = new ArrayList<Integer>(26);
        ArrayList<Integer> outdoor = new ArrayList<Integer>(26);
        for (int i=0; i<26; i++){
            indoor.add(10);
            outdoor.add(2);
        }
        Building b = new Building("cur_building", 34.0222316, -118.2845691, indoor, outdoor);
        myActivity.building = b;

        // Add dummy user
        User u = new User("email", "id", "name", "affiliation", "pwd");
        Reservation r1 = new Reservation("cur_building", 0, 2, "indoor");
        Reservation r2 = new Reservation("hist_building", 0, 1, "indoor");
        u.currentReservation = r1;
        u.history.add(r2);
        myActivity.user = u;

        // Performe addOldAvail() function
        myActivity.addOldAvail();

        // The original occupied slots should be released
        assertTrue(myActivity.building.indoor_avail.get(0) == 11);
        assertTrue(myActivity.building.indoor_avail.get(1) == 11);
        assertTrue(myActivity.building.indoor_avail.get(2) == 11);
    }

    @Test
    public void testUpdateOldAvail() {
        // Add dummy current reserved building
        ArrayList<Integer> indoor = new ArrayList<Integer>(26);
        ArrayList<Integer> outdoor = new ArrayList<Integer>(26);
        for (int i=0; i<26; i++){
            indoor.add(10);
            outdoor.add(2);
        }
        Building b = new Building("cur_building", 34.0222316, -118.2845691, indoor, outdoor);
        myActivity.building = b;

        // Add dummy user
        User u = new User("email", "id", "name", "affiliation", "pwd");
        Reservation r1 = new Reservation("cur_building", 0, 2, "indoor");
        Reservation r2 = new Reservation("hist_building", 0, 1, "indoor");
        u.currentReservation = r1;
        u.history.add(r2);
        myActivity.user = u;

        // Add new time slots after managing current reservation
        myActivity.new_start = 3;
        myActivity.new_end = 4;

        // Performe deleteNewAvail() function
        myActivity.deleteNewAvail();

        // The new occupied slots should be reserved
        assertTrue(myActivity.building.indoor_avail.get(3) == 9);
        assertTrue(myActivity.building.indoor_avail.get(4) == 9);
    }

    @Test
    public void testCheckConsecutiveReservation() {
        ArrayList<Boolean> slots = new ArrayList<>(Collections.nCopies(26, false));
        // Select one slot, should be valid
        slots.set(0, true);
        boolean isValid = myActivity.checkConsecutiveReservation(slots);
        assertTrue(isValid);

        // Select non-consecutive slots, should be invalid
        slots = new ArrayList<>(Collections.nCopies(26, false));
        slots.set(7, true);
        slots.set(5, true);
        isValid = myActivity.checkConsecutiveReservation(slots);
        assertFalse(isValid);
    }

    @Test
    public void testCheckTotalReservation() {
        ArrayList<Boolean> slots = new ArrayList<>(Collections.nCopies(26, false));
        // Select one slot, should be valid
        slots.set(0, true);
        boolean isValid = myActivity.checkTotalReservation(slots);
        assertTrue(isValid);

        // Select five slots, should be invalid
        slots.set(2, true);
        slots.set(1, true);
        slots.set(3, true);
        slots.set(4, true);
        isValid = myActivity.checkTotalReservation(slots);
        assertFalse(isValid);

        // Select non-consecutive slots, should be invalid
        slots = new ArrayList<>(Collections.nCopies(26, false));
        slots.set(7, true);
        slots.set(5, true);
        isValid = myActivity.checkTotalReservation(slots);
        assertTrue(isValid);
    }
}
