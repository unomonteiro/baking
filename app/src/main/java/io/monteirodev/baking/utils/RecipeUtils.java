package io.monteirodev.baking.utils;

import android.content.ContentValues;
import android.support.annotation.NonNull;

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
