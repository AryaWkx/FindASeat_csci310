package com.example.findaseat_csci310;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.findaseat_csci310.LoginActivity;
import com.example.findaseat_csci310.MainActivity;
import com.example.findaseat_csci310.BuildingActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class BlackBoxTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testLoginWithIncorrectPassword() {
        // Input valid username
        onView(withId(R.id.login_uscid))
                .perform(typeText("0000000001"), ViewActions.closeSoftKeyboard());

        // Input incorrect password
        onView(withId(R.id.login_password))
                .perform(typeText("123123123"), ViewActions.closeSoftKeyboard());

        // Click on the login button
        onView(withId(R.id.login_button)).perform(click());

        // Check for a login failure message (e.g., a Toast, TextView, or Dialog)
        // Replace "Login failed" with the actual error message your app displays
        onView(withText("Incorrect password. Please try again."))
                .inRoot(isDialog()) // Use isDialog() to ensure the view is in a dialog
                .check(matches(isDisplayed()));
    }
}
