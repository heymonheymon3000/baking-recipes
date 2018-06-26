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

@Entity(tableName = RecipeContract.StepEntry.TABLE_NAME,
        foreignKeys = @ForeignKey(entity = Recipe.class,
                parentColumns = RecipeContract.RecipeEntry.COLUMN_ID,
                childColumns = RecipeContract.StepEntry.COLUMN_RECIPE_ID))
public class Step implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = RecipeContract.StepEntry.COLUMN_ID)
    private long id;

    @ColumnInfo(index = true, name = RecipeContract.StepEntry.COLUMN_RECIPE_ID)
    private long recipeId;

    @ColumnInfo(name = RecipeContract.StepEntry.COLUMN_SHORT_DESCRIPTION)
    @SerializedName(RecipeContract.StepEntry.COLUMN_SHORT_DESCRIPTION)
    @Expose
    private String shortDescription;

    @ColumnInfo(name = RecipeContract.StepEntry.COLUMN_DESCRIPTION)
    @SerializedName(RecipeContract.StepEntry.COLUMN_DESCRIPTION)
    @Expose
    private String description;

    @ColumnInfo(name = RecipeContract.StepEntry.COLUMN_VIDEO_URL)
    @SerializedName(RecipeContract.StepEntry.COLUMN_VIDEO_URL)
    @Expose
    private String videoURL;

    @ColumnInfo(name = RecipeContract.StepEntry.COLUMN_THUMBNAIL_URL)
    @SerializedName(RecipeContract.StepEntry.COLUMN_THUMBNAIL_URL)
    @Expose
    private String thumbnailURL;

    public Step() {}

    private Step(Parcel in) {
        id = in.readLong();
        recipeId = in.readLong();
        shortDescription = in.readString();
        description = in.readString();
        videoURL = in.readString();
        thumbnailURL = in.readString();
    }

    public static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(recipeId);
        dest.writeString(shortDescription);
        dest.writeString(description);
        dest.writeString(videoURL);
        dest.writeString(thumbnailURL);
    }

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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {this.shortDescription = shortDescription; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }
}