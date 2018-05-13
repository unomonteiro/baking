package io.monteirodev.baking;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.SimpleCursorAdapter;

import io.monteirodev.baking.provider.BakingContract;
import io.monteirodev.baking.provider.BakingProvider;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID_RECIPES = 0;

    private RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    private RecipeAdapter mRecipeAdapter;

    static final String[] RECIPES_PROJECTION = {
            BakingContract.COLUMN_NAME
    };

    static final int COL_NUM_NAME = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recipes_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecipeAdapter = new RecipeAdapter();
        mRecyclerView.setAdapter(mRecipeAdapter);
        getContentResolver().delete(BakingProvider.BakingRecipes.CONTENT_URI,null, null);
        getSupportLoaderManager().initLoader(LOADER_ID_RECIPES, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, BakingProvider.BakingRecipes.CONTENT_URI,
                RECIPES_PROJECTION,
                null,
                null,
                BakingContract.COLUMN_NAME + " DESC");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mRecipeAdapter.swapCursor(data);
        if (data == null || data.getCount() == 0) {
            // dummy data
            ContentValues values = new ContentValues();
            values.put(BakingContract.COLUMN_NAME, "Nutella Pie");
            getContentResolver().insert(BakingProvider.BakingRecipes.CONTENT_URI, values);

            values.put(BakingContract.COLUMN_NAME, "Brownies");
            getContentResolver().insert(BakingProvider.BakingRecipes.CONTENT_URI, values);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mRecipeAdapter.swapCursor(null);
    }
}
