package baking.nanodegree.android.baking;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import baking.nanodegree.android.baking.ui.recipe.RecipeActivity;
import baking.nanodegree.android.baking.ui.recipeDetails.RecipeDetailActivity;
import baking.nanodegree.android.baking.ui.recipeVideo.RecipeVideoActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class WorkFlowTest {
    @Rule
    public IntentsTestRule<RecipeActivity> mActivityTestRule = new IntentsTestRule<>(RecipeActivity.class);

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Before
    public void stubAllExternalIntents() {
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void checkRecipeBrowniesWorkFlow() {
        onView(ViewMatchers.withId(R.id.rv_recipe_card)).perform(RecyclerViewActions.scrollToPosition(0));
        onView(ViewMatchers.withId(R.id.rv_recipe_card)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        intended(hasComponent(RecipeDetailActivity.class.getName()));
        onView(allOf(Matchers.<View>instanceOf(TextView.class),
                withParent(withResourceName("action_bar"))))
                .check(matches(withText("Brownies")));
        onView(withId(R.id.expandableListView)).check(matches(isDisplayed()));
        onView(withId(R.id.stepper_list)).check(matches(isDisplayed()));
        onView(withIndex(withId(R.id.video_fab), 0)).perform(click());
        intended(hasComponent(RecipeVideoActivity.class.getName()));
        onView(withId(R.id.step_text_view)).check(matches(isDisplayed()));
        onView(withId(R.id.next_video_fab)).perform(click());
        onView(withId(R.id.step_text_view)).check(matches(isDisplayed()));
    }

    @Test
    public void checkRecipeCheesecakeWorkFlow() {
        onView(ViewMatchers.withId(R.id.rv_recipe_card)).perform(RecyclerViewActions.scrollToPosition(1));
        onView(ViewMatchers.withId(R.id.rv_recipe_card)).perform(RecyclerViewActions.actionOnItemAtPosition(1,click()));
        intended(hasComponent(RecipeDetailActivity.class.getName()));
        onView(allOf(Matchers.<View>instanceOf(TextView.class),
                withParent(withResourceName("action_bar"))))
                .check(matches(withText("Cheesecake")));
        onView(withId(R.id.expandableListView)).check(matches(isDisplayed()));
        onView(withId(R.id.stepper_list)).check(matches(isDisplayed()));
        onView(withIndex(withId(R.id.video_fab), 0)).perform(click());
        intended(hasComponent(RecipeVideoActivity.class.getName()));
        onView(withId(R.id.step_text_view)).check(matches(isDisplayed()));
        onView(withId(R.id.next_video_fab)).perform(click());
        onView(withId(R.id.step_text_view)).check(matches(isDisplayed()));
    }

    @Test
    public void checkRecipeNutellaPieWorkFlow() {
        onView(ViewMatchers.withId(R.id.rv_recipe_card)).perform(RecyclerViewActions.scrollToPosition(2));
        onView(ViewMatchers.withId(R.id.rv_recipe_card)).perform(RecyclerViewActions.actionOnItemAtPosition(2,click()));
        intended(hasComponent(RecipeDetailActivity.class.getName()));
        onView(allOf(Matchers.<View>instanceOf(TextView.class),
                withParent(withResourceName("action_bar"))))
                .check(matches(withText("Nutella Pie")));
        onView(withId(R.id.expandableListView)).check(matches(isDisplayed()));
        onView(withId(R.id.stepper_list)).check(matches(isDisplayed()));
        onView(withIndex(withId(R.id.video_fab), 0)).perform(click());
        intended(hasComponent(RecipeVideoActivity.class.getName()));
        onView(withId(R.id.step_text_view)).check(matches(isDisplayed()));
        onView(withId(R.id.next_video_fab)).perform(click());
        onView(withId(R.id.step_text_view)).check(matches(isDisplayed()));
    }

    @Test
    public void checkRecipeYellowCakeWorkFlow() {
        onView(ViewMatchers.withId(R.id.rv_recipe_card)).perform(RecyclerViewActions.scrollToPosition(3));
        onView(ViewMatchers.withId(R.id.rv_recipe_card)).perform(RecyclerViewActions.actionOnItemAtPosition(3,click()));
        intended(hasComponent(RecipeDetailActivity.class.getName()));
        onView(allOf(Matchers.<View>instanceOf(TextView.class),
                withParent(withResourceName("action_bar"))))
                .check(matches(withText("Yellow Cake")));
        onView(withId(R.id.expandableListView)).check(matches(isDisplayed()));
        onView(withId(R.id.stepper_list)).check(matches(isDisplayed()));
        onView(withIndex(withId(R.id.video_fab), 0)).perform(click());
        intended(hasComponent(RecipeVideoActivity.class.getName()));
        onView(withId(R.id.step_text_view)).check(matches(isDisplayed()));
        onView(withId(R.id.next_video_fab)).perform(click());
        onView(withId(R.id.step_text_view)).check(matches(isDisplayed()));
    }


    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }

    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }
}