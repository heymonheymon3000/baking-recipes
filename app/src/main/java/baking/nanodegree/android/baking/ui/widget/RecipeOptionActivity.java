package baking.nanodegree.android.baking.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RemoteViews;

import baking.nanodegree.android.baking.R;
import baking.nanodegree.android.baking.persistence.entity.Recipe;
import baking.nanodegree.android.baking.ui.recipe.RecipeActivity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class RecipeOptionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecipeOptionDialog recipeOptionDialog = new RecipeOptionDialog();
        recipeOptionDialog.setDialogListener(new RecipeOptionDialog.RecipeChoiceDialogListener() {
            @Override
            public void onDismiss(Recipe recipeCard) {

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(RecipeOptionActivity.this);
                RemoteViews remoteViews = new RemoteViews(RecipeOptionActivity.this.getPackageName(), R.layout.baking_widget_provider);
                ComponentName recipeWidget = new ComponentName(RecipeOptionActivity.this, BakingWidgetProvider.class);

                final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(recipeWidget);
                for (int appWidgetId : appWidgetIds) {
                    if (appWidgetId == getIntent().getIntExtra("widgetId", 0)) {
                        remoteViews.setTextViewText(R.id.tv_recipe_name, recipeCard.getName());
                        remoteViews.setViewVisibility(R.id.ll_no_recipe, GONE);
                        remoteViews.setViewVisibility(R.id.ll_ingredient, VISIBLE);

                        Intent remoteAdapterIntent =
                                new Intent(RecipeOptionActivity.this, RecipeWidgetService.class);
                        remoteAdapterIntent.setData(Uri.fromParts("recipeId",
                                String.valueOf(recipeCard.getId()), null));
                        remoteViews.setRemoteAdapter(R.id.gv_ingredient, remoteAdapterIntent);

                        Intent openAppIntent = new Intent(RecipeOptionActivity.this,
                                RecipeActivity.class);
                        openAppIntent.putExtra("recipeId", recipeCard.getId());

                        PendingIntent appPendingIntent = PendingIntent.getActivity(
                                RecipeOptionActivity.this, (int)recipeCard.getId(), openAppIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        remoteViews.setPendingIntentTemplate(R.id.gv_ingredient, appPendingIntent);
                        remoteViews.setEmptyView(R.id.gv_ingredient, R.id.ll_no_recipe);

                        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

                        break;
                    }
                }

                RecipeOptionActivity.this.finish();
            }
        });
        recipeOptionDialog.show(getSupportFragmentManager(), "recipeChoiceDialog");
    }
}


