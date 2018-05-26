package io.monteirodev.baking.ui;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.monteirodev.baking.R;
import io.monteirodev.baking.database.BakingProvider;
import io.monteirodev.baking.database.IngredientColumns;
import io.monteirodev.baking.database.RecipeColumns;
import io.monteirodev.baking.database.StepColumns;
import timber.log.Timber;

public class RecipeActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, RecipeDetailsAdapter.StepClickListener {

    public static final String INTENT_EXTRA_RECIPE_ID = "intent_extra_recipe_id";
    public static final String INTENT_EXTRA_STEP_ID = "intent_extra_step_id";

    private static final int ID_RECIPE_LOADER = 2;
    private static final int ID_INGREDIENTS_LOADER = 3;
    private static final int ID_STEPS_LOADER = 4;

    @BindView(R.id.recipe_details_recycler_view)
    RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;

    private LinearLayoutManager mLayoutManager;
    private RecipeDetailsAdapter mRecipeDetailsAdapter;
    private int mRecipeId;

    public static final String[] INGREDIENTS_PROJECTION = {
            IngredientColumns.QUANTITY,
            IngredientColumns.MEASURE,
            IngredientColumns.INGREDIENT
    };

    public static final String[] STEPS_PROJECTION = {
            StepColumns.ID,
            StepColumns.STEP,
            StepColumns.SHORT_DESCRIPTION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);

        String recipeName = getString(R.string.app_name);
        if (getIntent() != null && getIntent().hasExtra(INTENT_EXTRA_RECIPE_ID)) {
            mRecipeId = getIntent().getIntExtra(INTENT_EXTRA_RECIPE_ID, 0);
            if (mRecipeId > 0) {
                getSupportLoaderManager().restartLoader(ID_RECIPE_LOADER, null, this);
                getSupportLoaderManager().restartLoader(ID_INGREDIENTS_LOADER, null, this);
                getSupportLoaderManager().restartLoader(ID_STEPS_LOADER, null, this);
            }
        }
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle(recipeName);
        }

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecipeDetailsAdapter = new RecipeDetailsAdapter(this);
        mRecyclerView.setAdapter(mRecipeDetailsAdapter);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle args) {
        switch (loaderId) {

            case ID_RECIPE_LOADER:
                return new CursorLoader(this,
                        BakingProvider.Recipes.recipeWithId(mRecipeId),
                        null,
                        null,
                        null,
                        null);

            case ID_INGREDIENTS_LOADER:
                return new CursorLoader(this,
                        BakingProvider.Ingredients.recipeIngredients(mRecipeId),
                        INGREDIENTS_PROJECTION,
                        null,
                        null,
                        null);

            case ID_STEPS_LOADER:
                return new CursorLoader(this,
                        BakingProvider.Steps.recipeSteps(mRecipeId),
                        STEPS_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() != 0) {
            switch (loader.getId()) {
                case ID_RECIPE_LOADER:
                    setActionBartitle(data);
                    break;
                case ID_INGREDIENTS_LOADER:
                    smoothCursorSwap(ID_INGREDIENTS_LOADER, data);
                    break;
                case ID_STEPS_LOADER:
                    smoothCursorSwap(ID_STEPS_LOADER, data);
                    break;
                default:
                    throw new RuntimeException("Loader Not Implemented: " + loader.getId());
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        switch (loader.getId()) {
            case ID_RECIPE_LOADER:
                break;
            case ID_INGREDIENTS_LOADER:
                mRecipeDetailsAdapter.swapIngredientsCursor(null);
                break;
            case ID_STEPS_LOADER:
                mRecipeDetailsAdapter.swapStepsCursor(null);
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loader.getId());
        }
    }

    private void setActionBartitle(Cursor cursor) {
        if (cursor != null && cursor.moveToNext()) {
            String recipeName = cursor.getString(cursor.getColumnIndex(RecipeColumns.NAME));
            ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setTitle(recipeName);
            }
        }
    }

    private void smoothCursorSwap(int loaderId, Cursor data) {
        if (ID_INGREDIENTS_LOADER == loaderId) {
            mRecipeDetailsAdapter.swapIngredientsCursor(data);
        } else if (ID_STEPS_LOADER == loaderId) {
            mRecipeDetailsAdapter.swapStepsCursor(data);
        }
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
    }

    @Override
    public void onStepClick(int stepId) {
        Timber.d("Step clicked: " + stepId);
        Intent intent = new Intent(this, StepDetailActivity.class);
        intent.putExtra(INTENT_EXTRA_STEP_ID, stepId);
        startActivity(intent);
    }
}
