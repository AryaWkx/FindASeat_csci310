package com.example.findaseat_csci310;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.util.Log;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MapPageTest {

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void testReserveWithLogin() throws InterruptedException {
        Thread.sleep(1000);
        // Click on the profile icon (switch to profile page)
        onView(withId(R.id.bottom_profile)).perform(click());

        // Click yes button in alert dialog
        onView(withText("Login")).perform(click());
        Thread.sleep(1000);

        // Input valid user id
        onView(withId(R.id.login_uscid))
                .perform(typeText("0000000001"), ViewActions.closeSoftKeyboard());

        // Input valid password
        onView(withId(R.id.login_password))
                .perform(typeText("12345678"), ViewActions.closeSoftKeyboard());

        // Click on the login button
        onView(withId(R.id.login_button)).perform(click());
        Thread.sleep(1000);

        // Click on the floating action button (switch to reserve mode)
        onView(withId(R.id.floatingActionButton1)).perform(click());
        Thread.sleep(1000);

        // Click the taper hall marker through UI Automator through following steps:
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        int screenWidth = device.getDisplayWidth();
        int screenHeight = device.getDisplayHeight();
        Log.d("screenWidth", String.valueOf(screenWidth));
        Log.d("screenHeight", String.valueOf(screenHeight));
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2 - 100;
        device.click(centerX, centerY);
        Thread.sleep(1000);

        // Click on yes button in the popup window
        onView(withId(R.id.reserve_yes)).perform(click());

        // Check if Building Activity is opened
        Thread.sleep(1000);
        intended(hasComponent(BuildingActivity.class.getName()));
    }

    @Test
    public void testNavWithLogin() throws InterruptedException {
        Thread.sleep(1000);
        // Click on the profile icon (switch to profile page)
        onView(withId(R.id.bottom_profile)).perform(click());

        // Click yes button in alert dialog
        onView(withText("Login")).perform(click());
        Thread.sleep(1000);

        // Input valid user id
        onView(withId(R.id.login_uscid))
                .perform(typeText("0000000001"), ViewActions.closeSoftKeyboard());

        // Input valid password
        onView(withId(R.id.login_password))
                .perform(typeText("12345678"), ViewActions.closeSoftKeyboard());

        // Click on the login button
        onView(withId(R.id.login_button)).perform(click());
        Thread.sleep(1000);

        // Click on the list icon (switch to building page)
        onView(withId(R.id.bottom_reservations)).perform(click());

        // Check if Building Activity is opened
        Thread.sleep(1000);
        intended(hasComponent(BuildingActivity.class.getName()));

        // Click on the profile icon (switch to profile page)
        onView(withId(R.id.bottom_profile)).perform(click());

        // Check if Profile Activity is opened
        Thread.sleep(1000);
        intended(hasComponent(UserActivity.class.getName()));
    }


    @Test
    public void testReserveWithoutLogin() throws InterruptedException {
        // Click on the floating action button (switch to reserve mode)
        onView(withId(R.id.floatingActionButton1)).perform(click());

        // Click the taper hall marker through UI Automator through following steps:
        // Initialize UiDevice instance
        // Get the coordinates of the taper hall marker
        // Click on the taper hall marker
        Thread.sleep(2000);
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        int screenWidth = device.getDisplayWidth();
        int screenHeight = device.getDisplayHeight();

        int centerX = screenWidth / 2 + 50;
        int centerY = screenHeight / 2;

        device.click(centerX, centerY);
        Thread.sleep(2000);

        // Check for the reserve failure message
        onView(withText("Please login first"))
                .inRoot(isDialog()) // Use isDialog() to ensure the view is in a dialog
                .check(matches(isDisplayed()));
    }

    @Test
    public void testNavWithoutLogin() throws InterruptedException {
        Thread.sleep(1000);
        // Click on the profile icon (switch to profile page)
        onView(withId(R.id.bottom_profile)).perform(click());

        // Check for the failure message
        onView(withText("Please login first"))
                .inRoot(isDialog()) // Use isDialog() to ensure the view is in a dialog
                .check(matches(isDisplayed()));

        // Click cancel button in alert dialog
        onView(withText("Cancel")).perform(click());

        // Click on the list icon (switch to building page)
        onView(withId(R.id.bottom_reservations)).perform(click());

        // Check for the failure message
        onView(withText("Please login first"))
                .inRoot(isDialog()) // Use isDialog() to ensure the view is in a dialog
                .check(matches(isDisplayed()));
    }
}
