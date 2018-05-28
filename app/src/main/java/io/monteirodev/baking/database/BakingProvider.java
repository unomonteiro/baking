package io.monteirodev.baking.database;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(
        authority = BakingProvider.AUTHORITY,
        database = BakingDatabase.class)
public class BakingProvider {

    private BakingProvider() {
    }

    public static final String AUTHORITY = "io.monteirodev.baking.provider.BakingProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String RECIPES = "recipes";
        String INGREDIENTS = "ingredients";
        String STEPS = "steps";
    }

    interface Type {
        String RECIPES = "vnd.android.cursor.dir/recipes";
        String INGREDIENTS = "vnd.android.cursor.dir/ingredients";
        String STEPS = "vnd.android.cursor.dir/steps";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = BakingDatabase.Tables.RECIPES)
    public static class Recipes {

        @ContentUri(
                path = Path.RECIPES,
                type = Type.RECIPES,
                defaultSort = RecipeColumns.ID + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.RECIPES);

    }

    @TableEndpoint(table = BakingDatabase.Tables.INGREDIENTS)
    public static class Ingredients {

        @ContentUri(
                path = Path.INGREDIENTS,
                type = Type.INGREDIENTS)
        public static final Uri CONTENT_URI = buildUri(Path.INGREDIENTS);

        @InexactContentUri(
                path = Path.INGREDIENTS + "/#",
                name = "recipe_ingredients",
                type = Type.INGREDIENTS,
                whereColumn = IngredientColumns.RECIPE_ID,
                pathSegment = 1
        )
        public static Uri recipeIngredients(long id) {
            return buildUri(Path.INGREDIENTS, String.valueOf(id));
        }
    }

    @TableEndpoint(table = BakingDatabase.Tables.STEPS)
    public static class Steps {

        @ContentUri(
                path = Path.STEPS,
                type = Type.STEPS)
        public static final Uri CONTENT_URI = buildUri(Path.STEPS);

        @InexactContentUri(
                path = Path.RECIPES + "/#/" + Path.STEPS,
                name = "recipe_steps",
                type = Type.STEPS,
                whereColumn = StepColumns.RECIPE_ID,
                pathSegment = 1
        )
        public static Uri recipeSteps(long id) {
            return buildUri(Path.RECIPES, String.valueOf(id), Path.STEPS);
        }
    }


}
