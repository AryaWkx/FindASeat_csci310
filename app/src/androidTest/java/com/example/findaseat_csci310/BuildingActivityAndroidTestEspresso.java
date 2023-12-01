package com.example.findaseat_csci310;


import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;


import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class BuildingActivityAndroidTestEspresso {
    @Rule
    public ActivityScenarioRule<BuildingActivity> activityRule =
            new ActivityScenarioRule<>(BuildingActivity.class);

    private View decorView;

    private User u;
    FirebaseDatabase root;
    DatabaseReference reference;

    private void setUser(int flag)
    {
        root = FirebaseDatabase.getInstance();
        reference = root.getReference();
        u = new User("email@gmail.com", "2000000001", "test", "Student", "12345678");

        // for debug purpose
        if (flag != 0)
        {
            Reservation cr = new Reservation("Fertitta Hall (JFF)", 4, 5, "indoor");
            u.currentReservation = cr;
        }
        reference.child("Users").child("2000000001").setValue(u);
    }

    @Before
    public void setUp() {
        activityRule.getScenario().onActivity(new ActivityScenario.ActivityAction<BuildingActivity>() {
            @Override
            public void perform(BuildingActivity activity) {
                decorView = activity.getWindow().getDecorView();
            }
        });
    }

    @After
    public void tearDown() {
        root = FirebaseDatabase.getInstance();
        reference = root.getReference();
        reference.child("Users").child("2000000001").removeValue();
    }

    @Test
    public void testReserveWithNoSeatSelected() {
        setUser(0);
        // delay for a second
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.book_button)).perform(click());

        // delay for a second
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("No Time Slot Selected!"))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testReserveUnavailable() {
        setUser(0);
        // delay for a second
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.recyclerViewTimeSlots))
                .perform(scrollToPosition(0))
                .perform(actionOnItemAtPosition(0, click()));

        onView(withId(R.id.book_button)).perform(click());

        // delay for a second
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("No Available Seats!"))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testReserveNotConsecutive() {
        setUser(0);
        // delay for a second
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.recyclerViewTimeSlots))
                .perform(scrollToPosition(20))
                .perform(actionOnItemAtPosition(20, click()));

        onView(withId(R.id.recyclerViewTimeSlots))
                .perform(scrollToPosition(22))
                .perform(actionOnItemAtPosition(22, click()));

        onView(withId(R.id.book_button)).perform(click());

        // delay for a second
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("Time Slots Not Consecutive!"))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testReserveWithInAndOutTimeSlots() {
        setUser(0);
        // delay for a second
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.recyclerViewTimeSlots))
                .perform(scrollToPosition(25))
                .perform(actionOnItemAtPosition(25, click()));

        onView(withId(R.id.recyclerViewTimeSlots))
                .perform(scrollToPosition(26))
                .perform(actionOnItemAtPosition(26, click()));

        onView(withId(R.id.book_button)).perform(click());

        // delay for a second
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("Time Slots Not of the Same Type!"))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testReserveWithMoreThan4() {
        setUser(0);
        // delay for a second
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 1; i < 6; i++) {
            onView(withId(R.id.recyclerViewTimeSlots))
                    .perform(scrollToPosition(i))
                    .perform(actionOnItemAtPosition(i, click()));
        }

        onView(withId(R.id.book_button)).perform(click());

        // delay for a second
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("Too Many Time Slots Selected!"))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testReserveWithActiveReserve() {
        setUser(1);
        // delay for a second
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // delay for a second
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.recyclerViewTimeSlots))
                .perform(scrollToPosition(16))
                .perform(actionOnItemAtPosition(16, click()));

        onView(withId(R.id.book_button)).perform(click());

        // delay for a second
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("You Have an Active Reservation!"))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));

    }
}
