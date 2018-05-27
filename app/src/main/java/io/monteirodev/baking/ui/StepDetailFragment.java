package io.monteirodev.baking.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.monteirodev.baking.R;
import io.monteirodev.baking.models.Step;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class StepDetailFragment extends Fragment {
    private static final String STEPS_KEY = "steps_key";
    private static final String STEP_INDEX_KEY = "step_index_key";

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    private OnStepChangeListener mOnStepChangeListener;

    Unbinder unbinder;
    @BindView(R.id.thumbnail_url_image_view)
    ImageView mThumbnailImageView;
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

    private SimpleExoPlayer mPlayer;

    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;
    private String mVideoURL;

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

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || mPlayer == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            playbackPosition = mPlayer.getCurrentPosition();
            currentWindow = mPlayer.getCurrentWindowIndex();
            playWhenReady = mPlayer.getPlayWhenReady();
            mPlayer.release();
            mPlayer = null;
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).
                createMediaSource(uri);
    }

    private MediaSource buildMediaSource4(Uri uri) {
        DataSource.Factory manifestDataSourceFactory = new DefaultHttpDataSourceFactory("ua");
        DashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(
                new DefaultHttpDataSourceFactory("ua", BANDWIDTH_METER));
        return new DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory)
                .createMediaSource(uri);
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        // todo
        FragmentActivity activity = getActivity();
        if (activity != null && activity.getResources().getConfiguration()
                .orientation == ORIENTATION_LANDSCAPE) {
            mPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
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

            mVideoURL = step.getVideoURL();

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

    private void initializePlayer() {
        if (mVideoURL == null || mVideoURL.isEmpty()) {
            mThumbnailImageView.setVisibility(VISIBLE);
            mPlayerView.setVisibility(INVISIBLE);
            return;
        }
        if (mPlayer == null) {
            mPlayer = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(getContext()),
                    new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(BANDWIDTH_METER)),
                    new DefaultLoadControl());
            mPlayerView.requestFocus();
            mPlayerView.setPlayer(mPlayer);
            mPlayer.setPlayWhenReady(playWhenReady);
        }
        MediaSource mediaSource = buildMediaSource(Uri.parse(mVideoURL));
        mPlayer.prepare(mediaSource);
        mThumbnailImageView.setVisibility(VISIBLE);
        mPlayerView.setVisibility(VISIBLE);
    }


    @OnClick(R.id.previous_button)
    void previousStep() {
        mStepIndex --;
        releasePlayer();
        updateStepViews();
        initializePlayer();
    }

    @OnClick(R.id.next_button)
    void nextStep() {
        mStepIndex++;
        releasePlayer();
        updateStepViews();
        initializePlayer();
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
