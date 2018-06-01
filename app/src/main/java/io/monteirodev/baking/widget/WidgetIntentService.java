package io.monteirodev.baking.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;

import io.monteirodev.baking.R;

import static io.monteirodev.baking.ui.MainActivity.INVALID_RECIPE_ID;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class WidgetIntentService extends IntentService {
    private static final String ACTION_UPDATE_SELECTED_RECIPE = "io.monteirodev.baking.widget.action.update_selected_recipe";

    private static final String EXTRA_RECIPE_ID = "io.monteirodev.baking.widget.extra.recipe_id";
    private static final String EXTRA_RECIPE_NAME = "io.monteirodev.baking.widget.extra.RECIPE_NAME";

    public WidgetIntentService() {
        super("WidgetIntentService");
    }

    public static void startActionUpdateSelectedRecipe(Context context, int recipeId, String recipeName) {
        Intent intent = new Intent(context, WidgetIntentService.class);
        intent.setAction(ACTION_UPDATE_SELECTED_RECIPE);
        intent.putExtra(EXTRA_RECIPE_ID, recipeId);
        intent.putExtra(EXTRA_RECIPE_NAME, recipeName);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_SELECTED_RECIPE.equals(action)) {
                final int recipeId = intent.getIntExtra(EXTRA_RECIPE_ID, INVALID_RECIPE_ID);
                final String recipeName = intent.getStringExtra(EXTRA_RECIPE_NAME);
                handleActionUpdateSelectedRecipe(recipeId, recipeName);
            }
        }
    }

    private void handleActionUpdateSelectedRecipe(int recipeId, String recipeName) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        // BakingWidget.updateAppWidget();
    }
}
