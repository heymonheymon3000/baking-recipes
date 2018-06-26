package baking.nanodegree.android.baking.ui.recipeDetails;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import baking.nanodegree.android.baking.persistence.db.AppDatabase;

public class RetrieveByRecipeIdViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mAppDatabase;
    private final long mRecipeId;

    public RetrieveByRecipeIdViewModelFactory(AppDatabase appDatabase, long recipeId) {
        this.mAppDatabase = appDatabase;
        this.mRecipeId = recipeId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new RetrieveByRecipeIdViewModel(mAppDatabase, mRecipeId);
    }
}
