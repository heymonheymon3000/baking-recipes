package baking.nanodegree.android.baking.ui.widget;

import java.util.ArrayList;

import baking.nanodegree.android.baking.persistence.entity.Recipe;

public interface IRecipeLoader {
    void loadRecipes();

    ArrayList<Recipe> getLoadedRecipes();
}
