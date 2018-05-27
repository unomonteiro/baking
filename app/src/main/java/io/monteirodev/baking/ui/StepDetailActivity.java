package io.monteirodev.baking.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import io.monteirodev.baking.R;
import io.monteirodev.baking.models.Recipe;
import timber.log.Timber;

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
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.step_container, stepDetailFragment)
                    .commit();
        }
    }

    @Override
    public void onStepChange(Uri uri) {
        Timber.d("on");
    }
}
