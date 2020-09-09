package com.travels.searchtravels;

import android.app.Instrumentation;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.travels.searchtravels.activity.MainActivity;

import org.hamcrest.Matcher;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class PriceTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private static final int IMG_RES_ID = R.drawable.mountain;
    private static final String CODE = "AER";
    private static final int MONTH_PERIOD = 1;
    private static final float PRICE_VOLATILITY = 0.05f;

    @Before
    public void before() {
        Intents.init();
    }

    @After
    public void after() {
        Intents.release();
    }

    @Test
    public void test() throws InterruptedException, IOException, JSONException {
        onView(withId(R.id.nextBTN)).perform(click());
        Instrumentation.ActivityResult imgGalleryResult = Utils.createImageGallerySetResultStub(IMG_RES_ID);
        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(imgGalleryResult);
        onView(withId(R.id.pickImageBTN)).perform(click());
        final int minPrice = getMinPrice(MONTH_PERIOD, CODE);
        Thread.sleep(10000);
        final int price = Integer.parseInt(getText(withId(R.id.airticketTV)).replaceAll("[\\p{L}+ ₽]", ""));
        final float max = minPrice * (1 + PRICE_VOLATILITY);
        final float min = minPrice * (1 - PRICE_VOLATILITY);
        Assert.assertTrue(String.format("Цена, указанная в приложении не принаджелит интервалу [%.2f, %.2f]. Минимальная цена %d. Разброс +- %.2f ", min, max, minPrice, PRICE_VOLATILITY) + "%",
                Float.compare(price, max) == -1 &&
                        Float.compare(price, min) == 1);

    }


    private String getText(final Matcher<View> matcher) {
        final String[] stringHolder = {null};
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "getting text from a TextView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView tv = (TextView) view; //Save, because of check in getConstraints()
                stringHolder[0] = tv.getText().toString();
            }
        });
        return stringHolder[0];
    }

    private int getMinPrice(int monthPeriod, String code) throws IOException, JSONException {
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(System.currentTimeMillis());
        final int startYear = calendar.get(Calendar.YEAR);
        final int startMonth = calendar.get(Calendar.MONTH);
        calendar.add(Calendar.MONTH, monthPeriod);
        final int endMonth = calendar.get(Calendar.MONTH);
        final int endYear = calendar.get(Calendar.YEAR);
        final URL obj2 = new URL(String.format("https://api.travelpayouts.com/v1/prices/cheap?origin=LED" +
                        "&depart_date=%d-%s" +
                        "&return_date=%d-%s" +
                        "&destination=%s",
                startYear,
                startMonth > 8 ? String.valueOf(startMonth + 1) : "0" + (startMonth + 1),
                endYear,
                endMonth > 8 ? String.valueOf(endMonth + 1) : "0" + (endMonth + 1),
                code));
        final HttpURLConnection connection2 = (HttpURLConnection) obj2.openConnection();

        //add reuqest header
        connection2.setRequestMethod("GET");
        connection2.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection2.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection2.setRequestProperty("Content-Type", "application/json");
        connection2.setRequestProperty("X-Access-Token", "0600f875d409bf004df9e0084a187aa5");
        int c = connection2.getResponseCode();
        final BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(connection2.getInputStream()));
        String inputLine2;
        StringBuffer response2 = new StringBuffer();

        while ((inputLine2 = bufferedReader2.readLine()) != null) {
            response2.append(inputLine2);
        }
        bufferedReader2.close();

        final JSONObject responseJSON2 = new JSONObject(response2.toString());
        final String isSuccess = responseJSON2.getString("success");
        if (!isSuccess.equals("true")) {
            return Integer.MAX_VALUE;
        }
        final JSONObject data = responseJSON2.getJSONObject("data").getJSONObject(code);
        final Iterator<String> keys = data.keys();
        int minPrixe = Integer.MAX_VALUE;
        while (keys.hasNext()) {
            final String key = keys.next();
            if (data.get(key) instanceof JSONObject) {
                final int price = Integer.parseInt(data.getJSONObject(key).getString("price"));
                minPrixe = Math.min(minPrixe, price);
            }
        }
        return minPrixe;
    }
}
