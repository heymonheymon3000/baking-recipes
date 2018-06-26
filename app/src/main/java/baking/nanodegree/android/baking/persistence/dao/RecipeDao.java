package baking.nanodegree.android.baking.persistence.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import java.util.List;

import baking.nanodegree.android.baking.persistence.db.RecipeContract;
import baking.nanodegree.android.baking.persistence.entity.Recipe;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface RecipeDao {
    @Query("SELECT COUNT(*) FROM " + RecipeContract.RecipeEntry.TABLE_NAME)
    int count();

    @Insert(onConflict = REPLACE)
    long insert(Recipe recipe);

    @Insert(onConflict = REPLACE)
    long[] insertAll(Recipe[] recipes);

    @Query("SELECT * FROM " + RecipeContract.RecipeEntry.TABLE_NAME + " ORDER BY " + RecipeContract.RecipeEntry.COLUMN_NAME)
    Cursor selectAll();

    @Query("SELECT * FROM " + RecipeContract.RecipeEntry.TABLE_NAME + " ORDER BY " + RecipeContract.RecipeEntry.COLUMN_NAME)
    LiveData<List<Recipe>> getAll();

    @Query("SELECT " + RecipeContract.RecipeEntry.TABLE_NAME + ".*, " +
            RecipeContract.IngredientEntry.TABLE_NAME + ".*, " +
            RecipeContract.StepEntry.TABLE_NAME + ".* " +
            "FROM " + RecipeContract.RecipeEntry.TABLE_NAME + " " +
            "INNER JOIN " + RecipeContract.IngredientEntry.TABLE_NAME + " ON " +
            RecipeContract.IngredientEntry.TABLE_NAME + "." +
            RecipeContract.IngredientEntry.COLUMN_RECIPE_ID + " = " +
            RecipeContract.RecipeEntry.TABLE_NAME + "." + RecipeContract.RecipeEntry.COLUMN_ID +
            " INNER JOIN " + RecipeContract.StepEntry.TABLE_NAME + " ON " +
            RecipeContract.StepEntry.TABLE_NAME + "." +
            RecipeContract.StepEntry.COLUMN_RECIPE_ID + " = " +
            RecipeContract.RecipeEntry.TABLE_NAME + "." + RecipeContract.RecipeEntry.COLUMN_ID)
    Cursor selectAllWithChildElements();

    @Query("SELECT * FROM " + RecipeContract.RecipeEntry.TABLE_NAME + " WHERE "
            + RecipeContract.RecipeEntry.COLUMN_ID + " = :id")
    Cursor selectById(long id);

    @Query("SELECT " + RecipeContract.RecipeEntry.TABLE_NAME + ".*, " +
            RecipeContract.IngredientEntry.TABLE_NAME + ".*, " +
            RecipeContract.StepEntry.TABLE_NAME + ".* " +
            "FROM " + RecipeContract.RecipeEntry.TABLE_NAME + " " +
            "INNER JOIN " + RecipeContract.IngredientEntry.TABLE_NAME + " ON " +
            RecipeContract.IngredientEntry.TABLE_NAME + "." +
            RecipeContract.IngredientEntry.COLUMN_RECIPE_ID + " = " +
            RecipeContract.RecipeEntry.TABLE_NAME + "." + RecipeContract.RecipeEntry.COLUMN_ID +
            " INNER JOIN " + RecipeContract.StepEntry.TABLE_NAME + " ON " +
            RecipeContract.StepEntry.TABLE_NAME + "." +
            RecipeContract.StepEntry.COLUMN_RECIPE_ID + " = " +
            RecipeContract.RecipeEntry.TABLE_NAME + "." + RecipeContract.RecipeEntry.COLUMN_ID +
            " WHERE " + RecipeContract.RecipeEntry.TABLE_NAME + "." +
            RecipeContract.RecipeEntry.COLUMN_ID + " = :id")
    Cursor selectByIdWithChildElements(long id);

    @Query("DELETE FROM " + RecipeContract.RecipeEntry.TABLE_NAME + " WHERE "
            + RecipeContract.RecipeEntry.COLUMN_ID + " = :id")
    int deleteById(long id);

    @Update(onConflict = REPLACE)
    int update(Recipe recipe);
}
