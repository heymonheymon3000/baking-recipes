package baking.nanodegree.android.baking.persistence.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import java.util.List;

import baking.nanodegree.android.baking.persistence.db.RecipeContract;
import baking.nanodegree.android.baking.persistence.entity.Step;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface StepDao {
    @Query("SELECT COUNT(*) FROM " + RecipeContract.StepEntry.TABLE_NAME)
    int count();

    @Insert(onConflict = REPLACE)
    long insert(Step step);

    @Insert(onConflict = REPLACE)
    long[] insertAll(Step[] steps);

    @Query("SELECT * FROM " + RecipeContract.StepEntry.TABLE_NAME + " WHERE "
            + RecipeContract.StepEntry.COLUMN_RECIPE_ID + " = :recipeId")
    Cursor selectAll(long recipeId);

    @Query("SELECT * FROM " + RecipeContract.StepEntry.TABLE_NAME + " WHERE "
            + RecipeContract.StepEntry.COLUMN_ID + " = :stepId" + " AND "
            + RecipeContract.StepEntry.COLUMN_RECIPE_ID + " = :recipeId")
    Cursor selectById(long stepId, long recipeId);

    @Query("DELETE FROM " + RecipeContract.StepEntry.TABLE_NAME + " WHERE "
            + RecipeContract.StepEntry.COLUMN_ID + " = :id")
    int deleteById(long id);

    @Update(onConflict = REPLACE)
    int update(Step step);

    @Query("SELECT * FROM " + RecipeContract.StepEntry.TABLE_NAME + " WHERE "
            + RecipeContract.StepEntry.COLUMN_RECIPE_ID + " = :recipeId")
    LiveData<List<Step>> getByRecipeId(long recipeId);
}
