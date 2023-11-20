package com.example.findaseat_csci310;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

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

@RunWith(AndroidJUnit4.class)
public class LoginPageTest {
    @Rule
    public IntentsTestRule<LoginActivity> intentsTestRule = new IntentsTestRule<>(LoginActivity.class);

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

    @Test
    public void testLoginWithInvalidID() {
        // Input valid username
        onView(withId(R.id.login_uscid))
                .perform(typeText("0000000002"), ViewActions.closeSoftKeyboard());

        // Input incorrect password
        onView(withId(R.id.login_password))
                .perform(typeText("123123123"), ViewActions.closeSoftKeyboard());

        // Click on the login button
        onView(withId(R.id.login_button)).perform(click());

        // Check for a login failure message (e.g., a Toast, TextView, or Dialog)
        // Replace "Login failed" with the actual error message your app displays
        onView(withText("Invalid ID. Please try again or sign up."))
                .inRoot(isDialog()) // Use isDialog() to ensure the view is in a dialog
                .check(matches(isDisplayed()));
    }

    @Test
    public void testLoginWithValidCredentials() throws InterruptedException {
        // Input valid username
        onView(withId(R.id.login_uscid))
                .perform(typeText("0000000001"), ViewActions.closeSoftKeyboard());

        // Input incorrect password
        onView(withId(R.id.login_password))
                .perform(typeText("12345678"), ViewActions.closeSoftKeyboard());

        // Click on the login button
        onView(withId(R.id.login_button)).perform(click());

        Thread.sleep(1000);
        // Check if MainActivity is opened
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void testSignup() throws InterruptedException {
        // Click on the signup button
        int signupTabIndex = 1;
        onView(withId(R.id.tab_layout))
                .perform(TabLayoutActions.selectTabAtPosition(signupTabIndex));

        Thread.sleep(500);
        // Input valid email
        onView(withId(R.id.signup_email))
                .perform(typeText("test@test.com"), ViewActions.closeSoftKeyboard());

        // Input valid USC ID
        onView(withId(R.id.uscid))
                .perform(typeText("0000000010"), ViewActions.closeSoftKeyboard());

        // Input valid name
        onView(withId(R.id.signup_name))
                .perform(typeText("Test"), ViewActions.closeSoftKeyboard());

        // Input valid affiliation
        onView(withId(R.id.signup_affiliation))
                .perform(typeText("Student"), ViewActions.closeSoftKeyboard());

        // Input valid password
        onView(withId(R.id.signup_password))
                .perform(typeText("12345678"), ViewActions.closeSoftKeyboard());

        // Click on the signup button
        onView(withId(R.id.signup_button)).perform(click());
        Thread.sleep(2000);

        // Go to Login fragment
        int loginTabIndex = 0;
        onView(withId(R.id.tab_layout))
                .perform(TabLayoutActions.selectTabAtPosition(loginTabIndex));

        Thread.sleep(500);
        // Input valid user id
        onView(withId(R.id.login_uscid))
                .perform(typeText("0000000010"), ViewActions.closeSoftKeyboard());

        // Input valid password
        onView(withId(R.id.login_password))
                .perform(typeText("12345678"), ViewActions.closeSoftKeyboard());

        // Click on the login button
        onView(withId(R.id.login_button)).perform(click());
        Thread.sleep(2000);

        // Check if MainActivity is opened
        intended(hasComponent(MainActivity.class.getName()));
    }
}
