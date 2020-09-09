package com.travels.searchtravels;

import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.travels.searchtravels.activity.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(Parameterized.class)
public class ImageCategoryTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Parameterized.Parameter(value = 0)
    public String tag;

    @Parameterized.Parameter(value = 1)
    public int resId;

    @Parameterized.Parameter(value = 2)
    public String cityName;

    @Parameterized.Parameters
    public static Collection<Object[]> initParameters() {

        return Arrays.asList(new Object[][]{
                {"sea", R.drawable.sea, "Римини"},
                {"ocean", R.drawable.sea, "Римини"},
                {"beach", R.drawable.sea, "Римини"},
                {"other", R.drawable.sea, "Римини"},
                {"snow", R.drawable.snow, "Хельсинки"},
                {"mountain", R.drawable.mountain, "Сочи"}
        });
    }

    @Before
    public void before() {
        Intents.init();
    }

    @After
    public void after() {
        Intents.release();
    }

    @Test
    public void categoryTest() throws IOException, InterruptedException {
        onView(withId(R.id.nextBTN)).perform(click());
        Instrumentation.ActivityResult imgGalleryResult = Utils.createImageGallerySetResultStub(resId);
        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(imgGalleryResult);
        onView(withId(R.id.pickImageBTN)).perform(click());
        Thread.sleep(10000);
        onView(withId(R.id.cityTV)).check(matches(withText(cityName)));
    }
}
