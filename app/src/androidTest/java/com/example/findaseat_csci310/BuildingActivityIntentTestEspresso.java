package com.example.findaseat_csci310;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

public class BuildingActivityIntentTestEspresso {
    @Rule
    public IntentsTestRule<BuildingActivity> activityRule =
            new IntentsTestRule<>(BuildingActivity.class);

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

    @After
    public void tearDown() {
        root = FirebaseDatabase.getInstance();
        reference = root.getReference();
        reference.child("Users").child("2000000001").removeValue();
    }

    @Test
    public void testReserveSuccessful() {
        setUser(0);
        // delay for a second
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // click on the first seat
        onView(withId(R.id.recyclerViewTimeSlots))
                .perform(RecyclerViewActions.actionOnItemAtPosition(16, click()));

        // click on the reserve button
        onView(withId(R.id.book_button)).perform(click());

        // check if the reservation is successful
        intended(hasComponent(MainActivity.class.getName()));
    }
}
