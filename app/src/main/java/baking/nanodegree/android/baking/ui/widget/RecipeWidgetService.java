package baking.nanodegree.android.baking.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import baking.nanodegree.android.baking.R;
import baking.nanodegree.android.baking.persistence.db.RecipeContract;
import baking.nanodegree.android.baking.persistence.entity.Ingredient;

public class RecipeWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RecipeRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class RecipeRemoteViewsFactory
        implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private ArrayList<Ingredient> ingredients = new ArrayList<>();
    private long recipeId;

    RecipeRemoteViewsFactory(Context applicationContext,
                             Intent intent) {
        this.mContext = applicationContext;
        recipeId = Long.valueOf(intent.getData().getSchemeSpecificPart());
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        ingredients = getIngredients(recipeId);
    }

    private ArrayList<Ingredient> getIngredients(long recipeId) {
        Uri baseIngredientUri = RecipeContract
                .IngredientEntry
                .INGREDIENT_CONTENT_ITEM_URI.build();

        String ingredientUriString = baseIngredientUri.toString() + "/" + recipeId;
        Uri ingredientUri = Uri.parse(ingredientUriString);

        Cursor ingredientCursor = mContext.getContentResolver()
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

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if(ingredients == null) return 0;
        return ingredients.size();
    }

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

    @Override
    public RemoteViews getViewAt(int position) {
        if (ingredients.size() == 0) {
            return null;
        }

        Ingredient ingredient = ingredients.get(position);
        String ingredientName = ingredient.getIngredient();
        Double quantity = ingredient.getQuantity();
        String measure = ingredient.getMeasure();

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.item_widget_ingredient);
        views.setTextViewText(R.id.tv_ingredient, ingredientName);
        views.setTextViewText(R.id.tv_quantity, quantity + " " + measure);

        Bundle extras = new Bundle();
        extras.putLong("recipeId", recipeId);
        extras.putLong("ingredientId", ingredients.get(position).getId());
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.ll_ingredient, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

