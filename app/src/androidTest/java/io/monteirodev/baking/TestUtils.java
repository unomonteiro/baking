package io.monteirodev.baking;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.Toolbar;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static org.hamcrest.Matchers.is;

public class TestUtils {

    /** http://blog.sqisland.com/2015/05/espresso-match-toolbar-title.html */
    public static ViewInteraction matchToolbarTitle(CharSequence title) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is(title))));
    }

    /** http://blog.sqisland.com/2015/05/espresso-match-toolbar-title.html */
    private static Matcher<Object> withToolbarTitle(final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }
            @Override public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }
}
