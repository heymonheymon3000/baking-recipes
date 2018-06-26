package baking.nanodegree.android.baking.ui.widget;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import baking.nanodegree.android.baking.persistence.entity.Recipe;
import baking.nanodegree.android.baking.ui.recipe.RecipeViewModel;
import baking.nanodegree.android.baking.ui.recipe.RecipeViewModelFactory;

public class RecipeOptionDialogPresenter implements IRecipeLoader {
    private final IOption options;
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private Fragment fragment;

    public RecipeOptionDialogPresenter(Fragment fragment, IOption options) {
        this.options = options;
        this.fragment = fragment;
    }

    @Override
    public void loadRecipes() {
        RecipeViewModelFactory recipeViewModelFactory =
                new RecipeViewModelFactory(fragment.getActivity().getApplication());

        RecipeViewModel recipeViewModel = ViewModelProviders.of(fragment,
                recipeViewModelFactory).get(RecipeViewModel.class);

        LiveData<List<Recipe>> allRecipes = recipeViewModel.getAllRecipes();

        allRecipes.observe(fragment, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> cachedRecipes) {
                recipes.clear();
                recipes.addAll(cachedRecipes);
                options.updateRecipeList();
            }
        });
    }

    @Override
    public ArrayList<Recipe> getLoadedRecipes() {
        return recipes;
    }
}