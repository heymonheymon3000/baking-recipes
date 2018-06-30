package baking.nanodegree.android.baking.ui.recipeDetails;

import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.List;

import baking.nanodegree.android.baking.R;

import baking.nanodegree.android.baking.persistence.entity.Step;
import baking.nanodegree.android.baking.ui.recipeVideo.RecipeVideoFragment;
import baking.nanodegree.android.baking.utilities.Constants;
import baking.nanodegree.android.baking.utilities.Events;
import baking.nanodegree.android.baking.utilities.GlobalBus;

public class RecipeDetailActivity extends AppCompatActivity implements
        RecipeVideoFragment.OnStepListener {

    private List<Step> mSteps;
    private int currentStepIndex = 0;

    @Override
    protected void onStart() {
        super.onStart();
        GlobalBus.getBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GlobalBus.getBus().unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        if (null != findViewById(R.id.video_container)) {
            Bundle recipeBundle = getIntent().getExtras();
            if(recipeBundle != null) {
                if(savedInstanceState != null &&
                        savedInstanceState.containsKey(Constants.CURRENT_STEP_INDEX)) {
                    currentStepIndex = savedInstanceState.getInt(Constants.CURRENT_STEP_INDEX);
                } else {
                    currentStepIndex = recipeBundle.getInt(Constants.CURRENT_STEP_INDEX,
                            0);
                }

                Events.ActivityFragmentMessage message =
                        new Events.ActivityFragmentMessage(currentStepIndex);
                GlobalBus.getBus().post(message);

                RecipeVideoFragment recipeVideoFragment = new RecipeVideoFragment();
                Bundle videoFragmentBundle = new Bundle();
                videoFragmentBundle.putLong(Constants.RECIPE_ID,
                        recipeBundle.getLong(Constants.RECIPE_ID));
                videoFragmentBundle.putInt(Constants.CURRENT_STEP_INDEX, currentStepIndex);
                videoFragmentBundle.putString(Constants.RECIPE_NAME,
                        recipeBundle.getString(Constants.RECIPE_NAME));
                videoFragmentBundle.putParcelableArrayList(Constants.STEPS,
                        recipeBundle.getParcelableArrayList(Constants.STEPS));
                mSteps = recipeBundle.getParcelableArrayList(Constants.STEPS);
                recipeVideoFragment.setArguments(videoFragmentBundle);

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.video_container, recipeVideoFragment).commit();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onStepClick(long recipeId, int index, String recipeName) {
        currentStepIndex = index;
        RecipeVideoFragment recipeVideoFragment = new RecipeVideoFragment();
        Bundle videoFragmentBundle = new Bundle();
        videoFragmentBundle.putLong(Constants.RECIPE_ID, recipeId);
        videoFragmentBundle.putInt(Constants.CURRENT_STEP_INDEX, index);
        videoFragmentBundle.putString(Constants.RECIPE_NAME,recipeName);
        videoFragmentBundle.putParcelableArrayList(Constants.STEPS,
                (ArrayList<? extends Parcelable>) mSteps);
        recipeVideoFragment.setArguments(videoFragmentBundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.video_container,
                recipeVideoFragment).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.CURRENT_STEP_INDEX, currentStepIndex);
    }

    @Subscribe
    public void onMessageEvent(Events.FragmentActivityMessage event) {
        currentStepIndex = event.getcurrentStepIndex();
    }
}
