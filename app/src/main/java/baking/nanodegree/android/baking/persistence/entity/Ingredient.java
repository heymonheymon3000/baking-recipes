package baking.nanodegree.android.baking.persistence.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import baking.nanodegree.android.baking.persistence.db.RecipeContract;

@Entity(tableName = RecipeContract.IngredientEntry.TABLE_NAME,
    foreignKeys = @ForeignKey(entity = Recipe.class,
    parentColumns = RecipeContract.RecipeEntry.COLUMN_ID,
    childColumns = RecipeContract.IngredientEntry.COLUMN_RECIPE_ID))
public class Ingredient implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = RecipeContract.IngredientEntry.COLUMN_ID)
    private long id;

    @ColumnInfo(index = true, name = RecipeContract.IngredientEntry.COLUMN_RECIPE_ID)
    private long recipeId;

    @ColumnInfo(name = RecipeContract.IngredientEntry.COLUMN_QUANTITY)
    @SerializedName(RecipeContract.IngredientEntry.COLUMN_QUANTITY)
    @Expose
    private double quantity;

    @ColumnInfo(name = RecipeContract.IngredientEntry.COLUMN_MEASURE)
    @SerializedName(RecipeContract.IngredientEntry.COLUMN_MEASURE)
    @Expose
    private String measure;

    @ColumnInfo(name = RecipeContract.IngredientEntry.COLUMN_INGREDIENT)
    @SerializedName(RecipeContract.IngredientEntry.COLUMN_INGREDIENT)
    @Expose
    private String ingredient;

    public Ingredient() {}

    private Ingredient(Parcel in) {
        id = in.readLong();
        recipeId = in.readLong();
        quantity = in.readDouble();
        measure = in.readString();
        ingredient = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(recipeId);
        dest.writeDouble(quantity);
        dest.writeString(measure);
        dest.writeString(ingredient);
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }
}
