package io.monteirodev.baking.sync;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import io.monteirodev.baking.database.BakingProvider;
import io.monteirodev.baking.database.RecipeColumns;

public class SyncUtils {

    //private static boolean sInitialized;
    //private static final int ONE_DAY_MILLIS = (int) TimeUnit.DAYS.toSeconds(1);
    private static final int ONE_DAY_MILLIS = (int) TimeUnit.SECONDS.toSeconds(10);
    private static final String LAST_SYNC_KEY = "last_sync";

    synchronized public static void initialise(@NonNull final Context context) {

//        if (sInitialized) return;
//        sInitialized = true;
        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri recipeQueryUri = BakingProvider.Recipes.CONTENT_URI;
                String[] projection = {RecipeColumns.ID};
                Cursor cursor = context.getContentResolver().query(
                        recipeQueryUri,
                        projection,
                        null,
                        null,
                        null);

                if (cursor == null || cursor.getCount() == 0) {
                    startImmediateSync(context);
                } else {
                    cursor.close();
                    startDailySync(context);
                }
            }
        });
        checkForEmpty.start();
    }

    private static void startDailySync(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long lastSync = prefs.getLong(LAST_SYNC_KEY, 0);
        if ((System.currentTimeMillis() - lastSync) >= ONE_DAY_MILLIS) {
            startImmediateSync(context);
            prefs.edit().putLong(LAST_SYNC_KEY, System.currentTimeMillis()).apply();
        }
    }

    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, BakingSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }


}
