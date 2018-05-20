package io.monteirodev.baking;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import io.monteirodev.baking.database.BakingProvider;
import io.monteirodev.baking.database.RecipeColumns;
import io.monteirodev.baking.sync.SyncUtils;

public class MainActivity extends AppCompatActivity {

    private static final int ID_RECIPE_LOADER = 0;

    private RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    private RecipeAdapter mRecipeAdapter;


    static final String[] RECIPES_PROJECTION = {
            RecipeColumns.NAME
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

        getSupportLoaderManager().initLoader(ID_RECIPE_LOADER, null, mRecipeLoaderCallback);
        SyncUtils.initialise(this);
    }

    private LoaderManager.LoaderCallbacks<Cursor> mRecipeLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            return new CursorLoader(MainActivity.this, BakingProvider.Recipes.CONTENT_URI,
                    RECIPES_PROJECTION,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            mRecipeAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            mRecipeAdapter.swapCursor(null);
        }
    };
}
