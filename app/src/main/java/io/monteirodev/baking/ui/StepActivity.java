package io.monteirodev.baking.ui;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import io.monteirodev.baking.R;
import timber.log.Timber;

import static io.monteirodev.baking.ui.RecipeActivity.INTENT_EXTRA_STEP_ID;

public class StepActivity extends AppCompatActivity implements StepFragment.OnStepChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        Toast.makeText(this, "Step id: " + getIntent().getIntExtra(INTENT_EXTRA_STEP_ID, -1), Toast.LENGTH_LONG).show();
        if (savedInstanceState == null) {
            StepFragment stepFragment = new StepFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.step_container, stepFragment)
                    .commit();
        }
    }

    @Override
    public void onStepChange(Uri uri) {
        Timber.d("on");
    }
}
