package baking.nanodegree.android.baking.ui.recipeVideo;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;
import java.util.Objects;

import static baking.nanodegree.android.baking.ui.recipe.RecipeActivity.RECIPE_ID;
import static baking.nanodegree.android.baking.ui.recipe.RecipeActivity.RECIPE_NAME;
import static baking.nanodegree.android.baking.ui.recipeDetails.RecipeDetailFragment.CURRENT_STEP_INDEX;

import baking.nanodegree.android.baking.R;
import baking.nanodegree.android.baking.persistence.entity.Step;
import baking.nanodegree.android.baking.ui.recipeDetails.RecipeDetailActivity;
import baking.nanodegree.android.baking.ui.recipeDetails.RecipeDetailFragment;

public class RecipeVideoFragment extends Fragment
        implements ExoPlayer.EventListener {

    private String TAG = RecipeVideoFragment.class.getSimpleName();
    public static String STEPS = "STEPS";
    private List<Step> mSteps;
    private Integer currentStepIndex;
    private String recipeName;
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer simpleExoPlayer;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private OnStepListener onStepListener;
    private long recipeId;
    private String videoUrl;
    private long currentVideoPosition = 0;

    public RecipeVideoFragment() {}

    public interface OnStepListener {
        void onStepClick(long recipeId, int index, String recipeName);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try{
            onStepListener = (OnStepListener) activity;
        } catch(ClassCastException e) {
            throw new ClassCastException(activity.toString()  + " must implement OnStepListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(getArguments() != null) {
            recipeId = Objects.requireNonNull(getArguments()).getLong(RECIPE_ID);
            currentStepIndex = getArguments().getInt(CURRENT_STEP_INDEX);
            recipeName = getArguments().getString(RECIPE_NAME);
            mSteps = getArguments().getParcelableArrayList(STEPS);
        }

        final View view = inflater.inflate(R.layout.fragment_recipe_video, container,
                false);

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity()))
                .getSupportActionBar()).setTitle(recipeName);

        simpleExoPlayerView =  view.findViewById(R.id.playerView);
        simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

        if (getArguments() != null && mSteps.size()-1 >= currentStepIndex) {
            if ((Objects.requireNonNull(mSteps)
                    .get(currentStepIndex).getVideoURL().equals(""))) {
                simpleExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                        (getResources(), R.drawable.baseline_videocam_off_white_48));
            }

            if (((view.getTag() != null) && (view.getTag().equals("tablet-land") &&
                    isInLandscapeMode(Objects.requireNonNull(getContext()))))
                    || !isInLandscapeMode(getContext())) {
                TextView stepTextView = view.findViewById(R.id.step_text_view);
                stepTextView.setText(mSteps.get(currentStepIndex).getDescription());

                if ((null != view.findViewById(R.id.back_video_fab)) &&
                        (null != view.findViewById(R.id.next_video_fab))) {
                    FloatingActionButton backImageView =
                            view.findViewById(R.id.back_video_fab);
                    backImageView.setEnabled(currentStepIndex > 0);
                    backImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (currentStepIndex > 0) {
                                currentStepIndex = currentStepIndex - 1;
                                onStepListener.onStepClick(recipeId, currentStepIndex, recipeName);
                                if (getActivity() instanceof RecipeDetailActivity) {
                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                    RecipeDetailFragment recipeDetailFragment =
                                            (RecipeDetailFragment) Objects.requireNonNull(fm)
                                                    .findFragmentById(R.id.master_list_recipe_card_fragment);


                                    recipeDetailFragment.prevStep();
                                }
                            }
                        }
                    });

                    FloatingActionButton nextImageView = view.findViewById(R.id.next_video_fab);
                    nextImageView.setEnabled(currentStepIndex < mSteps.size() - 1);

                    nextImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(currentStepIndex < mSteps.size() - 1) {
                                currentStepIndex = currentStepIndex + 1;
                                onStepListener.onStepClick(recipeId, currentStepIndex, recipeName);
                                if (getActivity() instanceof RecipeDetailActivity) {

                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                    RecipeDetailFragment recipeDetailFragment =
                                            (RecipeDetailFragment) Objects.requireNonNull(fm)
                                                    .findFragmentById(R.id.master_list_recipe_card_fragment);

                                    recipeDetailFragment.nextStep();
                                }
                            }
                        }
                    });
                }
            }

            if (mSteps.get(currentStepIndex).getVideoURL() != null &&
                    !mSteps.get(currentStepIndex).getVideoURL().equalsIgnoreCase("")) {
                videoUrl = mSteps.get(currentStepIndex).getVideoURL();
                initializePlayer();
            } else {
                simpleExoPlayerView.setVisibility(View.GONE);
            }
        }

        return view;
    }

    private boolean isInLandscapeMode( Context context ) {
        return (context.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(null != simpleExoPlayer) {
            currentVideoPosition = simpleExoPlayer.getCurrentPosition();
        } else {
            currentVideoPosition = 0;
        }

        releasePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        initializePlayer();
    }

    private void initializePlayer() {
        initializeMediaSession();
        initializeVideoPlayer();
    }

    private void initializeMediaSession() {
        if (mMediaSession == null) {
            mMediaSession = new MediaSessionCompat(Objects.requireNonNull(getActivity()), "Recipe");
            mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
            mMediaSession.setMediaButtonReceiver(null);

            mStateBuilder = new PlaybackStateCompat.Builder().setActions(
                    PlaybackStateCompat.ACTION_PLAY |
                            PlaybackStateCompat.ACTION_PAUSE |
                            PlaybackStateCompat.ACTION_PLAY_PAUSE);

            mMediaSession.setPlaybackState(mStateBuilder.build());

            mMediaSession.setCallback(new MediaSessionCompat.Callback() {
                @Override
                public void onPlay() {
                    simpleExoPlayer.setPlayWhenReady(true);
                }

                @Override
                public void onPause() {
                    simpleExoPlayer.setPlayWhenReady(false);
                }

                @Override
                public void onSkipToPrevious() {
                    simpleExoPlayer.seekTo(0);
                }
            });

            mMediaSession.setActive(true);
        }
    }

    private void initializeVideoPlayer() {
        if (simpleExoPlayer == null && videoUrl != null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            simpleExoPlayer =
                    ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            simpleExoPlayer.seekTo(currentVideoPosition);
            simpleExoPlayerView.setPlayer(simpleExoPlayer);

            simpleExoPlayer.addListener(this);

            String userAgent = Util.getUserAgent(getActivity(), "Recipe");
            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(videoUrl),
                    new DefaultDataSourceFactory(
                    Objects.requireNonNull(getActivity()), userAgent),
                    new DefaultExtractorsFactory(), null, null);
            simpleExoPlayer.prepare(mediaSource);
            simpleExoPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }

        if (mMediaSession != null) {
            mMediaSession.setActive(false);
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    simpleExoPlayer.getCurrentPosition(), 1f);
        } else if (playbackState == ExoPlayer.STATE_READY) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    simpleExoPlayer.getCurrentPosition(), 1f);
        }

        if (mStateBuilder != null) {
            mMediaSession.setPlaybackState(mStateBuilder.build());
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {}

    @Override
    public void onPositionDiscontinuity() {}

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {}

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {}

    @Override
    public void onLoadingChanged(boolean isLoading) {}
}
