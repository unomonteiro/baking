package io.monteirodev.baking.database;

import android.provider.BaseColumns;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.REAL;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface IngredientColumns extends BaseColumns {

    @DataType(INTEGER) @References(table = BakingDatabase.Tables.RECIPES, column = RecipeColumns._ID)
    String RECIPE_ID = "recipeId";

    @DataType(REAL)
    String QUANTITY = "quantity";

    @DataType(TEXT)
    String MEASURE = "measure";

    @DataType(TEXT)
    String INGREDIENT = "ingredient";

}
