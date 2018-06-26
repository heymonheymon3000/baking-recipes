package baking.nanodegree.android.baking.ui.recipeVideo;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import baking.nanodegree.android.baking.R;

import static baking.nanodegree.android.baking.ui.recipe.RecipeActivity.RECIPE_ID;
import static baking.nanodegree.android.baking.ui.recipe.RecipeActivity.RECIPE_NAME;
import static baking.nanodegree.android.baking.ui.recipeDetails.RecipeDetailFragment.CURRENT_STEP_INDEX;

public class RecipeVideoActivity extends AppCompatActivity implements
        RecipeVideoFragment.OnStepListener {

    private long recipeId;
    private Integer currentStepIndex;
    private String recipeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_video);

        if(savedInstanceState != null) {
            recipeId = savedInstanceState.getLong(RECIPE_ID);
            currentStepIndex = savedInstanceState.getInt(CURRENT_STEP_INDEX);
            recipeName = savedInstanceState.getString(RECIPE_NAME);
        } else {
            Bundle selectedRecipeBundle = getIntent().getExtras();
            if(selectedRecipeBundle != null) {
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

        RecipeVideoFragment recipeVideoFragment = new RecipeVideoFragment();
        Bundle videoFragmentBundle = new Bundle();
        videoFragmentBundle.putLong(RECIPE_ID, recipeId);
        videoFragmentBundle.putInt(CURRENT_STEP_INDEX, currentStepIndex);
        videoFragmentBundle.putString(RECIPE_NAME,recipeName);
        recipeVideoFragment.setArguments(videoFragmentBundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.video_container, recipeVideoFragment).commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onStepClick(long recipeId, int index, String recipeName) {
        RecipeVideoFragment recipeVideoFragment = new RecipeVideoFragment();
        Bundle videoFragmentBundle = new Bundle();
        videoFragmentBundle.putLong(RECIPE_ID, recipeId);
        videoFragmentBundle.putInt(CURRENT_STEP_INDEX, index);
        videoFragmentBundle.putString(RECIPE_NAME,recipeName);
        recipeVideoFragment.setArguments(videoFragmentBundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.video_container, recipeVideoFragment).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(RECIPE_ID, recipeId);
        outState.putInt(CURRENT_STEP_INDEX, currentStepIndex);
        outState.putString(RECIPE_NAME,recipeName);
    }
}
