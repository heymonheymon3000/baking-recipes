package baking.nanodegree.android.baking.ui.recipe;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import baking.nanodegree.android.baking.R;
import baking.nanodegree.android.baking.persistence.db.AppDatabase;
import baking.nanodegree.android.baking.persistence.entity.Recipe;
import baking.nanodegree.android.baking.persistence.entity.Step;
import baking.nanodegree.android.baking.ui.recipeDetails.RecipeDetailActivity;
import baking.nanodegree.android.baking.ui.recipeDetails.RetrieveByRecipeIdViewModel;
import baking.nanodegree.android.baking.ui.recipeDetails.RetrieveByRecipeIdViewModelFactory;
import baking.nanodegree.android.baking.utilities.Constants;
import baking.nanodegree.android.baking.utilities.SimpleIdlingResource;

public class RecipeActivity extends AppCompatActivity implements
        RecipeAdapter.OnRecipeCardClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
    }

    @Override
    public void onRecipeCardSelected(Recipe recipeCard) {
        final Bundle recipeBundle = new Bundle();
        recipeBundle.putLong(Constants.RECIPE_ID, recipeCard.getId());
        recipeBundle.putString(Constants.RECIPE_NAME, recipeCard.getName());

        RetrieveByRecipeIdViewModelFactory factory =
                new RetrieveByRecipeIdViewModelFactory(AppDatabase
                        .getInstance(getBaseContext()),recipeCard.getId());

        final RetrieveByRecipeIdViewModel viewModel =
                ViewModelProviders.of(this, factory)
                        .get(RetrieveByRecipeIdViewModel.class);

        final Context context = this;
        viewModel.getSteps().observe(this, new Observer<List<Step>>() {
            @Override
            public void onChanged(@Nullable List<Step> steps) {
                viewModel.getSteps().removeObserver(this);
                recipeBundle.putParcelableArrayList(Constants.STEPS, (ArrayList<? extends Parcelable>) steps);

                final Intent intent = new Intent(context, RecipeDetailActivity.class);
                intent.putExtras(recipeBundle);
                startActivity(intent);
            }
        });
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
