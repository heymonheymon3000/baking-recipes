package baking.nanodegree.android.baking.ui.recipeVideo;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import baking.nanodegree.android.baking.R;
import baking.nanodegree.android.baking.persistence.entity.Step;
import baking.nanodegree.android.baking.utilities.Events;
import baking.nanodegree.android.baking.utilities.GlobalBus;


import static baking.nanodegree.android.baking.ui.recipe.RecipeActivity.RECIPE_ID;
import static baking.nanodegree.android.baking.ui.recipe.RecipeActivity.RECIPE_NAME;
import static baking.nanodegree.android.baking.ui.recipeDetails.RecipeDetailFragment.CURRENT_STEP_INDEX;

public class RecipeVideoActivity extends AppCompatActivity implements
        RecipeVideoFragment.OnStepListener {
    private String TAG = RecipeVideoActivity.class.getSimpleName();
    public static String STEPS = "STEPS";
    private long recipeId;
    private Integer currentStepIndex;
    private String recipeName;
    private List<Step> mSteps;
    private RecipeVideoFragment recipeVideoFragment;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if ((recipeVideoFragment != null) && recipeVideoFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "recipeVideoFragment",
                    recipeVideoFragment);
        }
        if(mSteps !=null) {
            outState.putParcelableArrayList(STEPS, (ArrayList<? extends Parcelable>) mSteps);
        }
        outState.putLong(RECIPE_ID, recipeId);
        outState.putInt(CURRENT_STEP_INDEX, currentStepIndex);
        outState.putString(RECIPE_NAME,recipeName);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_video);

        if(savedInstanceState != null) {
            recipeId = savedInstanceState.getLong(RECIPE_ID);
            currentStepIndex = savedInstanceState.getInt(CURRENT_STEP_INDEX);
            recipeName = savedInstanceState.getString(RECIPE_NAME);
            mSteps = savedInstanceState.getParcelableArrayList(STEPS);

        } else {
            Bundle selectedRecipeBundle = getIntent().getExtras();
            if(selectedRecipeBundle != null) {
                mSteps = selectedRecipeBundle.getParcelableArrayList(STEPS);
                recipeId = selectedRecipeBundle.getLong(RECIPE_ID);
                currentStepIndex = selectedRecipeBundle.getInt(CURRENT_STEP_INDEX);
                recipeName = selectedRecipeBundle.getString(RECIPE_NAME);
            }
        }

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        if (savedInstanceState != null) {
            recipeVideoFragment = (RecipeVideoFragment)
                    getSupportFragmentManager().getFragment(savedInstanceState, "recipeVideoFragment");
        } else {
            recipeVideoFragment = new RecipeVideoFragment();
            Bundle videoFragmentBundle = new Bundle();
            if(mSteps !=null) {
                videoFragmentBundle.putParcelableArrayList(STEPS, (ArrayList<? extends Parcelable>) mSteps);
            }

            videoFragmentBundle.putLong(RECIPE_ID, recipeId);
            videoFragmentBundle.putInt(CURRENT_STEP_INDEX, currentStepIndex);
            videoFragmentBundle.putString(RECIPE_NAME,recipeName);
            recipeVideoFragment.setArguments(videoFragmentBundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.video_container, recipeVideoFragment).commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onStepClick(long recipeId, int index, String recipeName) {
        Events.ActivityFragmentMessage message = new Events.ActivityFragmentMessage(index);
        GlobalBus.getBus().post(message);

        RecipeVideoFragment recipeVideoFragment = new RecipeVideoFragment();
        Bundle videoFragmentBundle = new Bundle();
        if(mSteps !=null) {
            videoFragmentBundle.putParcelableArrayList(STEPS, (ArrayList<? extends Parcelable>) mSteps);
        }
        videoFragmentBundle.putLong(RECIPE_ID, recipeId);
        videoFragmentBundle.putInt(CURRENT_STEP_INDEX, index);
        videoFragmentBundle.putString(RECIPE_NAME,recipeName);

        recipeVideoFragment.setArguments(videoFragmentBundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.video_container, recipeVideoFragment).commit();
    }
}
