package io.monteirodev.baking.ui;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import io.monteirodev.baking.R;
import timber.log.Timber;

import static io.monteirodev.baking.ui.RecipeActivity.INTENT_EXTRA_STEP_ID;

public class StepDetailActivity extends AppCompatActivity implements StepDetailFragment.OnStepChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int stepId = getIntent().getIntExtra(INTENT_EXTRA_STEP_ID, -1);
        if (savedInstanceState == null) {
            StepDetailFragment stepDetailFragment = new StepDetailFragment();
            stepDetailFragment.setStepId(stepId);
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
