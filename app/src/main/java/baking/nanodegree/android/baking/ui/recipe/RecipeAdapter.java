package baking.nanodegree.android.baking.ui.recipe;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;
import baking.nanodegree.android.baking.R;
import baking.nanodegree.android.baking.persistence.entity.Recipe;
import baking.nanodegree.android.baking.utilities.DisplayMetricUtils;

public class RecipeAdapter
        extends RecyclerView.Adapter<RecipeAdapter.MasterListRecipeAdapterViewHolder> {

    private final String TAG = RecipeAdapter.class.getSimpleName();

    private List<Recipe> mRecipes;
    private Picasso picassoInstance;
    private int cardWidth;
    private int cardHeight;

    private final OnRecipeCardClickListener mRecipeCardClickListener;

    public interface OnRecipeCardClickListener {
        void onRecipeCardSelected(Recipe selectedItem);
    }

    public RecipeAdapter(Context context,
                         int cardWidth, int cardHeight,
                         OnRecipeCardClickListener recipeCardClickListener) {
        this.mRecipeCardClickListener = recipeCardClickListener;
        picassoInstance =
                new Picasso.Builder(context.getApplicationContext())
                        .addRequestHandler(new VideoRequestHandler())
                        .build();
        this.cardWidth = cardWidth;
        this.cardHeight = cardHeight;
    }

    @NonNull
    @Override
    public MasterListRecipeAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,
                                                                int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.recipe_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        view.setFocusable(true);

        return new MasterListRecipeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MasterListRecipeAdapterViewHolder holder,
                                 int position) {
        final Recipe recipe = mRecipes.get(position);
        setCardViewSize(holder);
        picassoInstance
                .load(VideoRequestHandler.SCHEME_VIDEO+"://"+recipe.getImage())
                .placeholder(R.drawable.recipes)
                .resize(DisplayMetricUtils.convertDpToPixel(cardWidth),
                        DisplayMetricUtils.convertDpToPixel(cardHeight))
                .centerCrop()
                .into(holder.mRecipeCardImageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        holder.mRecipeCardTitleTextView.setText(recipe.getName());
                        holder.mServingImageView.setImageResource(R.drawable.ic_person_black_24dp);
                        holder.mRecipeCardServingsTextView.setText(String.valueOf(recipe.getServings()));
                    }

                    @Override
                    public void onError() {
                        Log.e(TAG, "Error occurred retrieving image.");
                    }
                });
    }

    private void setCardViewSize(MasterListRecipeAdapterViewHolder holder) {
        ViewGroup.LayoutParams layoutParams =
                holder.cardView.getLayoutParams();
        layoutParams.width = DisplayMetricUtils.convertDpToPixel(cardWidth);
        layoutParams.height = DisplayMetricUtils.convertDpToPixel(cardHeight);
        holder.cardView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        if (null == mRecipes) return 0;
        return mRecipes.size();
    }

    public void setRecipes(List<Recipe> recipes) {
        this.mRecipes = recipes;
        notifyDataSetChanged();
    }

    public class MasterListRecipeAdapterViewHolder extends
            RecyclerView.ViewHolder implements View.OnClickListener  {
        final ImageView mRecipeCardImageView;
        final CardView cardView;
        final TextView mRecipeCardTitleTextView;
        final ImageView mServingImageView;
        final TextView mRecipeCardServingsTextView;

        private MasterListRecipeAdapterViewHolder(View view){
            super(view);
            mRecipeCardImageView = view.findViewById(R.id.recipe_card_image_view);
            cardView = view.findViewById(R.id.cv_recipe);
            mRecipeCardTitleTextView = view.findViewById(R.id.tv_title);
            mServingImageView = view.findViewById(R.id.iv_serving);
            mRecipeCardServingsTextView = view.findViewById(R.id.tv_serving);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int selectedPosition = getAdapterPosition();
            mRecipeCardClickListener.onRecipeCardSelected(mRecipes.get(selectedPosition));
        }
    }
}
