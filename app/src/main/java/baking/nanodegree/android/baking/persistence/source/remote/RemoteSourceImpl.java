package baking.nanodegree.android.baking.persistence.source.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.ArrayList;

import baking.nanodegree.android.baking.persistence.entity.Recipe;
import baking.nanodegree.android.baking.rest.ApiUtils;
import baking.nanodegree.android.baking.rest.RecipeService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RemoteSourceImpl implements RemoteSource {
    private RecipeService recipeService;
    private Context mContext;

    public RemoteSourceImpl(Context context) {
        this.mContext = context;
        this.recipeService = ApiUtils.getRecipeService();
    }

    @Override
    public void getRecipes(final RemoteResponseListener<Recipe> responseListener) {
        recipeService.getRecipes().enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Recipe>> call, @NonNull Response<ArrayList<Recipe>> response) {
                if(response.isSuccessful()) {
                    responseListener.onSuccess(response.body());
                } else {
                    responseListener.onFailure(new Throwable("Failed to get recipes with a http status of " + String.valueOf(response.code())));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Recipe>> call, @NonNull Throwable t) {
                Toast.makeText(mContext,
                        "Something went wrong, please check your internet connection and try again!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}


