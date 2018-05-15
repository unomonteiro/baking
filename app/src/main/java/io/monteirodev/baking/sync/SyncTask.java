package io.monteirodev.baking.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import java.net.URL;
import java.util.List;

import io.monteirodev.baking.database.BakingContract;
import io.monteirodev.baking.database.BakingProvider;
import io.monteirodev.baking.utils.JsonUtils;
import io.monteirodev.baking.utils.NetworkUtils;

public class SyncTask {

    private static final String TAG = SyncTask.class.getSimpleName();

    synchronized public static void syncRecipes(Context context) {
        try {
            URL bakingUrl = NetworkUtils.getURL();

            String jsonRecipeResponse = NetworkUtils.getResponseFromHttpUrl(bakingUrl);

            List<ContentValues[]> bakingValues = JsonUtils.getRecipeContentValues(jsonRecipeResponse);

            if (bakingValues != null) {
                ContentResolver bakingContentResolver = context.getContentResolver();
                bakingContentResolver.bulkInsert(BakingProvider.BakingRecipes)

            }
        } catch (Exception e) {
            Log.e(TAG, "syncRecipes: " + e.getMessage(), e);
        }
    }
}
