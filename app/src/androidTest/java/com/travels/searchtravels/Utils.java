package com.travels.searchtravels;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.test.core.app.ApplicationProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class Utils {
    private Utils() {
    }


    public static Uri savePickedImage(int imageResId) throws IOException {
        final Bitmap bm = BitmapFactory.decodeResource(ApplicationProvider.getApplicationContext().getResources(), imageResId);
        assertTrue(bm != null);
        final File file = File.createTempFile("img" + imageResId, ".jpg", ApplicationProvider.getApplicationContext().getCacheDir());

        try (final FileOutputStream outStream = new FileOutputStream(file)) {
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
        }
        return Uri.fromFile(file);
    }

    public static Instrumentation.ActivityResult createImageGallerySetResultStub(int imageResId) throws IOException {
        final Intent resultData = new Intent();
        final Uri uri = savePickedImage(imageResId);
        resultData.setData(uri);
        return new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
    }
}
