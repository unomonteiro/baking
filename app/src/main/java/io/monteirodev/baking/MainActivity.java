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

import java.util.ArrayList;
import java.util.List;

import io.monteirodev.baking.api.BakingClient;
import io.monteirodev.baking.api.BakingInterface;
import io.monteirodev.baking.database.BakingProvider;
import io.monteirodev.baking.database.RecipeColumns;
import io.monteirodev.baking.models.Recipe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID_RECIPES = 0;

    private ArrayList<Recipe> mRecipeList;
    private RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    private RecipeAdapter mRecipeAdapter;

    BakingInterface mBakingInterface;

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

        mBakingInterface = BakingClient.getClient().create(BakingInterface.class);
        Call<List<Recipe>> call = mBakingInterface.getRecipes();
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                mRecipeList = new ArrayList<>(response.body());
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                mRecipeList = null;
            }
        });

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, BakingProvider.Recipes.CONTENT_URI,
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
}
