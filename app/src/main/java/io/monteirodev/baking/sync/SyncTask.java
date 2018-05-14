package io.monteirodev.baking.sync;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import io.monteirodev.baking.utils.JsonUtils;
import io.monteirodev.baking.utils.NetworkUtils;

public class SyncTask {

    private static final String TAG = SyncTask.class.getSimpleName();

    synchronized public static void syncRecipes(Context context) {
        try {
            URL bakingUrl = NetworkUtils.getURL();

            String jsonString = NetworkUtils.getResponseFromHttpUrl(bakingUrl);

            List<ContentValues[]> bakingValues = JsonUtils.getContentValues(jsonString);
        } catch (Exception e) {
            Log.e(TAG, "syncRecipes: " + e.getMessage(), e);
        }
    }
}
