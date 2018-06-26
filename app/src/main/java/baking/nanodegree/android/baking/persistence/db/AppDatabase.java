package baking.nanodegree.android.baking.persistence.db;

import android.arch.persistence.room.Database;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import baking.nanodegree.android.baking.persistence.dao.IngredientDao;
import baking.nanodegree.android.baking.persistence.dao.RecipeDao;
import baking.nanodegree.android.baking.persistence.dao.StepDao;
import baking.nanodegree.android.baking.persistence.entity.Ingredient;
import baking.nanodegree.android.baking.persistence.entity.Recipe;
import baking.nanodegree.android.baking.persistence.entity.Step;

@Database(entities = {Recipe.class, Ingredient.class, Step.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private volatile static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if(INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "baking_recipe.db").build();
                }
            }
        }

        return INSTANCE;
    }

    public abstract RecipeDao recipeDao();

    public abstract IngredientDao ingredientDao();

    public abstract StepDao stepDao();
}
