package io.monteirodev.baking.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import java.net.URL;
import java.util.ArrayList;

import io.monteirodev.baking.database.BakingProvider;
import io.monteirodev.baking.utils.BakingJsonUtils;
import io.monteirodev.baking.utils.NetworkUtils;

public class SyncTask {

    private static final String TAG = SyncTask.class.getSimpleName();

    synchronized public static void syncRecipes(Context context) {
        try {
            URL bakingUrl = NetworkUtils.getURL();

            String jsonResponse = NetworkUtils.getResponseFromHttpUrl(bakingUrl);

            ArrayList<ContentValues[]> bakingValues = BakingJsonUtils.getContentValues(jsonResponse);

            if (bakingValues != null && bakingValues.size() > 0) {
                ContentResolver contentResolver = context.getContentResolver();

                contentResolver.delete(
                        BakingProvider.Recipes.CONTENT_URI, null, null);
                contentResolver.delete(
                        BakingProvider.Ingredients.CONTENT_URI, null, null);
                contentResolver.delete(
                        BakingProvider.Steps.CONTENT_URI, null, null);

                contentResolver.bulkInsert(
                        BakingProvider.Recipes.CONTENT_URI,
                        bakingValues.get(BakingJsonUtils.RECIPES_INDEX)
                );
                contentResolver.bulkInsert(
                        BakingProvider.Ingredients.CONTENT_URI,
                        bakingValues.get(BakingJsonUtils.INGREDIENTS_INDEX)
                );
                contentResolver.bulkInsert(
                        BakingProvider.Steps.CONTENT_URI,
                        bakingValues.get(BakingJsonUtils.STEPS_INDEX)
                );

            }
        } catch (Exception e) {
            Log.e(TAG, "syncRecipes: " + e.getMessage(), e);
        }
    }
}
