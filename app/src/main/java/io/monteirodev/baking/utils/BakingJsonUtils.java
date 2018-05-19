package io.monteirodev.baking.utils;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.monteirodev.baking.database.IngredientColumns;
import io.monteirodev.baking.database.RecipeColumns;
import io.monteirodev.baking.database.StepColumns;

public class BakingJsonUtils {

    public static final int RECIPES_INDEX = 0;
    public static final int INGREDIENTS_INDEX = 1;
    public static final int STEPS_INDEX = 2;
    
    public static ArrayList<ContentValues[]> getContentValues(String json) throws JSONException {

        final String KEY_ID = "id";
        final String KEY_NAME = "name";
        final String KEY_INGREDIENTS = "ingredients";
        final String KEY_STEPS = "steps";
        final String KEY_SERVINGS = "servings";
        final String KEY_IMAGE = "image";

        final String KEY_QUANTITY = "quantity";
        final String KEY_MEASURE = "measure";
        final String KEY_INGREDIENT = "ingredient";

        final String KEY_SHORT_DESCRIPTION = "shortDescription";
        final String KEY_DESCRIPTION = "description";
        final String KEY_VIDEO_URL = "videoURL";
        final String KEY_THUMBNAIL_URL = "thumbnailURL";

        JSONArray jsonResultArray = new JSONArray(json);

        ArrayList<ContentValues[]> bakingList = new ArrayList<>();
        ArrayList<ContentValues> recipesList = new ArrayList<>();
        ArrayList<ContentValues> ingredientsList = new ArrayList<>();
        ArrayList<ContentValues> stepsList = new ArrayList<>();

        for (int i = 0; i < jsonResultArray.length(); i++) {
            JSONObject recipeJson = jsonResultArray.getJSONObject(i);

            ContentValues recipe = new ContentValues();
            recipe.put(RecipeColumns.ID, recipeJson.getInt(KEY_ID));
            recipe.put(RecipeColumns.NAME, recipeJson.getInt(KEY_NAME));

            // INGREDIENTS
            JSONArray ingredientsJsonArray = recipeJson.getJSONArray(KEY_INGREDIENTS);
            for (int j = 0; j < ingredientsJsonArray.length(); j++) {
                JSONObject ingredientJson = ingredientsJsonArray.getJSONObject(j);
                ContentValues ingredient = new ContentValues();
                ingredient.put(IngredientColumns.RECIPE_ID, recipeJson.getInt(KEY_ID));
                ingredient.put(IngredientColumns.QUANTITY, ingredientJson.getDouble(KEY_QUANTITY));
                ingredient.put(IngredientColumns.MEASURE, ingredientJson.getString(KEY_MEASURE));
                ingredient.put(IngredientColumns.INGREDIENT, ingredientJson.getString(KEY_INGREDIENT));

                ingredientsList.add(ingredient);
            }
            recipe.put(RecipeColumns.INGREDIENTS, ingredientsJsonArray.length());

            // STEPS
            JSONArray stepsJsonArray = recipeJson.getJSONArray(KEY_STEPS);
            for (int j = 0; j < stepsJsonArray.length(); j++) {
                JSONObject stepJson = stepsJsonArray.getJSONObject(j);

                ContentValues step = new ContentValues();
                step.put(StepColumns.RECIPE_ID, recipeJson.getInt(KEY_ID));
                step.put(StepColumns.STEP, stepJson.getInt(KEY_ID));
                step.put(StepColumns.SHORT_DESCRIPTION, stepJson.getString(KEY_SHORT_DESCRIPTION));
                step.put(StepColumns.DESCRIPTION, stepJson.getString(KEY_DESCRIPTION));
                step.put(StepColumns.VIDEO_URL, stepJson.getString(KEY_VIDEO_URL));
                step.put(StepColumns.THUMBNAIL_URL, stepJson.getString(KEY_THUMBNAIL_URL));
                stepsList.add(step);
            }
            recipe.put(RecipeColumns.STEPS, stepsJsonArray.length());

            recipe.put(RecipeColumns.SERVINGS, recipeJson.getInt(KEY_SERVINGS));
            recipe.put(RecipeColumns.IMAGE, recipeJson.getString(KEY_IMAGE));

            recipesList.add(recipe);
        }

        bakingList.add(RECIPES_INDEX, recipesList.toArray(new ContentValues[recipesList.size()]));
        bakingList.add(INGREDIENTS_INDEX, ingredientsList.toArray(new ContentValues[ingredientsList.size()]));
        bakingList.add(STEPS_INDEX, stepsList.toArray(new ContentValues[stepsList.size()]));

        return bakingList;
    }
}




















