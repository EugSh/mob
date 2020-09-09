package com.travels.searchtravels;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.preference.PreferenceManager;
import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.api.services.vision.v1.model.LatLng;
import com.preview.planner.prefs.AppPreferences;
import com.travels.searchtravels.api.OnVisionApiListener;
import com.travels.searchtravels.api.VisionApi;

import org.junit.Test;

public class ImageCategoryTest {
    private static final String TOKEN = "TOKEN";

    @Test
    public void test(){
//        final Bitmap bm = BitmapFactory.decodeResource(ApplicationProvider.getApplicationContext().getResources(), R.drawable.sea);
//        final String token =  AppPreferences.getToken();
//        accessToken = GoogleAuthUtil.getToken(ApplicationProvider.getApplicationContext(), mAccount, mScope);
//        GoogleAuthUtil.clearToken (mActivity, accessToken); // used to remove stale tokens.
//        accessToken = GoogleAuthUtil.getToken(mActivity, mAccount, mScope);
//        SharedPreferences defS = PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext());
//        defS.getString()
//        VisionApi.findLocation(bm, token, new OnVisionApiListener() {
//            @Override
//            public void onSuccess(LatLng latLng) {
//                System.out.println(123);
//            }
//
//            @Override
//            public void onErrorPlace(String category) {
//                System.out.println(321);
//            }
//
//            @Override
//            public void onError() {
//                System.out.println(2);
//
//            }
//        });
    }
}
