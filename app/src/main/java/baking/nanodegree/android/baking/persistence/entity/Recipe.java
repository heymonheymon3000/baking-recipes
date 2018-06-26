package baking.nanodegree.android.baking.persistence.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import baking.nanodegree.android.baking.persistence.db.RecipeContract;

@Entity(tableName = RecipeContract.RecipeEntry.TABLE_NAME)
public class Recipe implements Parcelable {
    @PrimaryKey
    @ColumnInfo(index = true, name = RecipeContract.RecipeEntry.COLUMN_ID)
    @SerializedName(RecipeContract.RecipeEntry.COLUMN_ID)
    @Expose
    @NonNull
    private long id;

    @ColumnInfo(name = RecipeContract.RecipeEntry.COLUMN_NAME)
    @SerializedName(RecipeContract.RecipeEntry.COLUMN_NAME)
    @Expose
    private String name;

    @Ignore
    @SerializedName(RecipeContract.RecipeEntry.ENTITY_INGREDIENTS)
    private ArrayList<Ingredient> ingredients = null;

    @Ignore
    @SerializedName(RecipeContract.RecipeEntry.ENTITY_STEPS)
    private ArrayList<Step> steps = null;

    @ColumnInfo(name = RecipeContract.RecipeEntry.COLUMN_SERVINGS)
    @SerializedName(RecipeContract.RecipeEntry.COLUMN_SERVINGS)
    @Expose
    private Integer servings;

    @ColumnInfo(name = RecipeContract.RecipeEntry.COLUMN_IMAGE)
    @SerializedName(RecipeContract.RecipeEntry.COLUMN_IMAGE)
    @Expose
    private String image;

    public Recipe() {}

    private Recipe(Parcel in) {
        id = in.readLong();
        name = in.readString();
        ingredients = in.createTypedArrayList(Ingredient.CREATOR);
        steps = in.createTypedArrayList(Step.CREATOR);
        servings = in.readInt();
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeTypedList(ingredients);
        dest.writeTypedList(steps);
        dest.writeInt(servings);
        dest.writeString(image);
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @NonNull
    public long getId() {
        return id;
    }

    public void setId(@NonNull long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static Recipe fromContentValues(ContentValues values) {
        final Recipe recipe = new Recipe();

        if (values.containsKey(RecipeContract.RecipeEntry.COLUMN_ID)) {
            recipe.id = values.getAsLong(RecipeContract.RecipeEntry.COLUMN_ID);
        }

        if (values.containsKey(RecipeContract.RecipeEntry.COLUMN_NAME)) {
            recipe.name = values.getAsString(RecipeContract.RecipeEntry.COLUMN_NAME);
        }

        if (values.containsKey(RecipeContract.RecipeEntry.ENTITY_INGREDIENTS)) {
            recipe.ingredients = new Gson().fromJson(values.getAsString(
                    RecipeContract.RecipeEntry.ENTITY_INGREDIENTS),
                    new TypeToken<List<Ingredient>>() {
                    }.getType());
        }

        if (values.containsKey(RecipeContract.RecipeEntry.ENTITY_STEPS)) {
            recipe.steps = new Gson().fromJson(values.getAsString(
                    RecipeContract.RecipeEntry.ENTITY_STEPS),
                    new TypeToken<List<Step>>() {
                    }.getType());
        }

        if (values.containsKey(RecipeContract.RecipeEntry.COLUMN_SERVINGS)) {
            recipe.servings = values.getAsInteger(RecipeContract.RecipeEntry.COLUMN_SERVINGS);
        }

        if (values.containsKey(RecipeContract.RecipeEntry.COLUMN_IMAGE)) {
            recipe.image = values.getAsString(RecipeContract.RecipeEntry.COLUMN_IMAGE);
        }

        return recipe;
    }
}
