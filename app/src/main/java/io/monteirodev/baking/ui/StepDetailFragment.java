package io.monteirodev.baking.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.monteirodev.baking.R;
import io.monteirodev.baking.models.Step;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class StepDetailFragment extends Fragment {

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
            int stepIndex = step.getId();
            String shortDescription = step.getShortDescription();
            String description = step.getDescription();
            boolean isFirst = stepIndex == 0;
            boolean isLast = stepIndex == mSteps.size() -1;

            mShortDescriptionTextView.setText(shortDescription);
            if (description.equalsIgnoreCase(shortDescription)) {
                mDescriptionTextView.setVisibility(INVISIBLE);
            } else {
                mDescriptionTextView.setText(description);
            }
            mPreviousButton.setVisibility(isFirst ? INVISIBLE : VISIBLE);
            mNextButton.setVisibility(isLast ? INVISIBLE : VISIBLE);
        }
    }

    public interface OnStepChangeListener {
        void onStepChange(Uri uri);
    }
}
