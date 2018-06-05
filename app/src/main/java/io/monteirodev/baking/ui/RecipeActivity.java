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
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.monteirodev.baking.R;
import io.monteirodev.baking.database.BakingProvider;
import io.monteirodev.baking.models.Ingredient;
import io.monteirodev.baking.models.Recipe;
import io.monteirodev.baking.models.Step;
import timber.log.Timber;

public class RecipeActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, RecipeDetailsAdapter.StepClickListener,
        StepDetailFragment.OnStepChangeListener {

    private static final String RECIPE_KEY = "recipe_key";
    private static final String STEP_DETAIL_FRAGMENT_KEY = "STEP_DETAIL_FRAGMENT_KEY";
    private static final int ID_INGREDIENTS_LOADER = 2;
    private static final int ID_STEPS_LOADER = 3;

    public static final String INTENT_EXTRA_RECIPE = "intent_extra_recipe";
    public static final String INTENT_EXTRA_STEP_INDEX = "intent_extra_step_id";

    @BindView(R.id.recipe_details_recycler_view)
    RecyclerView mRecyclerView;

    private RecipeDetailsAdapter mRecipeDetailsAdapter;
    private Recipe mRecipe;
    private boolean mIsTablet;
    private StepDetailFragment mStepDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);

        mIsTablet = getResources().getBoolean(R.bool.is_tablet);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(INTENT_EXTRA_RECIPE)) {
            mRecipe = intent.getParcelableExtra(INTENT_EXTRA_RECIPE);

        }
        if (mRecipe == null) {
            finish();
            Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
        }
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle(mRecipe.getName());
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecipeDetailsAdapter = new RecipeDetailsAdapter(this);
        mRecyclerView.setAdapter(mRecipeDetailsAdapter);

        int stepIndex = 0;
        if (savedInstanceState == null) {
            getSupportLoaderManager().restartLoader(ID_INGREDIENTS_LOADER, null, this);
            getSupportLoaderManager().restartLoader(ID_STEPS_LOADER, null, this);
            if (mIsTablet) {
                setStepDetailFragment(mRecipe.getSteps(), stepIndex);
            }
        } else {
            mRecipe = savedInstanceState.getParcelable(RECIPE_KEY);
            if (mRecipe != null) {
                mRecipeDetailsAdapter.setIngredients(mRecipe.getIngredients());
                mRecipeDetailsAdapter.setSteps(mRecipe.getSteps());
                if (mIsTablet) {
                    mStepDetailFragment = (StepDetailFragment) getSupportFragmentManager()
                            .getFragment(savedInstanceState, STEP_DETAIL_FRAGMENT_KEY);
                    replaceStepDetailFragment(mRecipe.getSteps(), stepIndex);
                }
            }
        }
    }

    private void setStepDetailFragment(ArrayList<Step> steps, int stepIndex) {
        mStepDetailFragment = new StepDetailFragment();
        replaceStepDetailFragment(steps, stepIndex);
    }

    private void replaceStepDetailFragment(ArrayList<Step> steps, int stepIndex) {
        mStepDetailFragment.setSteps(steps);
        mStepDetailFragment.setStepIndex(stepIndex);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.step_container, mStepDetailFragment)
                .commit();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle args) {
        switch (loaderId) {
            case ID_INGREDIENTS_LOADER:
                return new CursorLoader(this,
                        BakingProvider.Ingredients.recipeIngredients(mRecipe.getId()),
                        null, null, null, null);

            case ID_STEPS_LOADER:
                return new CursorLoader(this,
                        BakingProvider.Steps.recipeSteps(mRecipe.getId()),
                        null, null, null, null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() != 0) {
            int loaderId = loader.getId();
            switch (loaderId) {
                case ID_INGREDIENTS_LOADER:
                    if (mRecipe != null) {
                        ArrayList<Ingredient> ingredients = new ArrayList<>();
                        data.moveToPosition(-1);
                        while (data.moveToNext()) {
                            ingredients.add(new Ingredient(data));
                        }
                        mRecipe.setIngredients(ingredients);
                        mRecipeDetailsAdapter.setIngredients(mRecipe.getIngredients());
                    }
                    break;
                case ID_STEPS_LOADER:
                    if (mRecipe != null) {
                        ArrayList<Step> steps = new ArrayList<>();
                        data.moveToPosition(-1);
                        while (data.moveToNext()) {
                            steps.add(new Step(data));
                        }
                        mRecipe.setSteps(steps);
                        mRecipeDetailsAdapter.setSteps(mRecipe.getSteps());
                        if (mIsTablet) {
                            setStepDetailFragment(mRecipe.getSteps(), 0);
                        }
                    }
                    break;
                default:
                    throw new RuntimeException("Loader Not Implemented: " + loaderId);
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        switch (loader.getId()) {
           case ID_INGREDIENTS_LOADER:
                mRecipeDetailsAdapter.setIngredients(null);
                break;
            case ID_STEPS_LOADER:
                mRecipeDetailsAdapter.setSteps(null);
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loader.getId());
        }
    }

    @Override
    public void onStepClick(int stepIndex) {
        if (mIsTablet) {
            setStepDetailFragment(mRecipe.getSteps(), stepIndex);
        } else {
            Intent intent = new Intent(this, StepDetailActivity.class);
            intent.putExtra(INTENT_EXTRA_RECIPE, mRecipe);
            intent.putExtra(INTENT_EXTRA_STEP_INDEX, stepIndex);
            startActivity(intent);
        }
    }

    @Override
    public void onStepChange(int stepIndex) {
        Timber.d("onStepChange() stepIndex " + stepIndex);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECIPE_KEY, mRecipe);
        if (mIsTablet) {
            getSupportFragmentManager().putFragment(
                    outState, STEP_DETAIL_FRAGMENT_KEY, mStepDetailFragment);
        }
    }
}
