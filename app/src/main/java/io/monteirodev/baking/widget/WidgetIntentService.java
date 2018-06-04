package io.monteirodev.baking.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.preference.PreferenceManager;

import io.monteirodev.baking.R;
import io.monteirodev.baking.database.BakingProvider;
import io.monteirodev.baking.models.Recipe;

import static io.monteirodev.baking.ui.MainActivity.INVALID_RECIPE_ID;
import static io.monteirodev.baking.ui.MainActivity.RECIPE_ID_KEY;

public class WidgetIntentService extends IntentService {
    static final String ACTION_UPDATE_SELECTED_RECIPE = "io.monteirodev.baking.widget.action.update_selected_recipe";

    public WidgetIntentService() {
        super("WidgetIntentService");
    }

    public static void startActionUpdateSelectedRecipe(Context context) {
        Intent intent = new Intent(context, WidgetIntentService.class);
        intent.setAction(ACTION_UPDATE_SELECTED_RECIPE);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_SELECTED_RECIPE.equals(action)) {
                int intExtraRecipeId = intent.getIntExtra(RECIPE_ID_KEY, INVALID_RECIPE_ID);
                if (intExtraRecipeId != INVALID_RECIPE_ID) {
                    PreferenceManager.getDefaultSharedPreferences(this)
                            .edit()
                            .putInt(RECIPE_ID_KEY, intExtraRecipeId)
                            .apply();
                }
                handleActionUpdateSelectedRecipe();
            }
        }
    }

    private void handleActionUpdateSelectedRecipe() {
        String recipeName = null;
        int recipeId = PreferenceManager.getDefaultSharedPreferences(this).getInt(
                RECIPE_ID_KEY, INVALID_RECIPE_ID);
        if (recipeId != INVALID_RECIPE_ID) {
            Cursor cursor = getContentResolver().query(
                    BakingProvider.Recipes.withId(recipeId),
                    null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                recipeName = new Recipe(cursor).getName();
            }
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        BakingWidget.updateBakingWidgets(this, appWidgetManager, appWidgetIds, recipeName);
    }
}
