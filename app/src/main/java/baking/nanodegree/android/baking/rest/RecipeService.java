package baking.nanodegree.android.baking.rest;

import java.util.ArrayList;

import baking.nanodegree.android.baking.persistence.entity.Recipe;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipeService {
    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<ArrayList<Recipe>> getRecipes();
}
