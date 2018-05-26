package io.monteirodev.baking.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.monteirodev.baking.R;

public class StepDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ID_RECIPE_LOADER = 1;
    private static final int ID_STEP_LOADER = 2;
    private OnStepChangeListener mListener;

    Unbinder unbinder;
    @BindView(R.id.short_description_text_view)
    TextView mShortDescriptionTextView;
    @BindView(R.id.step_description_text_view)
    TextView mDescriptionTextView;
    @BindView(R.id.previous_button)
    Button mPreviousButton;
    @BindView(R.id.next_button)
    Button mNextButton;
    private int mStepId;

    public StepDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        mShortDescriptionTextView.setText(String.valueOf(mStepId));

        if (mStepId != 0) {
            getLoaderManager().initLoader(ID_STEP_LOADER, null, this);
        }
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStepChangeListener) {
            mListener = (OnStepChangeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnStepChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
        super.onDestroyView();
    }

    public void setStepId(int stepId) {
        mStepId = stepId;
    }

    @NonNull
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle args) {
        switch (loaderId) {

            case ID_RECIPE_LOADER:
//                return new CursorLoader(this,
//                        BakingProvider.Recipes.recipeWithId(mRecipeId),
//                        null,
//                        null,
//                        null,
//                        null);
                return null;

            case ID_STEP_LOADER:
                // todo
//                return new CursorLoader(this,
//                        BakingProvider.Steps.recipeSteps(mRecipeId),
//                        STEPS_PROJECTION,
//                        null,
//                        null,
//                        null);
                return null;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() != 0) {
            switch (loader.getId()) {
                case ID_RECIPE_LOADER:
                    // todo
                    break;
                case ID_STEP_LOADER:
                    // todo
                    // getLoaderManager().initLoader(ID_RECIPE_LOADER, null, this);
                    break;
                default:
                    throw new RuntimeException("Loader Not Implemented: " + loader.getId());
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<Cursor> loader) {
        switch (loader.getId()) {
            case ID_RECIPE_LOADER:
                // todo
                break;
            case ID_STEP_LOADER:
                // todo
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loader.getId());
        }
    }

    public interface OnStepChangeListener {
        void onStepChange(Uri uri);
    }
}
