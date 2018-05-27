package io.monteirodev.baking.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.monteirodev.baking.R;
import io.monteirodev.baking.models.Recipe;
import io.monteirodev.baking.models.Step;
import timber.log.Timber;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class StepDetailFragment extends Fragment {
    private static final String STEPS_KEY = "steps_key";
    private static final String STEP_INDEX_KEY = "step_index_key";

    private OnStepChangeListener mOnStepChangeListener;

    Unbinder unbinder;
    @BindView(R.id.short_description_text_view)
    TextView mShortDescriptionTextView;
    @BindView(R.id.description_text_view)
    TextView mDescriptionTextView;
    @BindView(R.id.previous_button)
    Button mPreviousButton;
    @BindView(R.id.next_button)
    Button mNextButton;
    private ArrayList<Step> mSteps;
    private int mStepIndex;

    public StepDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        if(savedInstanceState != null) {
            mSteps = savedInstanceState.getParcelableArrayList(STEPS_KEY);
            mStepIndex = savedInstanceState.getInt(STEP_INDEX_KEY);
        }
        updateStepViews();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStepChangeListener) {
            mOnStepChangeListener = (OnStepChangeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnStepChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnStepChangeListener = null;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
        super.onDestroyView();
    }

    public void setSteps(ArrayList<Step> steps) {
        mSteps = steps;
    }

    public void setStepIndex(int stepIndex) {
        mStepIndex = stepIndex;
    }

    private void updateStepViews() {
        Context context = getContext();
        if (context != null && mSteps != null && mSteps.size() > 0 && mStepIndex > -1) {
            Step step = mSteps.get(mStepIndex);
            String shortDescription = step.getShortDescription();
            String description = step.getDescription();
            boolean isFirstStep = mStepIndex == 0;
            boolean isLastStep = mStepIndex == mSteps.size() -1;

            mShortDescriptionTextView.setText(shortDescription);
            if (description.equalsIgnoreCase(shortDescription)) {
                mDescriptionTextView.setVisibility(INVISIBLE);
            } else {
                mDescriptionTextView.setVisibility(VISIBLE);
                mDescriptionTextView.setText(description);
            }
            mPreviousButton.setVisibility(isFirstStep ? INVISIBLE : VISIBLE);
            mNextButton.setVisibility(isLastStep ? INVISIBLE : VISIBLE);
        }
    }
    @OnClick(R.id.previous_button)
    void previousStep() {
        mStepIndex --;
        updateStepViews();
    }

    @OnClick(R.id.next_button)
    void nextStep() {
        mStepIndex++;
        updateStepViews();
    }

    public interface OnStepChangeListener {
        void onStepChange(Uri uri);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(STEPS_KEY, mSteps);
        outState.putInt(STEP_INDEX_KEY, mStepIndex);
    }
}
