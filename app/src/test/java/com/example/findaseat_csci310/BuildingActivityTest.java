package com.example.findaseat_csci310;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Vector;

@RunWith(MockitoJUnitRunner.class)
public class BuildingActivityTest {

    @Mock
    private BuildingActivity myActivity;

    @Before
    public void setUp() {
        myActivity = new BuildingActivity();
    }

    @Test
    public void testBuildingConsistency() {
        Building b = new Building();

        assertTrue(b.name.equals("Doheny Memorial Library (DML)"));
        assertTrue(b.lat == 0.0);
        assertTrue(b.lng == 0.0);
        assertTrue(b.indoor_avail.size() == 26);
        assertTrue(b.outdoor_avail.size() == 26);
    }

    @Test
    public void testCheckNoReservation() {
        myActivity.selectedTimeSlots = new Vector<TimeSlot>();
        myActivity.selectedTimeSlots.clear();
        assertTrue(myActivity.isSelectedEmpty());
        assertTrue(myActivity.isValid() == 1);
    }

    @Test
    public void testCheckSelectionAvailable() {
        myActivity.selectedTimeSlots = new Vector<TimeSlot>();
        myActivity.selectedTimeSlots.clear();
        myActivity.selectedTimeSlots.add(new TimeSlot("8:00-8:29", "Indoor", 10, 0));
        myActivity.selectedTimeSlots.add(new TimeSlot("8:30-8:59", "Indoor", 10, 1));
        assertTrue(myActivity.isSelectedAvailable());

        myActivity.selectedTimeSlots.clear();
        myActivity.selectedTimeSlots.add(new TimeSlot("8:00-8:29", "Indoor", 0, 0));
        myActivity.selectedTimeSlots.add(new TimeSlot("8:30-8:59", "Indoor", 10, 1));
        assertFalse(myActivity.isSelectedAvailable());
        assertTrue(myActivity.isValid() == 2);
    }

    @Test
    public void testCheckSelectionConsecutive() {
        myActivity.selectedTimeSlots = new Vector<TimeSlot>();
        myActivity.selectedTimeSlots.clear();
        myActivity.selectedTimeSlots.add(new TimeSlot("8:00-8:29", "Indoor", 10, 0));
        myActivity.selectedTimeSlots.add(new TimeSlot("8:30-8:59", "Indoor", 10, 1));
        assertTrue(myActivity.isSelectedConsecutive());

        myActivity.selectedTimeSlots.clear();
        myActivity.selectedTimeSlots.add(new TimeSlot("8:00-8:29", "Indoor", 10, 0));
        myActivity.selectedTimeSlots.add(new TimeSlot("9:00-9:29", "Indoor", 10, 2));
        assertFalse(myActivity.isSelectedConsecutive());
        assertTrue(myActivity.isValid() == 3);
    }

    @Test
    public void testCheckSelectionType() {
        myActivity.selectedTimeSlots = new Vector<TimeSlot>();
        myActivity.selectedTimeSlots.clear();
        myActivity.selectedTimeSlots.add(new TimeSlot("8:00-8:29", "Indoor", 10, 0));
        myActivity.selectedTimeSlots.add(new TimeSlot("8:30-8:59", "Indoor", 10, 1));
        assertTrue(myActivity.isSelectedSameType());

        myActivity.selectedTimeSlots.clear();
        myActivity.selectedTimeSlots.add(new TimeSlot("8:00-8:29", "Indoor", 10, 25));
        myActivity.selectedTimeSlots.add(new TimeSlot("8:30-8:59", "Outdoor", 10, 26));
        assertFalse(myActivity.isSelectedSameType());
        assertTrue(myActivity.isValid() == 4);
    }

    @Test
    public void testCheckSelectionNumber() {
        myActivity.selectedTimeSlots = new Vector<TimeSlot>();
        myActivity.selectedTimeSlots.clear();
        myActivity.selectedTimeSlots.add(new TimeSlot("8:00-8:29", "Indoor", 10, 0));
        myActivity.selectedTimeSlots.add(new TimeSlot("8:30-8:59", "Indoor", 10, 1));
        myActivity.selectedTimeSlots.add(new TimeSlot("9:00-9:29", "Indoor", 10, 2));
        myActivity.selectedTimeSlots.add(new TimeSlot("9:30-9:59", "Indoor", 10, 3));
        assertTrue(myActivity.isSelectedLessThan4());

        myActivity.selectedTimeSlots.clear();
        myActivity.selectedTimeSlots.add(new TimeSlot("8:00-8:29", "Indoor", 10, 0));
        myActivity.selectedTimeSlots.add(new TimeSlot("8:30-8:59", "Indoor", 10, 1));
        myActivity.selectedTimeSlots.add(new TimeSlot("9:00-9:29", "Indoor", 10, 2));
        myActivity.selectedTimeSlots.add(new TimeSlot("9:30-9:59", "Indoor", 10, 3));
        myActivity.selectedTimeSlots.add(new TimeSlot("10:00-10:29", "Indoor", 10, 4));
        assertFalse(myActivity.isSelectedLessThan4());
        assertTrue(myActivity.isValid() == 5);
    }

    @Test
    public void testCheckActiveReservation() {
        User u = new User("email", "id", "name", "affiliation", "pwd");
        Reservation cr = new Reservation("cur_building", 0, 2, "indoor");
        u.currentReservation = cr;
        myActivity.user = u;

        assertTrue(myActivity.isActiveReservation());
    }

    @Test
    public void testCheckReservationTime() {
        Reservation r = new Reservation("cur_building", 0, 1, "indoor");
        assertTrue(r.getTime().equals("8:00 - 8:59"));
        r = new Reservation("cur_building", 0, 2, "indoor");
        assertTrue(r.getTime().equals("8:00 - 9:29"));
    }
}
