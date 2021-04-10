package org.coursera.sustainableapps.caostoneproject;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static androidx.test.espresso.web.model.Atoms.getCurrentUrl;
import static androidx.test.espresso.web.sugar.Web.onWebView;
import static org.hamcrest.Matchers.containsString;

@RunWith(AndroidJUnit4.class)
public class EspressoDangerTest {

    @Rule
    public ActivityTestRule mDangerRule = new ActivityTestRule<>(
            Danger.class);

    @Before
    public void setUp() {
        mDangerRule.getActivity();
    }

    @Test
    public void jumpToWikipediaRadiation() {

        onView(withId(R.id.buttonRadiation)).perform(click());

        onWebView()
                .check(webMatches(getCurrentUrl(),
                        containsString("https://en.m.wikipedia.org/wiki/Radiation")));
    }

    @Test
    public void jumpToWikipediaBiodefense() {

        onView(withId(R.id.buttonBio)).perform(click());

        onWebView()
                .check(webMatches(getCurrentUrl(),
                        containsString("https://en.m.wikipedia.org/wiki/Biodefense")));
    }

    @Test
    public void jumpToWikipediaChemical() {

        onView(withId(R.id.buttonChem)).perform(click());

        onWebView()
                .check(webMatches(getCurrentUrl(),
                        containsString("https://en.m.wikipedia.org/wiki/Chemical_hazard")));
    }

    @Test
    public void jumpToWikipediaLaser() {

        onView(withId(R.id.buttonLaser)).perform(click());

        onWebView()
                .check(webMatches(getCurrentUrl(),
                        containsString("https://en.m.wikipedia.org/wiki/Laser_safety")));
    }

    @Test
    public void jumpToWikipediaMagnetic() {

        onView(withId(R.id.buttonMagnetic)).perform(click());

        onWebView()
                .check(webMatches(getCurrentUrl(),
                        containsString("https://en.m.wikipedia.org/wiki/Electromagnetic_radiation")));
    }

    @Test
    public void jumpToWikipediaRadio() {

        onView(withId(R.id.buttonRadio)).perform(click());

        onWebView()
                .check(webMatches(getCurrentUrl(),
                        containsString("https://en.m.wikipedia.org/wiki/Radio_wave")));
    }

}
