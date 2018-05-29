package io.monteirodev.baking.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

public class WidgetAdapter implements RemoteViewsService.RemoteViewsFactory {

    List<String> mIngredientList = new ArrayList<>();
    Context mContext;
    Intent mIntent;

    private void initData() {
        mIngredientList.clear();
        for (int i = 1; i < 10; i++) {
            mIngredientList.add("Item " + i);
        }
    }

    public WidgetAdapter(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
        // todo use intent to open activity
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
        if (mIngredientList == null) {
            return 0;
        }
        return mIngredientList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(
                mContext.getPackageName(),
                android.R.layout.simple_list_item_1);
        remoteView.setTextViewText(
                android.R.id.text1,
                mIngredientList.get(position));
        remoteView.setTextColor(android.R.id.text1, Color.BLACK);
        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        // todo list of recipe
        // todo list of ingredients
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position; // todo recipeId
    }

    @Override
    public boolean hasStableIds() {
        return true; // todo add/delete data
    }
}
