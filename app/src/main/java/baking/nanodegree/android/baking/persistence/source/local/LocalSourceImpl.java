package baking.nanodegree.android.baking.persistence.source.local;

import android.arch.lifecycle.LiveData;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import baking.nanodegree.android.baking.persistence.db.AppDatabase;
import baking.nanodegree.android.baking.persistence.db.RecipeContract;
import baking.nanodegree.android.baking.persistence.entity.Ingredient;
import baking.nanodegree.android.baking.persistence.entity.Recipe;
import baking.nanodegree.android.baking.persistence.entity.Step;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

import static android.R.attr.id;

public class LocalSourceImpl implements LocalSource {

    private Context context;
    public LocalSourceImpl(Context context) {
        this.context = context;
    }

    @Override
    public void getAll(LocalObserver<ArrayList<Recipe>> dataObserver) {
        Observable.create(new ObservableOnSubscribe<ArrayList<Recipe>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ArrayList<Recipe>> e) throws Exception {
                Cursor recipeCursor = context.getContentResolver()
                        .query(RecipeContract.RecipeEntry.RECIPE_CONTENT_URI,
                                null,
                                null,
                                null,
                                RecipeContract.RecipeEntry.COLUMN_NAME + " DESC");

                ArrayList<Recipe> recipes = new ArrayList<>();
                if (recipeCursor != null) {
                    while (recipeCursor.moveToNext()) {
                        Recipe recipe = getRecipeFromCursor(recipeCursor);
                        recipes.add(recipe);
                    }
                    recipeCursor.close();

                    for (int i = 0; i < recipes.size(); i++) {
                        recipes.get(i).setIngredients(getIngredients(recipes, i));
                        recipes.get(i).setSteps(getSteps(recipes, i));
                    }
                    e.onNext(recipes);
                } else {
                    e.onError(new NullPointerException("Failed to query all data"));
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataObserver);
    }

    @android.support.annotation.NonNull
    private ArrayList<Ingredient> getIngredients(ArrayList<Recipe> recipes, int recipePosition) {
        Uri baseIngredientUri = RecipeContract
                .IngredientEntry
                .INGREDIENT_CONTENT_ITEM_URI.build();

        String ingredientUriString = baseIngredientUri.toString() + "/" +
                recipes.get(recipePosition).getId();
        Uri ingredientUri = Uri.parse(ingredientUriString);

        Cursor ingredientCursor = context.getContentResolver()
                .query(ingredientUri,
                        null,
                        null,
                        null,
                        RecipeContract.IngredientEntry.COLUMN_ID + " ASC");

        ArrayList<Ingredient> ingredients = new ArrayList<>();
        if (ingredientCursor != null) {
            while (ingredientCursor.moveToNext()) {
                Ingredient ingredient = getIngredientFromCursor(ingredientCursor);
                ingredients.add(ingredient);
            }
            ingredientCursor.close();
        }
        return ingredients;
    }

    @android.support.annotation.NonNull
    private ArrayList<Step> getSteps(ArrayList<Recipe> recipes, int recipePosition) {
        Uri baseStepUri = RecipeContract
                .StepEntry
                .STEP_CONTENT_ITEM_URI.build();

        String stepUriString = baseStepUri.toString() + "/" + recipes.get(recipePosition).getId();
        Uri stepUri = Uri.parse(stepUriString);

        Cursor stepCursor = context.getContentResolver()
                .query(stepUri,
                        null,
                        null,
                        null,
                        RecipeContract.StepEntry.COLUMN_ID + " ASC");

        ArrayList<Step> steps = new ArrayList<>();
        if (stepCursor != null) {
            while (stepCursor.moveToNext()) {
                Step step = getStepFromCursor(stepCursor);
                steps.add(step);
            }
            stepCursor.close();
        }
        return steps;
    }

    @android.support.annotation.NonNull
    private Ingredient getIngredientFromCursor(Cursor ingredientCursor) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientCursor.getLong(ingredientCursor
                .getColumnIndex(RecipeContract.IngredientEntry.COLUMN_ID)));
        ingredient.setRecipeId(ingredientCursor.getLong(ingredientCursor
                .getColumnIndex(RecipeContract.IngredientEntry.COLUMN_RECIPE_ID)));
        ingredient.setIngredient(ingredientCursor.getString(ingredientCursor
                .getColumnIndex(RecipeContract.IngredientEntry.COLUMN_INGREDIENT)));
        ingredient.setQuantity(ingredientCursor.getDouble(ingredientCursor
                .getColumnIndex(RecipeContract.IngredientEntry.COLUMN_QUANTITY)));
        ingredient.setMeasure(ingredientCursor.getString(ingredientCursor
                .getColumnIndex(RecipeContract.IngredientEntry.COLUMN_MEASURE)));
        return ingredient;
    }

    @android.support.annotation.NonNull
    private Step getStepFromCursor(Cursor stepCursor) {
        Step step = new Step();
        step.setId(stepCursor.getLong(stepCursor
                .getColumnIndex(RecipeContract.StepEntry.COLUMN_ID)));
        step.setRecipeId(stepCursor.getLong(stepCursor
                .getColumnIndex(RecipeContract.StepEntry.COLUMN_RECIPE_ID)));
        step.setShortDescription(stepCursor.getString(stepCursor
                .getColumnIndex(RecipeContract.StepEntry.COLUMN_SHORT_DESCRIPTION)));
        step.setDescription(stepCursor.getString(stepCursor
                .getColumnIndex(RecipeContract.StepEntry.COLUMN_DESCRIPTION)));
        step.setThumbnailURL(stepCursor.getString(stepCursor
                .getColumnIndex(RecipeContract.StepEntry.COLUMN_THUMBNAIL_URL)));
        step.setVideoURL(stepCursor.getString(stepCursor
                .getColumnIndex(RecipeContract.StepEntry.COLUMN_VIDEO_URL)));
        return step;
    }

    @android.support.annotation.NonNull
    private Recipe getRecipeFromCursor(Cursor recipeCursor) {
        Recipe recipe = new Recipe();
        recipe.setId(recipeCursor.getLong(recipeCursor.getColumnIndex(
                RecipeContract.RecipeEntry.COLUMN_ID)));
        recipe.setImage(recipeCursor.getString(recipeCursor.getColumnIndex(
                RecipeContract.RecipeEntry.COLUMN_IMAGE)));
        recipe.setName(recipeCursor.getString(recipeCursor.getColumnIndex(
                RecipeContract.RecipeEntry.COLUMN_NAME)));
        recipe.setServings(recipeCursor.getInt(recipeCursor.getColumnIndex(
                RecipeContract.RecipeEntry.COLUMN_SERVINGS)));
        return recipe;
    }

    @Override
    public void getById(final long id, LocalObserver<Cursor> cursorFavoriteDataObserver) {
        Observable.create(new ObservableOnSubscribe<Cursor>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Cursor> e) throws Exception {
                String stringId = Long.toString(id);
                Uri uri = RecipeContract.RecipeEntry.RECIPE_CONTENT_URI.buildUpon()
                        .appendPath(stringId)
                        .build();
                Cursor cursor = context.getContentResolver()
                        .query(uri,
                                null,
                                RecipeContract.RecipeEntry.COLUMN_ID + " = ?",
                                new String[]{String.valueOf(id)},
                                RecipeContract.RecipeEntry.COLUMN_NAME + " DESC");

                if (cursor != null) {
                    Log.d("QueryCursor", String.valueOf(cursor.getCount()));
                    e.onNext(cursor);
                } else {
                    e.onError(new NullPointerException("Failed to query data with id: " + id));
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cursorFavoriteDataObserver);
    }

    @Override
    public void insert(final Recipe recipe, LocalObserver<Uri> uriFavoriteDataObserver) {
        Observable.create(new ObservableOnSubscribe<Uri>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Uri> e) throws Exception {
                insertSingleRecipe(e, recipe);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(uriFavoriteDataObserver);
    }

    private void insertSingleRecipe(@NonNull ObservableEmitter<Uri> e, Recipe recipe) {
        ContentValues contentValues = getContentValues(recipe);

        Uri uri = context.getContentResolver()
                .insert(RecipeContract.RecipeEntry.RECIPE_CONTENT_URI, contentValues);
        if (uri != null) {
            Log.d("InsertUri", uri.toString());
            e.onNext(uri);
        } else {
            e.onError(new NullPointerException("Failed to insert data"));
        }
    }

    @Override
    public void insertMany(final ArrayList<Recipe> recipes, LocalObserver<Uri> uriFavoriteDataObserver) {
        Observable.create(new ObservableOnSubscribe<Uri>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Uri> e) throws Exception {
                for (int i = 0; i < recipes.size(); i++) {
                    Recipe recipe = recipes.get(i);
                    insertSingleRecipe(e, recipe);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(uriFavoriteDataObserver);
    }

    @Override
    public void update(final Recipe recipe, LocalObserver<Integer> integerFavoriteDataObserver) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {

                String stringId = Long.toString(recipe.getId());
                Uri uri = RecipeContract.RecipeEntry.RECIPE_CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                ContentValues contentValues = getContentValues(recipe);

                int count = context.getContentResolver().update(uri, contentValues, null, null);

                if (count != 0) {
                    Log.d("UpdateCount", String.valueOf(count));
                    e.onNext(count);
                } else {
                    e.onError(new NullPointerException("Failed to update data with id: " + id));
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integerFavoriteDataObserver);
    }

    @android.support.annotation.NonNull
    private ContentValues getContentValues(Recipe recipe) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(RecipeContract.RecipeEntry.COLUMN_ID, recipe.getId());
        contentValues.put(RecipeContract.RecipeEntry.COLUMN_NAME, recipe.getName());
        contentValues.put(RecipeContract.RecipeEntry.COLUMN_SERVINGS, recipe.getServings());
        contentValues.put(RecipeContract.RecipeEntry.COLUMN_IMAGE, recipe.getImage());
        contentValues.put(RecipeContract.RecipeEntry.ENTITY_INGREDIENTS, new Gson().toJson(recipe.getIngredients()));
        contentValues.put(RecipeContract.RecipeEntry.ENTITY_STEPS, new Gson().toJson(recipe.getSteps()));
        return contentValues;
    }

    @Override
    public void delete(final long id, LocalObserver<Integer> integerFavoriteDataObserver) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {

                String stringId = Long.toString(id);
                Uri uri = RecipeContract.RecipeEntry.RECIPE_CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                int count = context.getContentResolver().delete(uri, null, null);

                if (count != 0) {
                    Log.d("DeleteCount", String.valueOf(count));
                    e.onNext(count);
                } else {
                    e.onError(new NullPointerException("Failed to delete data with id: " + id));
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integerFavoriteDataObserver);
    }

    @Override
    public LiveData<List<Recipe>> getAllRecipes() {
        return AppDatabase.getInstance(context).recipeDao().getAll();
    }

    @Override
    public LiveData<List<Ingredient>> getAllIngredientsByRecipeId(long id) {
        return AppDatabase.getInstance(context).ingredientDao().getByRecipeId(id);
    }

    @Override
    public LiveData<List<Step>> getAllStepsByRecipeId(long id) {
        return AppDatabase.getInstance(context).stepDao().getByRecipeId(id);
    }
}
