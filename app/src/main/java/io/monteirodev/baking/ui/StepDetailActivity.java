package io.monteirodev.baking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import io.monteirodev.baking.R;
import io.monteirodev.baking.models.Recipe;
import io.monteirodev.baking.widget.WidgetIntentService;
import timber.log.Timber;

import static io.monteirodev.baking.ui.MainActivity.INVALID_RECIPE_ID;
import static io.monteirodev.baking.ui.MainActivity.RECIPE_ID_KEY;
import static io.monteirodev.baking.ui.RecipeActivity.INTENT_EXTRA_RECIPE;
import static io.monteirodev.baking.ui.RecipeActivity.INTENT_EXTRA_STEP_INDEX;

public class StepDetailActivity extends AppCompatActivity implements StepDetailFragment.OnStepChangeListener {

    private Recipe mRecipe;
    private int mStepIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(INTENT_EXTRA_RECIPE) &&
                intent.hasExtra(INTENT_EXTRA_STEP_INDEX)) {
            mRecipe = intent.getParcelableExtra(INTENT_EXTRA_RECIPE);
            mStepIndex = intent.getIntExtra(INTENT_EXTRA_STEP_INDEX, -1);
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
        if (savedInstanceState == null) {
            StepDetailFragment stepDetailFragment = new StepDetailFragment();
            stepDetailFragment.setSteps(mRecipe.getSteps());
            stepDetailFragment.setStepIndex(mStepIndex);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.step_container, stepDetailFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recipe_menu, menu);
        MenuItem item = menu.findItem(R.id.action_favourite);
        item.setIcon(isFavourite() ? R.drawable.ic_heart : R.drawable.ic_heart_outline);
        return true;
    }

    private boolean isFavourite() {
        int recipeId = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt(RECIPE_ID_KEY, INVALID_RECIPE_ID);
        return mRecipe.getId() == recipeId;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_favourite) {
            int newRecipeId = isFavourite() ? INVALID_RECIPE_ID : mRecipe.getId();
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putInt(RECIPE_ID_KEY, newRecipeId)
                    .apply();
            WidgetIntentService.startActionUpdateSelectedRecipe(this);
            item.setIcon(isFavourite() ? R.drawable.ic_heart : R.drawable.ic_heart_outline);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStepChange(int stepIndex) {
        Timber.d("onStepChange() stepIndex " + stepIndex);
    }
}
