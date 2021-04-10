package org.coursera.sustainableapps.caostoneproject;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class EspressoMainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void setUp() {
            mActivityRule.getActivity();
    }

    /**
     * Jump from MainActivity to other Activity
     */
    @Test
    public void jumpFromMainActivityToDangerActivity() {

        onView(withId(R.id.buttonDanger)).perform(click());
        onView(withId(R.id.webView)).check(matches(isDisplayed()));
    }

    @Test
    public void jumpFromMainActivityToDataBaseActivity() {

        onView(withId(R.id.buttonDatabase)).perform(click());
        onView(withId(R.id.listDanger)).check(matches(isDisplayed()));
    }

    @Test
    public void jumpFromMainActivityToObserveActivity() {

        onView(withId(R.id.buttonMap)).perform(click());
        onView(withId(R.id.listObserve)).check(matches(isDisplayed()));
    }

}
