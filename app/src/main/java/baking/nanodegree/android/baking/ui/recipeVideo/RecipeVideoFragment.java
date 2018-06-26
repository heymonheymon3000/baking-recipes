package baking.nanodegree.android.baking.ui.recipeVideo;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.util.List;

import static baking.nanodegree.android.baking.ui.recipe.RecipeActivity.RECIPE_ID;
import static baking.nanodegree.android.baking.ui.recipe.RecipeActivity.RECIPE_NAME;
import static baking.nanodegree.android.baking.ui.recipeDetails.RecipeDetailFragment.CURRENT_STEP_INDEX;

import baking.nanodegree.android.baking.R;
import baking.nanodegree.android.baking.persistence.db.AppDatabase;
import baking.nanodegree.android.baking.persistence.entity.Step;
import baking.nanodegree.android.baking.ui.recipeDetails.RecipeDetailActivity;
import baking.nanodegree.android.baking.ui.recipeDetails.RecipeDetailFragment;
import baking.nanodegree.android.baking.ui.recipeDetails.RetrieveByRecipeIdViewModel;
import baking.nanodegree.android.baking.ui.recipeDetails.RetrieveByRecipeIdViewModelFactory;

public class RecipeVideoFragment extends Fragment implements ExoPlayer.EventListener {
    private List<Step> mSteps;
    private Integer currentStepIndex;
    private String recipeName;
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer simpleExoPlayer;
    private final static String TAG = RecipeVideoFragment.class.getSimpleName();
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private TextView stepTextView;
    private OnStepListener onStepListener;
    private long recipeId;

    public RecipeVideoFragment() {}

    public interface OnStepListener {
        void onStepClick(long recipeId, int index, String recipeName);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentManager fm = getFragmentManager();
        final RecipeDetailFragment recipeDetailFragment =
                (RecipeDetailFragment)fm.findFragmentById(R.id.master_list_recipe_card_fragment);

        if(savedInstanceState != null) {
            recipeId = savedInstanceState.getLong(RECIPE_ID);
            currentStepIndex = savedInstanceState.getInt(CURRENT_STEP_INDEX);
            recipeName = savedInstanceState.getString(RECIPE_NAME);
        } else {
            recipeId = getArguments().getLong(RECIPE_ID);
            currentStepIndex = getArguments().getInt(CURRENT_STEP_INDEX);
            recipeName = getArguments().getString(RECIPE_NAME);
        }

        final View view = inflater.inflate(R.layout.fragment_recipe_video, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(recipeName);

        simpleExoPlayerView =  view.findViewById(R.id.playerView);
        simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

        RetrieveByRecipeIdViewModelFactory factory =
                new RetrieveByRecipeIdViewModelFactory(AppDatabase.getInstance(getContext()),recipeId);
        final RetrieveByRecipeIdViewModel viewModel =
                ViewModelProviders.of(this, factory).get(RetrieveByRecipeIdViewModel.class);
        viewModel.getSteps().observe(this, new Observer<List<Step>>() {
            @Override
            public void onChanged(@Nullable List<Step> steps) {
                viewModel.getSteps().removeObserver(this);
                mSteps = steps;

                if((mSteps.get(currentStepIndex).getVideoURL().equals(""))) {
                    // Load the question mark as the background image until the user answers the question.
                    simpleExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                            (getResources(), R.drawable.baseline_videocam_off_white_48));
                }

                if (((view.getTag()!= null) && (view.getTag().equals("tablet-land") && isInLandscapeMode(getContext())))
                        || !isInLandscapeMode(getContext())) {
                    stepTextView =  view.findViewById(R.id.step_text_view);
                    stepTextView.setText(mSteps.get(currentStepIndex).getDescription());
                    if(getActivity() instanceof RecipeDetailActivity) {
                        onStepListener = (RecipeDetailActivity)getActivity();
                    } else {
                        onStepListener = (RecipeVideoActivity)getActivity();
                    }

                    if((null != view.findViewById(R.id.back_video_fab)) &&
                            (null != view.findViewById(R.id.next_video_fab))) {
                        FloatingActionButton backImageView = view.findViewById(R.id.back_video_fab);
                        backImageView.setEnabled(mSteps.get(currentStepIndex).getId() > 0);
                        backImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mSteps.get(currentStepIndex).getId() > 0) {
                                    if (simpleExoPlayer != null) {
                                        releasePlayer();
                                    }
                                    currentStepIndex = currentStepIndex - 1;
                                    onStepListener.onStepClick(recipeId, currentStepIndex, recipeName);
                                    if (getActivity() instanceof RecipeDetailActivity) {
                                        recipeDetailFragment.prevStep();
                                    }
                                }
                            }
                        });

                        FloatingActionButton nextImageView = view.findViewById(R.id.next_video_fab);
                        nextImageView.setEnabled(mSteps.get(currentStepIndex).getId() < mSteps.get(mSteps.size() - 1).getId());
                        nextImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mSteps.get(currentStepIndex).getId() < mSteps.get(mSteps.size() - 1).getId()) {
                                    if (simpleExoPlayer != null) {
                                        releasePlayer();
                                    }
                                    currentStepIndex = currentStepIndex + 1;
                                    onStepListener.onStepClick(recipeId, currentStepIndex + 1, recipeName);
                                    if (getActivity() instanceof RecipeDetailActivity) {
                                        recipeDetailFragment.nextStep();
                                    }
                                }
                            }
                        });
                    }
                }


                initializeMediaSession();
                initializePlayer(Uri.parse(mSteps.get(currentStepIndex).getVideoURL()));
            }
        });


        return view;
    }

    private boolean isInLandscapeMode( Context context ) {
        return (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    private void initializePlayer(Uri mediaUri){
        if(simpleExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("Baking");
            ExtractorsFactory extractor = new DefaultExtractorsFactory();

            MediaSource videoSource = new ExtractorMediaSource(mediaUri, dataSourceFactory, extractor, null, null);
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            // Set the ExoPlayer.EventListener to this activity.
            simpleExoPlayer.addListener(this);
            simpleExoPlayerView.setPlayer(simpleExoPlayer);

            simpleExoPlayer.prepare(videoSource);
            simpleExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        mMediaSession.setActive(false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(RECIPE_ID, recipeId);
        outState.putInt(CURRENT_STEP_INDEX, currentStepIndex);
        outState.putString(RECIPE_NAME,recipeName);
    }

    private void releasePlayer() {
        if(simpleExoPlayer != null) {
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    simpleExoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == ExoPlayer.STATE_READY)){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    simpleExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
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
    }
}
