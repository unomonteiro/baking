package io.monteirodev.baking.sync;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import io.monteirodev.baking.api.BakingClient;
import io.monteirodev.baking.api.BakingInterface;
import io.monteirodev.baking.database.BakingProvider;
import io.monteirodev.baking.models.Ingredient;
import io.monteirodev.baking.models.Recipe;
import io.monteirodev.baking.models.Step;
import timber.log.Timber;

import static io.monteirodev.baking.utils.RecipeUtils.getIngredientValues;
import static io.monteirodev.baking.utils.RecipeUtils.getRecipeValues;
import static io.monteirodev.baking.utils.RecipeUtils.getStepValues;

public class SyncTask {

    private static final String TAG = SyncTask.class.getSimpleName();

    synchronized public static void syncRecipes(Context context) {
        try {
            BakingInterface bakingInterface = BakingClient.getClient().create(BakingInterface.class);
            List<Recipe> recipes = bakingInterface.getRecipes().execute().body();

            if (recipes != null && recipes.size() > 0) {
                ArrayList<ContentProviderOperation> operations = new ArrayList<>();

                operations.add(delete(BakingProvider.Recipes.CONTENT_URI));
                operations.add(delete(BakingProvider.Ingredients.CONTENT_URI));
                operations.add(delete(BakingProvider.Steps.CONTENT_URI));

                for (Recipe recipe : recipes) {
                    operations.add(insert(BakingProvider.Recipes.CONTENT_URI,
                            getRecipeValues(recipe)));

                    for (Ingredient ingredient : recipe.getIngredients()) {
                        operations.add(insert(BakingProvider.Ingredients.CONTENT_URI,
                                getIngredientValues(recipe.getId(), ingredient)));
                    }

                    for (Step step : recipe.getSteps()) {
                        operations.add(insert(BakingProvider.Steps.CONTENT_URI,
                                getStepValues(recipe.getId(), step)));
                    }
                }
                context.getContentResolver().applyBatch(BakingProvider.AUTHORITY, operations);
            }
        } catch (Exception e) {
            Timber.e( "syncRecipes: " + e.getMessage(), e);
        }
    }

    private static ContentProviderOperation delete(Uri contentUri) {
        return ContentProviderOperation.newDelete(contentUri)
                .withSelection(null, null)
                .build();
    }

    private static ContentProviderOperation insert(Uri contentUri, ContentValues stepValues) {
        return ContentProviderOperation.newInsert(
                contentUri)
                .withValues(stepValues)
                .build();
    }

}
