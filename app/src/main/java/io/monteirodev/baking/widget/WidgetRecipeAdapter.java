package io.monteirodev.baking.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

public class WidgetRecipeAdapter implements RemoteViewsService.RemoteViewsFactory {

    Context context;
    Intent intent;
    List<String> recipeList = new ArrayList<>();

    public WidgetRecipeAdapter(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    private void initData() {
        recipeList.clear();
        for (int i = 1; i <= 10; i++) {
            // recipeList.add("item " + i);
        }
    }


    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return recipeList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(),
                android.R.layout.simple_list_item_1);
        remoteView.setTextViewText(android.R.id.text1, recipeList.get(position));
        remoteView.setTextColor(android.R.id.text1, Color.BLACK);
        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position; // todo recipeId
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
