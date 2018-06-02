package io.monteirodev.baking.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import io.monteirodev.baking.R;
import io.monteirodev.baking.ui.MainActivity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static io.monteirodev.baking.ui.MainActivity.INVALID_RECIPE_ID;
import static io.monteirodev.baking.ui.MainActivity.RECIPE_ID_KEY;

/**
 * Implementation of App Widget functionality.
 */
public class BakingWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String recipeName) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ingredients);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_title_layout, pendingIntent);
        views.setOnClickPendingIntent(R.id.widget_layout_main, pendingIntent);
        views.setViewVisibility(R.id.widget_invalid_recipe_text, GONE);
        views.setViewVisibility(R.id.widget_list, VISIBLE);
        int recipeId = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(RECIPE_ID_KEY, INVALID_RECIPE_ID);
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, WidgetViewsService.class));
        views.setEmptyView(R.id.widget_list, R.id.widget_invalid_recipe_text);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateBakingWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, String recipeName) {
        for (int appWidgetId: appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipeName);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        WidgetIntentService.startActionUpdateSelectedRecipe(context);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        WidgetIntentService.startActionUpdateSelectedRecipe(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // Perform any action when one or more AppWidget instances have been deleted
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


}

