package baking.nanodegree.android.baking.ui.widget;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import baking.nanodegree.android.baking.R;
import baking.nanodegree.android.baking.persistence.entity.Recipe;

public class SimpleRecipeAdapter extends
        RecyclerView.Adapter<SimpleRecipeAdapter.SimpleRecipeViewHolder> {

    private ArrayList<Recipe> recipes = new ArrayList<>();
    private SimpleRecipeClickListener clickListener;

    public SimpleRecipeAdapter(ArrayList<Recipe> recipes, SimpleRecipeClickListener clickListener) {
        this.recipes = recipes;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public SimpleRecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_simple, parent, false);

        return new SimpleRecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SimpleRecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.tvRecipeName.setText(recipe.getName());
    }

    @Override
    public int getItemCount() {
        if(recipes == null) return 0;
        return recipes.size();
    }

    interface SimpleRecipeClickListener {
        void onRecipeItemClick(Recipe review);
    }

    class SimpleRecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvRecipeName;

        SimpleRecipeViewHolder(View itemView) {
            super(itemView);
            tvRecipeName = itemView.findViewById(R.id.tv_recipe_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onRecipeItemClick(recipes.get(getAdapterPosition()));
        }
    }
}
