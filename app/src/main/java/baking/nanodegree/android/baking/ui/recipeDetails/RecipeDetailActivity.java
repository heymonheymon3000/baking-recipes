package baking.nanodegree.android.baking.ui.recipeDetails;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import baking.nanodegree.android.baking.R;

import baking.nanodegree.android.baking.ui.recipeVideo.RecipeVideoFragment;

import static baking.nanodegree.android.baking.ui.recipe.RecipeActivity.RECIPE_ID;
import static baking.nanodegree.android.baking.ui.recipe.RecipeActivity.RECIPE_NAME;
import static baking.nanodegree.android.baking.ui.recipeDetails.RecipeDetailFragment.CURRENT_STEP_INDEX;

public class RecipeDetailActivity extends AppCompatActivity implements
        RecipeVideoFragment.OnStepListener {

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
                RecipeVideoFragment recipeVideoFragment = new RecipeVideoFragment();
                Bundle videoFragmentBundle = new Bundle();
                videoFragmentBundle.putLong(RECIPE_ID, recipeBundle.getLong(RECIPE_ID));
                videoFragmentBundle.putInt(CURRENT_STEP_INDEX,
                        videoFragmentBundle.getInt(CURRENT_STEP_INDEX, 0));
                videoFragmentBundle.putString(RECIPE_NAME,recipeBundle.getString(RECIPE_NAME));
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
        RecipeVideoFragment recipeVideoFragment = new RecipeVideoFragment();
        Bundle videoFragmentBundle = new Bundle();
        videoFragmentBundle.putLong(RECIPE_ID, recipeId);
        videoFragmentBundle.putInt(CURRENT_STEP_INDEX, index);
        videoFragmentBundle.putString(RECIPE_NAME,recipeName);
        recipeVideoFragment.setArguments(videoFragmentBundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.video_container, recipeVideoFragment).commit();
    }
}
