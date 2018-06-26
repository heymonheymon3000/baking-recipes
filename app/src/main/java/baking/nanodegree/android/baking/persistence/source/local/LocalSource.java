package baking.nanodegree.android.baking.persistence.source.local;

import android.arch.lifecycle.LiveData;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import baking.nanodegree.android.baking.persistence.db.AppDatabase;
import baking.nanodegree.android.baking.persistence.entity.Ingredient;
import baking.nanodegree.android.baking.persistence.entity.Recipe;
import baking.nanodegree.android.baking.persistence.entity.Step;

public interface LocalSource {
    void getAll(LocalObserver<ArrayList<Recipe>> cursorFavoriteDataObserver);

    void getById(long id, LocalObserver<Cursor> cursorFavoriteDataObserver);

    void insert(Recipe recipe, LocalObserver<Uri> uriFavoriteDataObserver);

    void insertMany(ArrayList<Recipe> recipes, LocalObserver<Uri> uriFavoriteDataObserver);

    void update(Recipe recipe, LocalObserver<Integer> integerFavoriteDataObserver);

    void delete(long id, LocalObserver<Integer> integerFavoriteDataObserver);

    LiveData<List<Recipe>> getAllRecipes();

    LiveData<List<Ingredient>> getAllIngredientsByRecipeId(long id);

    LiveData<List<Step>> getAllStepsByRecipeId(long id);
}
