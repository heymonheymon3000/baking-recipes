package baking.nanodegree.android.baking.ui.recipeDetails;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import baking.nanodegree.android.baking.R;
import baking.nanodegree.android.baking.persistence.db.AppDatabase;
import baking.nanodegree.android.baking.persistence.entity.Ingredient;
import baking.nanodegree.android.baking.persistence.entity.Step;
import baking.nanodegree.android.baking.ui.recipeVideo.RecipeVideoActivity;
import baking.nanodegree.android.baking.ui.recipeVideo.RecipeVideoFragment;
import baking.nanodegree.android.baking.ui.recipeVideo.VideoNavigatorListener;
import baking.nanodegree.android.baking.utilities.Constants;
import baking.nanodegree.android.baking.utilities.Events;
import baking.nanodegree.android.baking.utilities.GlobalBus;
import moe.feng.common.stepperview.IStepperAdapter;
import moe.feng.common.stepperview.VerticalStepperItemView;
import moe.feng.common.stepperview.VerticalStepperView;

public class RecipeDetailFragment
        extends Fragment implements IStepperAdapter,
        VideoNavigatorListener {

    private final static String INGREDIENTS = "Ingredients";
    private List<String> expandableListTitle;
    private HashMap<String, List<Ingredient>> expandableListDetail;
    private VerticalStepperView mVerticalStepperView;
    private List<Step> mSteps;
    private View inflateView;
    private ExpandListDataChangeListener expandListDataChangeListener;
    private String recipeName;
    private Long recipeId;
    private int currentStepIndex = 0;

    public RecipeDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        GlobalBus.getBus().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        GlobalBus.getBus().register(this);

        View rootView =
                inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ExpandableListView expandableListView = rootView.findViewById(R.id.expandableListView);

        Bundle recipeBundle = Objects.requireNonNull(getActivity()).getIntent().getExtras();
        recipeId = Objects.requireNonNull(recipeBundle).getLong(Constants.RECIPE_ID);
        recipeName = recipeBundle.getString(Constants.RECIPE_NAME);
        Objects.requireNonNull(((AppCompatActivity) getActivity())
                .getSupportActionBar()).setTitle(recipeName);

        expandableListDetail = new HashMap<>();
        expandableListDetail.put(INGREDIENTS, new ArrayList<Ingredient>());
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());

        ExpandableListAdapter expandableListAdapter =
                new IngredientExpandableListAdapter(getContext(),
                        expandableListTitle, expandableListDetail);
        expandListDataChangeListener = (ExpandListDataChangeListener)expandableListAdapter;

        expandableListView.setAdapter(expandableListAdapter);

        setupViewModel(recipeId);

        return rootView;
    }

    private void setupViewModel(long recipeId) {
        RetrieveByRecipeIdViewModelFactory factory =
                new RetrieveByRecipeIdViewModelFactory(AppDatabase
                        .getInstance(getContext()),recipeId);

        final RetrieveByRecipeIdViewModel viewModel =
                ViewModelProviders.of(this, factory)
                        .get(RetrieveByRecipeIdViewModel.class);

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
    public View onCreateCustomView(final int index, final Context context,
                                   VerticalStepperItemView parent) {
        inflateView = LayoutInflater.from(context)
                .inflate(R.layout.vertical_stepper_item, parent, false);
        if(null != inflateView.findViewById(R.id.back_fab)) {
            ImageView backImageView = inflateView.findViewById(R.id.back_fab);
            backImageView.setEnabled(index > 0);
            backImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    prevStep();
                    currentStepIndex = index - 1;
                    Events.FragmentActivityMessage message =
                            new Events.FragmentActivityMessage(currentStepIndex);
                    GlobalBus.getBus().post(message);

                    if(inflateView.getTag() != null &&
                            inflateView.getTag().equals(Constants.TABLET_LAND)) {
                        RecipeVideoFragment recipeVideoFragment = new RecipeVideoFragment();
                        Bundle videoFragmentBundle = new Bundle();
                        videoFragmentBundle.putLong(Constants.RECIPE_ID, recipeId);
                        videoFragmentBundle.putInt(Constants.CURRENT_STEP_INDEX, currentStepIndex);
                        videoFragmentBundle.putString(Constants.RECIPE_NAME,recipeName);
                        videoFragmentBundle.putParcelableArrayList(
                                Constants.STEPS, (ArrayList<? extends Parcelable>) mSteps);
                        recipeVideoFragment.setArguments(videoFragmentBundle);
                        FragmentManager fragmentManager = Objects.requireNonNull(getActivity())
                                .getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.video_container,
                                recipeVideoFragment).commit();
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
                    nextStep();
                    currentStepIndex = index + 1;
                    Events.FragmentActivityMessage message =
                            new Events.FragmentActivityMessage(currentStepIndex);
                    GlobalBus.getBus().post(message);

                    if(inflateView.getTag() != null &&
                            inflateView.getTag().equals(Constants.TABLET_LAND)) {
                        RecipeVideoFragment recipeVideoFragment = new RecipeVideoFragment();
                        Bundle videoFragmentBundle = new Bundle();
                        videoFragmentBundle.putLong(Constants.RECIPE_ID, recipeId);
                        videoFragmentBundle.putInt(Constants.CURRENT_STEP_INDEX, currentStepIndex);
                        videoFragmentBundle.putString(Constants.RECIPE_NAME,recipeName);
                        videoFragmentBundle.putParcelableArrayList(
                                Constants.STEPS, (ArrayList<? extends Parcelable>) mSteps);
                        recipeVideoFragment.setArguments(videoFragmentBundle);
                        FragmentManager fragmentManager = Objects.requireNonNull(getActivity())
                                .getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.video_container,
                                recipeVideoFragment).commit();
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
                        selectedRecipeCardStepBundle.putParcelableArrayList(Constants.STEPS,
                                (ArrayList<? extends Parcelable>) mSteps);
                        selectedRecipeCardStepBundle.putLong(Constants.RECIPE_ID, recipeId);
                        selectedRecipeCardStepBundle.putInt(Constants.CURRENT_STEP_INDEX, index);
                        selectedRecipeCardStepBundle.putString(Constants.RECIPE_NAME, recipeName);
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
        return (null != videoURL) && (!videoURL.equals("")) ||
                (null != thumbnailURL) && (!thumbnailURL.equals(""));
    }

    @Override
    public void prevStep() {
        mVerticalStepperView.prevStep();
    }

    @Override
    public void nextStep(){
        mVerticalStepperView.nextStep();
    }

    @Subscribe
    public void onMessageEvent(Events.ActivityFragmentMessage event) {
        mVerticalStepperView.setCurrentStep(event.getcurrentStepIndex());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.CURRENT_STEP_INDEX, currentStepIndex);
    }

    @Override
    public void onShow(int index) {}

    @Override
    public void onHide(int index) {}
}
