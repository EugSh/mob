package com.travels.searchtravels;

import android.app.Instrumentation;
import android.content.Intent;
import android.util.Log;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.travels.searchtravels.activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
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

    @Parameterized.Parameter(value = 3)
    public boolean availableCategory;


    @Parameterized.Parameters
    public static Collection<Object[]> initParameters() {

        return Arrays.asList(new Object[][]{
                {"sea", R.drawable.sea, "Римини", true},
                {"ocean", R.drawable.sea, "Римини", true},
                {"beach", R.drawable.sea, "Римини", true},
                {"snow", R.drawable.snow, "Хельсинки", true},
                {"mountain", R.drawable.mountain, "Сочи", true},
                {"other", R.drawable.dog, "Other", false}
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
    public void cityName() throws IOException, InterruptedException {
        onView(withId(R.id.nextBTN)).perform(click());
        Instrumentation.ActivityResult imgGalleryResult = Utils.createImageGallerySetResultStub(resId);
        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(imgGalleryResult);
        onView(withId(R.id.pickImageBTN)).perform(click());
        Thread.sleep(10000);
        if (availableCategory) {
            onView(withId(R.id.cityTV)).check(matches(withText(cityName)));
        } else {
            onView(withId(R.id.loadAnotherTV)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void countryName() throws IOException, InterruptedException, JSONException {
        onView(withId(R.id.nextBTN)).perform(click());
        Instrumentation.ActivityResult imgGalleryResult = Utils.createImageGallerySetResultStub(resId);
        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(imgGalleryResult);
        onView(withId(R.id.pickImageBTN)).perform(click());
        Thread.sleep(10000);
        if (availableCategory) {
            final String expected = getCountry(cityName);
            onView(withId(R.id.cityTV)).check(matches(withText(expected)));
        } else {
            onView(withId(R.id.loadAnotherTV)).check(matches(isDisplayed()));
        }
    }

    private String getCountry(String city) throws IOException, JSONException {
        URL obj = new URL("https://autocomplete.travelpayouts.com/places2?locale=en&types[]=city&term=" + city);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Content-Type", "application/json");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }
        bufferedReader.close();

        JSONArray responseJSON = new JSONArray(response.toString());
        Log.d("myLogs", "responseJSON = " + responseJSON);
        String code = responseJSON.getJSONObject(0).getString("code");
        return responseJSON.getJSONObject(0).getString("country_name");
    }


}
