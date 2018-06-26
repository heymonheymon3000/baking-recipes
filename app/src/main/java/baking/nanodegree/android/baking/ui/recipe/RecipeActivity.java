package baking.nanodegree.android.baking.ui.recipe;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import baking.nanodegree.android.baking.R;
import baking.nanodegree.android.baking.persistence.entity.Recipe;
import baking.nanodegree.android.baking.ui.recipeDetails.RecipeDetailActivity;
import baking.nanodegree.android.baking.utilities.SimpleIdlingResource;

public class RecipeActivity extends AppCompatActivity implements
        RecipeAdapter.OnRecipeCardClickListener {

    public static String RECIPE_ID = "RECIPE_ID";
    public static String RECIPE_NAME = "RECIPE_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
    }

    @Override
    public void onRecipeCardSelected(Recipe recipeCard) {
        Bundle recipeBundle = new Bundle();
        recipeBundle.putLong(RECIPE_ID, recipeCard.getId());
        recipeBundle.putString(RECIPE_NAME, recipeCard.getName());

        final Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtras(recipeBundle);
        startActivity(intent);
    }

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }
}
