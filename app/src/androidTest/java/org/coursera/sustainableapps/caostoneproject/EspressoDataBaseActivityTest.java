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
public class EspressoDataBaseActivityTest {

    @Rule
    public ActivityTestRule mDangerRule = new ActivityTestRule<>(
            DataBase.class);

    @Before
    public void setUp() {
        mDangerRule.getActivity();
    }

    @Test
    public void jumpFromDataBaseToPositionActivityPushButtonAdd() {
        onView(withId(R.id.butAdd)).perform(click());
        onView(withId(R.id.imageIcon)).check(matches(isDisplayed()));
    }

    /**
     * this test works but deletes all data from the database
      */
//    @Test
//    public void pushButtonDeleteAll() {
//
//        onView(withId(R.id.butDelete)).perform(click());
//        onView(withId(R.id.listDanger)).check(matches(isDisplayed()));
//    }

    @Test
    public void pushButtonRefresh() {

        onView(withId(R.id.butRefresh)).perform(click());
        onView(withId(R.id.recyclerViewDataBase)).check(matches(isDisplayed()));
    }
}
