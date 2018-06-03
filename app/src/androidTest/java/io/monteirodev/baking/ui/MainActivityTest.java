package io.monteirodev.baking.ui;


import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.monteirodev.baking.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static io.monteirodev.baking.TestUtils.matchToolbarTitle;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private String BROWNIES = "Brownies";

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    /** <a href="https://stackoverflow.com/a/27669809/6997703">
     *  How to click on an item inside a RecyclerView in Espresso
     *  </a> */
    @Test
    public void actionBar_hasAppName() {
        CharSequence title = InstrumentationRegistry.getTargetContext()
                .getString(R.string.app_name);
        matchToolbarTitle(title).check(matches(isDisplayed()));
    }

    @Test
    public void recipesRecyclerView_isClickable() {
        onView(withId(R.id.recipes_recycler_view))
                .perform(actionOnItemAtPosition(0, click()));
    }

    @Test
    public void clickBrownies_opensRecipeActivityWithBrownies() {
        // to do add dynamic click
        onView(withId(R.id.recipes_recycler_view))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(BROWNIES)), click()));
        matchToolbarTitle(BROWNIES).check(matches(isDisplayed()));
    }
}
