package io.monteirodev.baking.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import io.monteirodev.baking.database.BakingProvider;
import io.monteirodev.baking.database.RecipeColumns;
import io.monteirodev.baking.models.Ingredient;
import io.monteirodev.baking.models.Recipe;

import static io.monteirodev.baking.ui.MainActivity.INVALID_RECIPE_ID;
import static io.monteirodev.baking.ui.MainActivity.RECIPE_ID_KEY;

public class WidgetViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRecipeAdapter(this, intent);
    }
}

class WidgetRecipeAdapter implements RemoteViewsService.RemoteViewsFactory {

    Context context;
    Intent intent;
    List<Recipe> mRecipes = new ArrayList<>();
    List<Ingredient> mIngredients = new ArrayList<>();
    private int mRecipeId;

    public WidgetRecipeAdapter(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    private void initData() {
        mRecipeId = PreferenceManager.getDefaultSharedPreferences(context).getInt(
                RECIPE_ID_KEY, INVALID_RECIPE_ID);
        if (mRecipeId == INVALID_RECIPE_ID) {
            mRecipes.clear();
            Cursor cursor = context.getContentResolver().query(
                    BakingProvider.Recipes.CONTENT_URI,
                    null, null, null, RecipeColumns.NAME);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    mRecipes.add(new Recipe(cursor));
                }
            }
        } else {
            mIngredients.clear();
            Cursor cursor = context.getContentResolver().query(
                    BakingProvider.Ingredients.recipeIngredients(mRecipeId),
                    null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    mIngredients.add(new Ingredient(cursor));
                }
            }
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
        if (mRecipeId == INVALID_RECIPE_ID) {
            return mRecipes == null ? 0 : mRecipes.size();
        } else {
            return mIngredients == null ? 0 : mIngredients.size();
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = null;
        if (mRecipeId == INVALID_RECIPE_ID) {
            if (mRecipes == null || mRecipes.size() == 0) {
                return null;
            }
            remoteView = new RemoteViews(context.getPackageName(),
                    android.R.layout.simple_list_item_1);
            remoteView.setTextViewText(android.R.id.text1, mRecipes.get(position).getName());
            remoteView.setTextColor(android.R.id.text1, Color.BLACK);
        } else {
            if (mIngredients == null || mIngredients.size() == 0) {
                return null;
            }
            remoteView = new RemoteViews(context.getPackageName(),
                    android.R.layout.simple_list_item_1);
            remoteView.setTextViewText(android.R.id.text1,
                    "- " + mIngredients.get(position).getQuantity() + " " + mIngredients.get(position).getMeasure()  + " " + mIngredients.get(position).getIngredient());
            remoteView.setTextColor(android.R.id.text1, Color.BLACK);
        }
        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return position; // todo recipeId
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}