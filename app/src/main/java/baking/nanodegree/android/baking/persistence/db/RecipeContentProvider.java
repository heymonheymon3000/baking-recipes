package baking.nanodegree.android.baking.persistence.db;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;

import baking.nanodegree.android.baking.persistence.dao.IngredientDao;
import baking.nanodegree.android.baking.persistence.dao.RecipeDao;
import baking.nanodegree.android.baking.persistence.dao.StepDao;
import baking.nanodegree.android.baking.persistence.entity.Ingredient;
import baking.nanodegree.android.baking.persistence.entity.Recipe;
import baking.nanodegree.android.baking.persistence.entity.Step;

public class RecipeContentProvider extends ContentProvider {
    public static final int RECIPE_DIRECTORY = 100;
    public static final int RECIPE_ITEM = 101;

    public static final int INGREDIENT_DIRECTORY = 200;
    public static final int INGREDIENT_ITEM = 201;

    public static final int STEP_DIRECTORY = 300;
    public static final int STEP_ITEM = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private Context mContext;

    private RecipeDao mRecipeDao;
    private IngredientDao mIngredientDao;
    private StepDao mStepDao;

    private static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(RecipeContract.AUTHORITY,
                RecipeContract.RecipeEntry.TABLE_NAME, RECIPE_DIRECTORY);
        uriMatcher.addURI(RecipeContract.AUTHORITY,
                RecipeContract.RecipeEntry.TABLE_NAME + "/#", RECIPE_ITEM);

        uriMatcher.addURI(RecipeContract.AUTHORITY,
                RecipeContract.IngredientEntry.TABLE_NAME, INGREDIENT_DIRECTORY);
        uriMatcher.addURI(RecipeContract.AUTHORITY,
                RecipeContract.IngredientEntry.TABLE_NAME + "/#", INGREDIENT_ITEM);

        uriMatcher.addURI(RecipeContract.AUTHORITY,
                RecipeContract.StepEntry.TABLE_NAME, STEP_DIRECTORY);
        uriMatcher.addURI(RecipeContract.AUTHORITY,
                RecipeContract.StepEntry.TABLE_NAME + "/#", STEP_ITEM);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        this.mContext = getContext();

