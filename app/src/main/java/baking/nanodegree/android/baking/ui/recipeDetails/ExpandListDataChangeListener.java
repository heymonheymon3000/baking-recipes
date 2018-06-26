package baking.nanodegree.android.baking.ui.recipeDetails;

import java.util.HashMap;
import java.util.List;

import baking.nanodegree.android.baking.persistence.entity.Ingredient;

public interface ExpandListDataChangeListener {
    void updateList(List<String> expandableListTitle,
                    HashMap<String, List<Ingredient>> expandableListDetail);
}
