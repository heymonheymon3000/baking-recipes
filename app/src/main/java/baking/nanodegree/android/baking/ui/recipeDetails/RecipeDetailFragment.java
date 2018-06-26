package baking.nanodegree.android.baking.ui.recipeDetails;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import baking.nanodegree.android.baking.R;
import baking.nanodegree.android.baking.persistence.db.AppDatabase;
import baking.nanodegree.android.baking.persistence.entity.Ingredient;
import baking.nanodegree.android.baking.persistence.entity.Step;
import baking.nanodegree.android.baking.ui.recipeVideo.RecipeVideoActivity;
import baking.nanodegree.android.baking.ui.recipeVideo.RecipeVideoFragment;
import baking.nanodegree.android.baking.ui.recipeVideo.VideoNavigatorListener;
import moe.feng.common.stepperview.IStepperAdapter;
import moe.feng.common.stepperview.VerticalStepperItemView;
import moe.feng.common.stepperview.VerticalStepperView;

import static baking.nanodegree.android.baking.ui.recipe.RecipeActivity.RECIPE_NAME;
import static baking.nanodegree.android.baking.ui.recipe.RecipeActivity.RECIPE_ID;


public class RecipeDetailFragment
        extends Fragment implements IStepperAdapter, VideoNavigatorListener {

    private List<String> expandableListTitle;
    private HashMap<String, List<Ingredient>> expandableListDetail;
    private final static String INGREDIENTS = "Ingredients";
    private VerticalStepperView mVerticalStepperView;
    private List<Step> mSteps;
    private View inflateView;
    public final static String CURRENT_STEP_INDEX = "CURRENT_STEP_INDEX";
    private ExpandListDataChangeListener expandListDataChangeListener;

    private String recipeName;
    private Long recipeId;

    public RecipeDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ExpandableListView expandableListView = rootView.findViewById(R.id.expandableListView);

        Bundle recipeBundle = getActivity().getIntent().getExtras();
        recipeId = recipeBundle.getLong(RECIPE_ID);
        recipeName = recipeBundle.getString(RECIPE_NAME);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(recipeName);

        expandableListDetail = new HashMap<>();
        expandableListDetail.put(INGREDIENTS, new ArrayList<Ingredient>());
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());

        ExpandableListAdapter expandableListAdapter = new IngredientExpandableListAdapter(getContext(), expandableListTitle, expandableListDetail);
        expandListDataChangeListener = (ExpandListDataChangeListener)expandableListAdapter;

        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getContext(),
                        expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getContext(),
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getContext(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT
                ).show();
                return false;
            }
        });

        setupViewModel(recipeId);

        return rootView;
    }

    private void setupViewModel(long recipeId) {
        RetrieveByRecipeIdViewModelFactory factory =
                new RetrieveByRecipeIdViewModelFactory(AppDatabase.getInstance(getContext()),recipeId);
        final RetrieveByRecipeIdViewModel viewModel =
                ViewModelProviders.of(this, factory).get(RetrieveByRecipeIdViewModel.class);

        viewModel.getIngredients().observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> i) {
                viewModel.getIngredients().removeObserver(this);
                expandableListDetail.clear();
                expandableListDetail.put(INGREDIENTS, i);
                expandableListTitle = new ArrayList<>(expandableListDetail.keySet());

                expandListDataChangeListener.updateList(expandableListTitle, expandableListDetail);
            }
        });

        viewModel.getSteps().observe(this, new Observer<List<Step>>() {
            @Override
            public void onChanged(@Nullable List<Step> steps) {
                viewModel.getSteps().removeObserver(this);
                mSteps = steps;
                mVerticalStepperView.updateSteppers();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mVerticalStepperView = view.findViewById( R.id.stepper_list );
        mVerticalStepperView.setStepperAdapter(this);
        mVerticalStepperView.setAnimationEnabled(true);
        mVerticalStepperView.setAlwaysShowSummary(false);
    }

    @Override
    public @NonNull CharSequence getTitle(int index) {
        return mSteps.get(index).getShortDescription();
    }

    @Override
    public @Nullable CharSequence getSummary(int index) {
        return null;
    }

    @Override
    public int size() {
        if(null == mSteps) return 0;
        return  mSteps.size();
    }

    @Override
    public View onCreateCustomView(final int index, final Context context, VerticalStepperItemView parent) {
        inflateView = LayoutInflater.from(context).inflate(R.layout.vertical_stepper_item, parent, false);
        if(null != inflateView.findViewById(R.id.back_fab)) {
            ImageView backImageView = inflateView.findViewById(R.id.back_fab);
            backImageView.setEnabled(index > 0);
            backImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mVerticalStepperView.prevStep();
                    if(inflateView.getTag() != null && inflateView.getTag().equals("tablet-land")) {

                        RecipeVideoFragment recipeVideoFragment = new RecipeVideoFragment();
                        Bundle videoFragmentBundle = new Bundle();
                        videoFragmentBundle.putLong(RECIPE_ID, recipeId);
                        videoFragmentBundle.putInt(CURRENT_STEP_INDEX, index - 1);
                        videoFragmentBundle.putString(RECIPE_NAME,recipeName);
                        recipeVideoFragment.setArguments(videoFragmentBundle);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().add(R.id.video_container, recipeVideoFragment).commit();
                    }
                }
            });
        }

        if(null != inflateView.findViewById(R.id.next_fab)) {
            ImageView nextImageView = inflateView.findViewById(R.id.next_fab);
            nextImageView.setEnabled(index < size() - 1);
            nextImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mVerticalStepperView.nextStep();
                    if(inflateView.getTag() != null && inflateView.getTag().equals("tablet-land")) {
                        RecipeVideoFragment recipeVideoFragment = new RecipeVideoFragment();
                        Bundle videoFragmentBundle = new Bundle();
                        videoFragmentBundle.putLong(RECIPE_ID, recipeId);
                        videoFragmentBundle.putInt(CURRENT_STEP_INDEX, index + 1);
                        videoFragmentBundle.putString(RECIPE_NAME,recipeName);
                        recipeVideoFragment.setArguments(videoFragmentBundle);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().add(R.id.video_container, recipeVideoFragment).commit();
                    }
                }
            });
        }

        if(null != inflateView.findViewById(R.id.video_fab)) {
            ImageView videoImageView = inflateView.findViewById(R.id.video_fab);
            videoImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (hasVideoUrl(mSteps.get(index).getVideoURL(),
                            mSteps.get(index).getThumbnailURL())) {
                        Bundle selectedRecipeCardStepBundle = new Bundle();
                        selectedRecipeCardStepBundle.putLong(RECIPE_ID, recipeId);
                        selectedRecipeCardStepBundle.putInt(CURRENT_STEP_INDEX, index);
                        selectedRecipeCardStepBundle.putString(RECIPE_NAME, recipeName);

                        final Intent intent = new Intent(getContext(), RecipeVideoActivity.class);
                        intent.putExtras(selectedRecipeCardStepBundle);
                        startActivity(intent);
                    } else {
                        Toast.makeText(context,
                                "There is no video associated with this step",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        return inflateView;
    }

    private boolean hasVideoUrl(String videoURL, String thumbnailURL) {
        if ((null != videoURL) && (!videoURL.equals(""))) { return true; }

        if ((null != thumbnailURL) && (!thumbnailURL.equals(""))) { return true; }

        return false;
    }

    @Override
    public void prevStep() {
        mVerticalStepperView.prevStep();
    }

    @Override
    public void nextStep(){
        mVerticalStepperView.nextStep();
    }

    @Override
    public void onShow(int index) {}

    @Override
    public void onHide(int index) {}
}
