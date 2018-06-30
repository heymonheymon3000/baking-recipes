package baking.nanodegree.android.baking.ui.recipeVideo;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import baking.nanodegree.android.baking.R;
import baking.nanodegree.android.baking.persistence.entity.Step;
import baking.nanodegree.android.baking.utilities.Constants;
import baking.nanodegree.android.baking.utilities.Events;
import baking.nanodegree.android.baking.utilities.GlobalBus;

public class RecipeVideoActivity extends AppCompatActivity implements
        RecipeVideoFragment.OnStepListener {
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
            outState.putParcelableArrayList(Constants.STEPS,
                    (ArrayList<? extends Parcelable>) mSteps);
        }
        outState.putLong(Constants.RECIPE_ID, recipeId);
        outState.putInt(Constants.CURRENT_STEP_INDEX, currentStepIndex);
        outState.putString(Constants.RECIPE_NAME,recipeName);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_video);

        if(savedInstanceState != null) {
            recipeId = savedInstanceState.getLong(Constants.RECIPE_ID);
            currentStepIndex = savedInstanceState.getInt(Constants.CURRENT_STEP_INDEX);
            recipeName = savedInstanceState.getString(Constants.RECIPE_NAME);
            mSteps = savedInstanceState.getParcelableArrayList(Constants.STEPS);

        } else {
            Bundle selectedRecipeBundle = getIntent().getExtras();
            if(selectedRecipeBundle != null) {
                mSteps = selectedRecipeBundle.getParcelableArrayList(Constants.STEPS);
                recipeId = selectedRecipeBundle.getLong(Constants.RECIPE_ID);
                currentStepIndex = selectedRecipeBundle.getInt(Constants.CURRENT_STEP_INDEX);
                recipeName = selectedRecipeBundle.getString(Constants.RECIPE_NAME);
            }
        }

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        if (savedInstanceState != null) {
            recipeVideoFragment = (RecipeVideoFragment)
                    getSupportFragmentManager().getFragment(savedInstanceState,
                            "recipeVideoFragment");
        } else {
            recipeVideoFragment = new RecipeVideoFragment();
            Bundle videoFragmentBundle = new Bundle();
            if(mSteps !=null) {
                videoFragmentBundle.putParcelableArrayList(Constants.STEPS,
                        (ArrayList<? extends Parcelable>) mSteps);
            }
            videoFragmentBundle.putLong(Constants.RECIPE_ID, recipeId);
            videoFragmentBundle.putInt(Constants.CURRENT_STEP_INDEX, currentStepIndex);
            videoFragmentBundle.putString(Constants.RECIPE_NAME,recipeName);
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
            videoFragmentBundle.putParcelableArrayList(Constants.STEPS,
                    (ArrayList<? extends Parcelable>) mSteps);
        }
        videoFragmentBundle.putLong(Constants.RECIPE_ID, recipeId);
        videoFragmentBundle.putInt(Constants.CURRENT_STEP_INDEX, index);
        videoFragmentBundle.putString(Constants.RECIPE_NAME,recipeName);

        recipeVideoFragment.setArguments(videoFragmentBundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.video_container,
                recipeVideoFragment).commit();
    }
}
