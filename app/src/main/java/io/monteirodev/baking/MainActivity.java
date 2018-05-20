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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.monteirodev.baking.database.BakingProvider;
import io.monteirodev.baking.database.RecipeColumns;
import io.monteirodev.baking.sync.SyncUtils;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ID_RECIPE_LOADER = 1;

    @BindView(R.id.recipes_recycler_view)
    RecyclerView mRecyclerView;
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
        ButterKnife.bind(this);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecipeAdapter = new RecipeAdapter();
        mRecyclerView.setAdapter(mRecipeAdapter);

        getSupportLoaderManager().initLoader(ID_RECIPE_LOADER, null, this);
        SyncUtils.initialise(this);
    }

    @Override
    @NonNull
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle args) {
        switch (loaderId) {
            case ID_RECIPE_LOADER:
                return new CursorLoader(MainActivity.this, BakingProvider.Recipes.CONTENT_URI,
                        RECIPES_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case ID_RECIPE_LOADER:
                mRecipeAdapter.swapCursor(data);
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        switch (loader.getId()) {
            case ID_RECIPE_LOADER:
                mRecipeAdapter.swapCursor(null);
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loader.getId());
        }
    }
}
