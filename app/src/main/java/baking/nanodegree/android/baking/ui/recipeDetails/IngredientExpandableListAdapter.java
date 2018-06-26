package baking.nanodegree.android.baking.ui.recipeDetails;

import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import baking.nanodegree.android.baking.R;
import baking.nanodegree.android.baking.persistence.entity.Ingredient;

public class IngredientExpandableListAdapter
        extends BaseExpandableListAdapter implements ExpandListDataChangeListener {
    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<Ingredient>> expandableListDetail;

    public IngredientExpandableListAdapter(Context context, List<String> expandableListTitle,
                                           HashMap<String, List<Ingredient>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Ingredient expandedListText = (Ingredient) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        TextView expandedListQuantityTextView = convertView
                .findViewById(R.id.expandedListItemQuantity);
        expandedListQuantityTextView.setText(Html.fromHtml(formatText("Quantity", String.valueOf(expandedListText.getQuantity()))));

        TextView expandedListMeasureTextView = convertView
                .findViewById(R.id.expandedListItemMeasure);
        expandedListMeasureTextView.setText(Html.fromHtml(formatText("Measure", expandedListText.getMeasure())));

        TextView expandedListIngredientTextView = convertView
                .findViewById(R.id.expandedListItemIngredient);
        expandedListIngredientTextView.setText(Html.fromHtml(formatText("Ingredient", expandedListText.getIngredient())));

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = convertView
                .findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    private String formatText(String item, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("\u2022 <b>");
        sb.append(item);
        sb.append("</b>: ");
        sb.append(value);
        return sb.toString();
    }


    @Override
    public void updateList(List<String> expandableListTitle,
                           HashMap<String, List<Ingredient>> expandableListDetail) {
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        notifyDataSetChanged();
    }
}
