package baking.nanodegree.android.baking.rest;

public class ApiUtils {
    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";

    public static RecipeService getRecipeService() {
        return RetrofitClient.getClient(BASE_URL).create(RecipeService.class);
    }
}
