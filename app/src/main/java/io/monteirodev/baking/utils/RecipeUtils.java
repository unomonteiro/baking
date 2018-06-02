package io.monteirodev.baking.utils;

import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.monteirodev.baking.R;
import io.monteirodev.baking.database.IngredientColumns;
import io.monteirodev.baking.database.RecipeColumns;
import io.monteirodev.baking.database.StepColumns;
import io.monteirodev.baking.models.Ingredient;
import io.monteirodev.baking.models.Recipe;
import io.monteirodev.baking.models.Step;

public class RecipeUtils {
    @NonNull
    public static ContentValues getRecipeValues(Recipe recipe) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(RecipeColumns.ID, recipe.getId());
        recipeValues.put(RecipeColumns.NAME, recipe.getName());
        recipeValues.put(RecipeColumns.INGREDIENTS, recipe.getIngredients().size());
        recipeValues.put(RecipeColumns.STEPS, recipe.getSteps().size());
        recipeValues.put(RecipeColumns.SERVINGS, recipe.getServings());
        recipeValues.put(RecipeColumns.IMAGE, recipe.getImage());
        return recipeValues;
    }

    @NonNull
    public static ContentValues getIngredientValues(Integer recipeId, Ingredient ingredient) {
        ContentValues ingredientValues = new ContentValues();
        ingredientValues.put(IngredientColumns.RECIPE_ID, recipeId);
        ingredientValues.put(IngredientColumns.QUANTITY, ingredient.getQuantity());
        ingredientValues.put(IngredientColumns.MEASURE, ingredient.getMeasure());
        ingredientValues.put(IngredientColumns.INGREDIENT, ingredient.getIngredient());
        return ingredientValues;
    }

    /**
     * Html.fromHtml deprecated in Android N
     * https://stackoverflow.com/a/37905107/6997703
     */
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    @NonNull
    public static List<String> getIngredientsString(@NonNull Context context, List<Ingredient> ingredients, String prefix) {
        List<String> ingredientsString = new ArrayList<>();
        for (Ingredient ingredientObj : ingredients) {
            DecimalFormat df = new DecimalFormat("0.##");
            String quantity = df.format((float) ingredientObj.getQuantity());
            String measure = ingredientObj.getMeasure();
            String ingredient = ingredientObj.getIngredient();
            ingredientsString.add(context.getString(
                    R.string.quantity_measure_ingredient, prefix, quantity, measure, ingredient));
        }
        return ingredientsString;
    }


    @NonNull
    public static ContentValues getStepValues(Integer recipeId, Step step) {
        ContentValues stepValues = new ContentValues();
        stepValues.put(StepColumns.RECIPE_ID, recipeId);
        stepValues.put(StepColumns.STEP, step.getId());
        stepValues.put(StepColumns.SHORT_DESCRIPTION, step.getShortDescription());
        stepValues.put(StepColumns.DESCRIPTION, step.getDescription());
        stepValues.put(StepColumns.VIDEO_URL, step.getVideoURL());
        stepValues.put(StepColumns.THUMBNAIL_URL, step.getThumbnailURL());
        return stepValues;
    }
}
