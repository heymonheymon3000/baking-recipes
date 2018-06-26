package baking.nanodegree.android.baking.ui.recipe;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentUris;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import baking.nanodegree.android.baking.R;

import baking.nanodegree.android.baking.persistence.entity.Recipe;
import baking.nanodegree.android.baking.utilities.DisplayMetricUtils;
import baking.nanodegree.android.baking.utilities.SimpleIdlingResource;

public class RecipeFragment extends Fragment {
    private RecipeAdapter mRecipeAdapter;
    private RecyclerView mRecyclerView;
    private Context context;

    public RecipeFragment() {}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();

        setupModelView();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_master_list_recipe, container, false);
        mRecyclerView = rootView.findViewById(R.id.rv_recipe_card);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }

    private void setupRecyclerView() {
        int marginInDp = 8;
        int marginInPixel = DisplayMetricUtils.convertDpToPixel(8);
        int deviceWidthInDp = DisplayMetricUtils.convertPixelsToDp(
                DisplayMetricUtils.getDeviceWidth(getActivity()));

        int column = deviceWidthInDp / 300;
        int totalMarginInDp = marginInDp * (column + 1);
        int cardWidthInDp = (deviceWidthInDp - totalMarginInDp) / column;
        int cardHeightInDp = (int) (2.0f / 3.0f * cardWidthInDp);

        RecyclerViewMarginDecoration decoration =
                new RecyclerViewMarginDecoration(RecyclerViewMarginDecoration.ORIENTATION_VERTICAL,
                        marginInPixel, column);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), column, LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(decoration);
        mRecipeAdapter =
                new RecipeAdapter(getContext(), cardWidthInDp, cardHeightInDp, (RecipeActivity)getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mRecipeAdapter);
    }

    private void setupModelView() {
        RecipeViewModelFactory recipeViewModelFactory = new RecipeViewModelFactory(
                getActivity().getApplication());

        RecipeViewModel recipeViewModel = ViewModelProviders.of(this,
                recipeViewModelFactory).get(RecipeViewModel.class);

        final SimpleIdlingResource idlingResource = (SimpleIdlingResource)((RecipeActivity)getActivity()).getIdlingResource();

        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        LiveData<List<Recipe>> recipes = recipeViewModel.getAllRecipes();

        recipes.observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                mRecipeAdapter.setRecipes(recipes);

                if (idlingResource != null) {
                    idlingResource.setIdleState(true);
                }
            }
        });
    }
}
