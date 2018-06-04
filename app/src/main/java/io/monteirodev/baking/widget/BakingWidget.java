package io.monteirodev.baking.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import io.monteirodev.baking.R;
import io.monteirodev.baking.ui.MainActivity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Implementation of App Widget functionality.
 */
public class BakingWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String recipeName) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ingredients);
        Intent appIntent = new Intent(context, MainActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, 0);
        String title = context.getString(R.string.app_name_small);
        if (recipeName != null && !recipeName.isEmpty()) {
            title = recipeName;
        }
        views.setTextViewText(R.id.widget_title_text, title);
        views.setOnClickPendingIntent(R.id.widget_title_layout, appPendingIntent);
        views.setOnClickPendingIntent(R.id.widget_layout_main, appPendingIntent);
        views.setViewVisibility(R.id.widget_invalid_recipe_text, GONE);
        views.setViewVisibility(R.id.widget_list, VISIBLE);

        // Add the click handler
        Intent selectIntent = new Intent(context, WidgetIntentService.class);
        selectIntent.setAction(WidgetIntentService.ACTION_UPDATE_SELECTED_RECIPE);
        PendingIntent selectPendingIntent = PendingIntent.getService(context, 0, selectIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_list, selectPendingIntent);
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

