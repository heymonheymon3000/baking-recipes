package baking.nanodegree.android.baking.ui.recipe;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import baking.nanodegree.android.baking.persistence.entity.Recipe;
import baking.nanodegree.android.baking.persistence.source.local.LocalObserver;
import baking.nanodegree.android.baking.persistence.source.local.LocalSource;
import baking.nanodegree.android.baking.persistence.source.local.LocalSourceImpl;
import baking.nanodegree.android.baking.persistence.source.remote.RemoteResponseListener;
import baking.nanodegree.android.baking.persistence.source.remote.RemoteSource;
import baking.nanodegree.android.baking.persistence.source.remote.RemoteSourceImpl;

public class RecipeViewModel extends AndroidViewModel {
    private LiveData<List<Recipe>> recipes = null;

    private final RemoteSource remoteSource;
    private final LocalSource localSource;

    public RecipeViewModel(Application application) {
        super(application);

        this.remoteSource = new RemoteSourceImpl(application.getApplicationContext());
        this.localSource = new LocalSourceImpl(application.getApplicationContext());
        loadRecipes();
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        recipes = localSource.getAllRecipes();
        return recipes;
    }

    private void loadRecipes() {
        if (recipes == null) {
            localSource.getAll(new LocalObserver<ArrayList<Recipe>>() {
                @Override
                public void onNext(@io.reactivex.annotations.NonNull ArrayList<Recipe> cachedRecipes) {
                    if (cachedRecipes == null || cachedRecipes.size() != 4) {
                        getRecipeFromRemote();
                    }
                }

                @Override
                public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                    super.onError(e);
                    getRecipeFromRemote();
                }
            });
        }
    }

    private void getRecipeFromRemote() {
        remoteSource.getRecipes(new RemoteResponseListener<Recipe>() {
            @Override
            public void onSuccess(ArrayList<Recipe> result) {
                setRecipeImage(result);
                setRecipeVideo(result);
                makeRecipeCache(result);
            }

            @Override
            public void onFailure(Throwable throwable) {}
        });
    }

    private void makeRecipeCache(ArrayList<Recipe> recipes) {
        localSource.insertMany(recipes, new LocalObserver<Uri>() {});
    }

    private void setRecipeImage(ArrayList<Recipe> result) {
        for (int i = 0; i < result.size(); i++) {
            Recipe recipe = result.get(i);
            if (recipe.getImage() == null || recipe.getImage().isEmpty()) {
                int stepCount = recipe.getSteps().size();
                for (int j = stepCount - 1; j >= 0; j--) {
                    if (recipe.getSteps().get(j).getThumbnailURL() != null
                            && !recipe.getSteps().get(j).getThumbnailURL().equals("")) {
                        recipe.setImage(recipe.getSteps().get(j).getThumbnailURL());
                        break;
                    }
                    else if (recipe.getSteps().get(j).getVideoURL() != null
                            && !recipe.getSteps().get(j).getVideoURL().equals("")) {
                        recipe.setImage(recipe.getSteps().get(j).getVideoURL());
                        break;
                    }
                }
            }
        }
    }

    private void setRecipeVideo(ArrayList<Recipe> result) {
        for (int i = 0; i < result.size(); i++) {
            Recipe recipe = result.get(i);
            int stepCount = recipe.getSteps().size();
            for (int j = 0; j < stepCount; j++) {
                if (recipe.getSteps().get(j).getVideoURL().equals("") &&
                        !(recipe.getSteps().get(j).getThumbnailURL().isEmpty())) {
                    recipe.getSteps().get(j).setVideoURL(recipe.getSteps().get(j).getThumbnailURL());
                }
            }
        }
    }
}
