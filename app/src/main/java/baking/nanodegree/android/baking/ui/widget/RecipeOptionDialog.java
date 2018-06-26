package baking.nanodegree.android.baking.ui.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import baking.nanodegree.android.baking.R;
import baking.nanodegree.android.baking.persistence.entity.Recipe;

public class RecipeOptionDialog extends
        DialogFragment implements IOption {
    private SimpleRecipeAdapter recipeAdapter;
    private RecipeChoiceDialogListener dialogListener;
    private Context context;

    public void setDialogListener(RecipeChoiceDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_recipe_choice, container);
        RecyclerView rvRecipes = view.findViewById(R.id.rv_recipes);

        RecipeOptionDialogPresenter presenter = new RecipeOptionDialogPresenter(this, this);
        presenter.loadRecipes();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        DividerItemDecoration itemDecor = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        rvRecipes.addItemDecoration(itemDecor);
        rvRecipes.setLayoutManager(layoutManager);
        recipeAdapter = new SimpleRecipeAdapter(presenter.getLoadedRecipes(), new SimpleRecipeAdapter.SimpleRecipeClickListener() {
            @Override
            public void onRecipeItemClick(Recipe recipe) {
                dialogListener.onDismiss(recipe);
            }
        });
        rvRecipes.setAdapter(recipeAdapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void updateRecipeList() {
        recipeAdapter.notifyDataSetChanged();
    }

    public interface RecipeChoiceDialogListener {
        void onDismiss(Recipe recipe);
    }
}
