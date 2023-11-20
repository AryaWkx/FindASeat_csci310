package com.example.findaseat_csci310;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfilePageTest {

    @Rule
    public IntentsTestRule<UserActivity> intentsTestRule = new IntentsTestRule<>(UserActivity.class);

    @Before
    public void setUp() throws Exception {
        FirebaseDatabase root;
        DatabaseReference reference;
        root = FirebaseDatabase.getInstance();
        reference = root.getReference();
        User u = new User("email@gmail.com", "1000000001", "test", "Student", "12345678");
        Reservation r1 = new Reservation("Fertitta Hall (JFF)", 4, 5, "indoor");
        Reservation r2 = new Reservation("Taper Hall (THH)", 9, 10, "outdoor");
        Reservation r3 = new Reservation("Taper Hall (THH)", 11, 13, "outdoor");
        Reservation r4 = new Reservation("Leavey Library (LVL)", 4, 5, "indoor");
        u.history.add(r2);
        u.history.add(r3);
        u.history.add(r4);
        u.currentReservation = r1;
        reference.child("Users").child("1000000001").setValue(u);
    }

    @After
    public void tearDown() throws Exception {
        FirebaseDatabase root;
        DatabaseReference reference;
        root = FirebaseDatabase.getInstance();
        reference = root.getReference();
        reference.child("Users").child("1000000001").removeValue();
    }

    @Test
    public void testCancel() throws InterruptedException {
        Thread.sleep(1000);

        // Click on the cancel button
        onView(withId(R.id.cancel)).perform(click());

        // Click on the yes button in the dialog
        onView(withText("Yes")).perform(click());
        Thread.sleep(500);

        // Check if the text in the current reservation shows no current reservation
        onView(withId(R.id.activated_reserved_buildingname)).check(matches(withText("No Current Reservation")));

        // Check if the text in the first reservation shows the correct building name
        onView(withId(R.id.building_name1)).check(matches(withText("Fertitta Hall (JFF)")));
    }

    @Test
    public void testManageWithTooMuchTime() throws InterruptedException {
        Thread.sleep(2000);

        // Click on the manage button
        onView(withId(R.id.manage)).perform(click());
        Thread.sleep(1000);

        // Click on the text views to select five slots
        onView(withText("9:00-9:29")).perform(click());
        onView(withText("9:30-9:59")).perform(click());
        onView(withText("10:00-10:29")).perform(click());
        onView(withText("10:30-10:59")).perform(click());
        onView(withText("11:00-11:29")).perform(click());

        // Click on the confirm button
        onView(withId(R.id.confirmButton)).perform(click());

        // Check if the error message shows up
        onView(withText("Invalid Timeslot Selected"))
                .inRoot(isDialog()) // Use isDialog() to ensure the view is in a dialog
                .check(matches(isDisplayed()));
        Thread.sleep(5000);
    }

    @Test
    public void testManageWithNonconsecutiveTime() throws InterruptedException {
        Thread.sleep(2000);

        // Click on the manage button
        onView(withId(R.id.manage)).perform(click());
        Thread.sleep(1000);

        // Click on the text views to select five slots
        onView(withText("9:00-9:29")).perform(click());
        onView(withText("9:30-9:59")).perform(click());
        onView(withText("12:00-12:29")).perform(click());


        // Click on the confirm button
        onView(withId(R.id.confirmButton)).perform(click());

        // Check if the error message shows up
        onView(withText("Invalid Timeslot Selected"))
                .inRoot(isDialog()) // Use isDialog() to ensure the view is in a dialog
                .check(matches(isDisplayed()));
        Thread.sleep(5000);
    }

    @Test
    public void testManageWithValidTime() throws InterruptedException {
        Thread.sleep(2000);

        // Click on the manage button
        onView(withId(R.id.manage)).perform(click());
        Thread.sleep(1000);

        // Click on the text views to select five slots
        onView(withText("9:00-9:29")).perform(click());
        onView(withText("9:30-9:59")).perform(click());

        // Click on the confirm button
        onView(withId(R.id.confirmButton)).perform(click());

        // Check if current message shows up
        onView(withId(R.id.activated_reserved_buildingname)).check(matches(withText("Fertitta Hall (JFF)")));
        onView(withId(R.id.activated_reserved_indoor)).check(matches(withText("indoor")));
        onView(withId(R.id.activated_reserved_time)).check(matches(withText("9:00 - 9:59")));
    }

    @Test
    public void testDataConsistencyAfterChange() throws InterruptedException {
        Thread.sleep(500);

        // Click on the cancel button
        onView(withId(R.id.cancel)).perform(click());
        onView(withText("Yes")).perform(click());
        Thread.sleep(1000);

        // Go to the map page
        onView(withId(R.id.bottom_map)).perform(click());
        Thread.sleep(100);

        // Go back to the profile page
        onView(withId(R.id.bottom_profile)).perform(click());
        Thread.sleep(100);
        // Check if the text in the current reservation shows no current reservation
        onView(withId(R.id.activated_reserved_buildingname)).check(matches(withText("No Current Reservation")));

        // Check if the text in the first reservation shows the correct building name
        onView(withId(R.id.building_name1)).check(matches(withText("Fertitta Hall (JFF)")));
    }
}
