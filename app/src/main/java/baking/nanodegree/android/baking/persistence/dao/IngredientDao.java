package baking.nanodegree.android.baking.persistence.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import java.util.List;

import baking.nanodegree.android.baking.persistence.db.RecipeContract;
import baking.nanodegree.android.baking.persistence.entity.Ingredient;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface IngredientDao {
    @Query("SELECT COUNT(*) FROM " + RecipeContract.IngredientEntry.TABLE_NAME)
    int count();

    @Insert(onConflict = REPLACE)
    long insert(Ingredient ingredient);

    @Insert(onConflict = REPLACE)
    long[] insertAll(Ingredient[] ingredients);

    @Query("SELECT * FROM " + RecipeContract.IngredientEntry.TABLE_NAME + " WHERE "
            + RecipeContract.IngredientEntry.COLUMN_RECIPE_ID + " = :recipeId")
    Cursor selectAll(long recipeId);

    @Query("SELECT * FROM " + RecipeContract.IngredientEntry.TABLE_NAME + " WHERE "
            + RecipeContract.IngredientEntry.COLUMN_ID + " = :ingredientId" + " AND "
            + RecipeContract.IngredientEntry.COLUMN_RECIPE_ID + " = :recipeId")
    Cursor selectById(long ingredientId, long recipeId);

    @Query("DELETE FROM " + RecipeContract.IngredientEntry.TABLE_NAME + " WHERE "
            + RecipeContract.IngredientEntry.COLUMN_ID + " = :id")
    int deleteById(long id);

    @Update(onConflict = REPLACE)
    int update(Ingredient ingredient);

    @Query("SELECT * FROM " + RecipeContract.IngredientEntry.TABLE_NAME + " WHERE "
            + RecipeContract.IngredientEntry.COLUMN_RECIPE_ID + " = :recipeId")
    LiveData<List<Ingredient>> getByRecipeId(long recipeId);
}
