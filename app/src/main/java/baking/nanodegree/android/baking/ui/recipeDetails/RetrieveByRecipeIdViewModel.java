package baking.nanodegree.android.baking.ui.recipeDetails;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import baking.nanodegree.android.baking.persistence.db.AppDatabase;
import baking.nanodegree.android.baking.persistence.entity.Ingredient;
import baking.nanodegree.android.baking.persistence.entity.Step;

public class RetrieveByRecipeIdViewModel extends ViewModel {

    private LiveData<List<Ingredient>> ingredients;
    private LiveData<List<Step>> steps;

    public RetrieveByRecipeIdViewModel(AppDatabase appDatabase, long recipeId) {
        ingredients = appDatabase.ingredientDao().getByRecipeId(recipeId);
        steps = appDatabase.stepDao().getByRecipeId(recipeId);
    }

    public LiveData<List<Ingredient>> getIngredients() {
        return ingredients;
    }

    public LiveData<List<Step>> getSteps() {
        return steps;
    }
}
