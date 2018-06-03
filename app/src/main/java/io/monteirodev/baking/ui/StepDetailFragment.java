package io.monteirodev.baking.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.monteirodev.baking.R;
import io.monteirodev.baking.models.Step;
import timber.log.Timber;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class StepDetailFragment extends Fragment {
    private static final String STEPS_KEY = "steps_key";
    private static final String STEP_INDEX_KEY = "step_index_key";
    private static final String PLAYER_POSITION = "player_position";

    @BindView(R.id.video_placeholder)
    TextView mThumbnailImageView;
    @BindView(R.id.video_view)
    PlayerView mPlayerView;
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

    private OnStepChangeListener mOnStepChangeListener;
    private Unbinder unbinder;
    private SimpleExoPlayer mPlayer;

    private long mPlayerPosition;
    private boolean playWhenReady = true;
    private String mVideoURL;

    public StepDetailFragment() {
        // Required empty public constructor
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        if (savedInstanceState != null) {
            mSteps = savedInstanceState.getParcelableArrayList(STEPS_KEY);
            mStepIndex = savedInstanceState.getInt(STEP_INDEX_KEY);
            mPlayerPosition = savedInstanceState.getLong(PLAYER_POSITION, C.TIME_UNSET);
        } else {
            mPlayerPosition = C.TIME_UNSET;
        }
        updateStepViews();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPlayer != null) {
            if (mPlayerPosition != C.TIME_UNSET) {
                mPlayer.seekTo(mPlayerPosition);
            }
            mPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPlayer != null) {
            mPlayerPosition = mPlayer.getCurrentPosition();
            releasePlayer();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(STEPS_KEY, mSteps);
        outState.putInt(STEP_INDEX_KEY, mStepIndex);
        outState.putLong(PLAYER_POSITION, mPlayerPosition);
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnStepChangeListener = null;
    }

    public void setSteps(ArrayList<Step> steps) {
        mSteps = steps;
    }

    public void setStepIndex(int stepIndex) {
        mStepIndex = stepIndex;
    }

    @OnClick(R.id.previous_button)
    void previousStep() {
        if (mStepIndex > 0) {
            mStepIndex--;
        }
        releasePlayer();
        updateStepViews();
        initializePlayer();
    }

    @OnClick(R.id.next_button)
    void nextStep() {
        Timber.d("step index " + mStepIndex + ", mSteps.size() " + mSteps.size());
        int maxIndex = mSteps.size() + 1;
        if (mStepIndex < maxIndex) {
            mStepIndex++;
            Timber.d("step index " + mStepIndex);
        }
        releasePlayer();
        updateStepViews();
        initializePlayer();
    }

    private void updateStepViews() {
        Context context = getContext();
        if (context != null && mSteps != null && mSteps.size() > 0 &&
                mStepIndex > -1 && mStepIndex < mSteps.size()) {
            Step step = mSteps.get(mStepIndex);

            mVideoURL = step.getVideoURL();

            String shortDescription = step.getShortDescription();
            String description = step.getDescription();
            boolean isFirstStep = mStepIndex == 0;
            boolean isLastStep = mStepIndex == mSteps.size() -1;

            mShortDescriptionTextView.setText(shortDescription);
            mDescriptionTextView.setText(description);
            boolean showDescription = description.equalsIgnoreCase(shortDescription);

            handleVisibilities(context, isFirstStep, isLastStep, showDescription);
        }
    }

    private void handleVisibilities(@NonNull Context context, boolean isFirstStep,
                                    boolean isLastStep, boolean showDescription) {
        Resources resources = context.getResources();
        boolean isTablet = resources.getBoolean(R.bool.is_tablet);
        int orientation = resources.getConfiguration().orientation;

        if (isTablet) {
            mThumbnailImageView.setVisibility(mVideoURL.isEmpty() ? VISIBLE : INVISIBLE);
            mPlayerView.setVisibility(mVideoURL.isEmpty() ? INVISIBLE : VISIBLE);
            mShortDescriptionTextView.setVisibility(GONE);
            mPreviousButton.setVisibility(GONE);
            mNextButton.setVisibility(GONE);
        } else if (orientation == ORIENTATION_LANDSCAPE) {
            if (mVideoURL.isEmpty()) {
                mPlayerView.setVisibility(GONE);
                mThumbnailImageView.setVisibility(GONE);
                mShortDescriptionTextView.setVisibility(VISIBLE);
                mDescriptionTextView.setVisibility(showDescription ? INVISIBLE : VISIBLE);
                mPreviousButton.setVisibility(isFirstStep ? INVISIBLE : VISIBLE);
                mNextButton.setVisibility(isLastStep ? INVISIBLE : VISIBLE);
            } else {
                mPlayerView.setVisibility(View.VISIBLE);
                mThumbnailImageView.setVisibility(GONE);
                mShortDescriptionTextView.setVisibility(GONE);
                mDescriptionTextView.setVisibility(GONE);
                mPreviousButton.setVisibility(GONE);
                mNextButton.setVisibility(GONE);
                hideSystemUi();
            }
        } else if (orientation == ORIENTATION_PORTRAIT) {
            if (mVideoURL.isEmpty()) {
                mPlayerView.setVisibility(INVISIBLE);
                mThumbnailImageView.setVisibility(VISIBLE);
            } else {
                mPlayerView.setVisibility(VISIBLE);
                mThumbnailImageView.setVisibility(INVISIBLE);
            }
            mPreviousButton.setVisibility(isFirstStep ? INVISIBLE : VISIBLE);
            mNextButton.setVisibility(isLastStep ? INVISIBLE : VISIBLE);
        }
    }

    private void initializePlayer() {
//        if (mVideoURL == null || mVideoURL.isEmpty()) {
//            mThumbnailImageView.setVisibility(VISIBLE);
//            mPlayerView.setVisibility(INVISIBLE);
//            return;
//        }
        if (mPlayer == null && mVideoURL != null && !mVideoURL.isEmpty()) {
            mPlayer = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(getContext()),
                    new DefaultTrackSelector(
                            new AdaptiveTrackSelection.Factory(
                                    new DefaultBandwidthMeter())),
                    new DefaultLoadControl());
            mPlayerView.requestFocus();
            mPlayerView.setPlayer(mPlayer);
            mPlayer.setPlayWhenReady(playWhenReady);
            MediaSource mediaSource = buildMediaSource(Uri.parse(mVideoURL));
            mPlayer.prepare(mediaSource);
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("ua"))
                .createMediaSource(uri);
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        mPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            playWhenReady = mPlayer.getPlayWhenReady();
            mPlayer.release();
            mPlayer = null;
        }
    }

    public interface OnStepChangeListener {
        void onStepChange(int stepIndex);
    }
}