        this.mRecipeDao = AppDatabase.getInstance(mContext).recipeDao();
        this.mIngredientDao = AppDatabase.getInstance(mContext).ingredientDao();
        this.mStepDao = AppDatabase.getInstance(mContext).stepDao();

        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case RECIPE_DIRECTORY:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        RecipeContract.AUTHORITY + "." +
                        RecipeContract.RecipeEntry.TABLE_NAME;
            case RECIPE_ITEM:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                        RecipeContract.AUTHORITY + "." +
                        RecipeContract.RecipeEntry.TABLE_NAME;
            case INGREDIENT_DIRECTORY:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        RecipeContract.AUTHORITY + "." +
                        RecipeContract.IngredientEntry.TABLE_NAME;
            case INGREDIENT_ITEM:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                        RecipeContract.AUTHORITY + "." +
                        RecipeContract.IngredientEntry.TABLE_NAME;
            case STEP_DIRECTORY:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        RecipeContract.AUTHORITY + "." +
                        RecipeContract.StepEntry.TABLE_NAME;
            case STEP_ITEM:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                        RecipeContract.AUTHORITY + "." +
                        RecipeContract.StepEntry.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final int code = sUriMatcher.match(uri);
        Cursor cursor;
        switch (code) {
            case RECIPE_DIRECTORY: {
                cursor = mRecipeDao.selectAll();
                cursor.setNotificationUri(mContext.getContentResolver(), uri);
                return cursor;
            }
            case RECIPE_ITEM: {
                cursor = mRecipeDao.selectById(ContentUris.parseId(uri));
                cursor.setNotificationUri(mContext.getContentResolver(), uri);
                return cursor;
            }
            case INGREDIENT_DIRECTORY:
                throw new IllegalArgumentException("URI Not Implemented Yet: " + uri);
            case INGREDIENT_ITEM:
                cursor = mIngredientDao.selectAll(ContentUris.parseId(uri));
                cursor.setNotificationUri(mContext.getContentResolver(), uri);
                return cursor;
            case STEP_DIRECTORY:
                throw new IllegalArgumentException("URI Not Implemented Yet: " + uri);
            case STEP_ITEM:
                cursor = mStepDao.selectAll(ContentUris.parseId(uri));
                cursor.setNotificationUri(mContext.getContentResolver(), uri);
                return cursor;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case RECIPE_DIRECTORY:
                final Recipe recipe = Recipe.fromContentValues(values);
                final long recipeId = mRecipeDao.insert(recipe);
                if (recipeId > 0) {
                    // Insert ingredients
                    bulkInsertIngredients(recipe, recipeId);

                    // Insert steps
                    bulkInsertSteps(recipe, recipeId);
                }
                mContext.getContentResolver().notifyChange(uri, null);
                if (recipeId > 0) {
                    return ContentUris.withAppendedId(uri, recipeId);
                } else {
                    throw new SQLiteException("Failed to insert row into URI: " + uri);
                }
            case RECIPE_ITEM:
                throw new IllegalArgumentException("Invalid URI, cannot insert with ID: " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    private void bulkInsertSteps(Recipe recipe, long recipeId) {
        final Step[] steps = new Step[recipe.getSteps().size()];
        for (int i = 0; i < recipe.getSteps().size(); i++) {
            /**
             *  Sets the foreign key in step to the primary key in recipe.
             **/
            recipe.getSteps().get(i).setRecipeId(recipeId);

            /**
             * https://developer.android.com/reference/android/arch/persistence/room/PrimaryKey
             *
             * Must set id to 0 because the id may have been altered during the JSON deserialization
             * process. Setting id to 0 allows the id to be
             * assigned by the database when inserting an step.
             **/
            recipe.getSteps().get(i).setId(0);
            steps[i] = recipe.getSteps().get(i);
        }

        mStepDao.insertAll(steps);
    }

    private void bulkInsertIngredients(Recipe recipe, long recipeId) {
        final Ingredient[] ingredients = new Ingredient[recipe.getIngredients().size()];
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            /**
             *  Sets the foreign key in ingredient to the primary key in recipe.
             **/
            recipe.getIngredients().get(i).setRecipeId(recipeId);

            /**
             * https://developer.android.com/reference/android/arch/persistence/room/PrimaryKey
             *
             * Must set id to 0 because the id may have been altered during the JSON deserialization
             * process. Setting id to 0 allows the id to be
             * assigned by the database when inserting an ingredient.
             **/
            recipe.getIngredients().get(i).setId(0);
            ingredients[i] = recipe.getIngredients().get(i);
        }

        mIngredientDao.insertAll(ingredients);
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] valuesArray) {
        switch (sUriMatcher.match(uri)) {
            case RECIPE_DIRECTORY:
                if (mContext == null) {
                    return 0;
                }
                final Recipe[] recipes = new Recipe[valuesArray.length];
                for (int i = 0; i < valuesArray.length; i++) {
                    recipes[i] = Recipe.fromContentValues(valuesArray[i]);
                }
                long recipeIds[] = mRecipeDao.insertAll(recipes);
                for (int i = 0; i < recipeIds.length; i++) {
                    // Insert ingredients
                    bulkInsertIngredients(recipes[i], recipeIds[i]);

                    // Insert steps
                    bulkInsertSteps(recipes[i], recipeIds[i]);
                }
            case RECIPE_ITEM:
                throw new IllegalArgumentException("Invalid URI, cannot insert with ID: " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int count;
        switch (sUriMatcher.match(uri)) {
            case RECIPE_DIRECTORY:
                throw new IllegalArgumentException("Invalid URI, cannot update without ID" + uri);
            case RECIPE_ITEM:
                count = mRecipeDao.deleteById(ContentUris.parseId(uri));
                mContext.getContentResolver().notifyChange(uri, null);
                return count;
            case INGREDIENT_ITEM:
                count = mIngredientDao.deleteById(ContentUris.parseId(uri));
                mContext.getContentResolver().notifyChange(uri, null);
                return count;
            case STEP_ITEM:
                count = mStepDao.deleteById(ContentUris.parseId(uri));
                mContext.getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case RECIPE_DIRECTORY:
                throw new IllegalArgumentException("Invalid URI, cannot update without ID" + uri);
            case RECIPE_ITEM:
                final Recipe recipe = Recipe.fromContentValues(values);
                final int count = mRecipeDao.update(recipe);
                mContext.getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(
            @NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final Context context = getContext();
        if (context == null) {
            return new ContentProviderResult[0];
        }
        final AppDatabase database = AppDatabase.getInstance(context);
        database.beginTransaction();
        try {
            final ContentProviderResult[] result = super.applyBatch(operations);
            database.setTransactionSuccessful();
            return result;
        } finally {
            database.endTransaction();
        }
    }
}
