package baking.nanodegree.android.baking.persistence.source.remote;

import baking.nanodegree.android.baking.persistence.entity.Recipe;

public interface RemoteSource {
    void getRecipes(RemoteResponseListener<Recipe> responseListener);
}
