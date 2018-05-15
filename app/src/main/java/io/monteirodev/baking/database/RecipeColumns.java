package io.monteirodev.baking.database;

import android.provider.BaseColumns;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface RecipeColumns extends BaseColumns {

    @DataType(TEXT) @NotNull
    String NAME = "name";

    @DataType(INTEGER)
    String INGREDIENTS = "ingredients";

    @DataType(INTEGER)
    String STEPS = "steps";

    @DataType(INTEGER)
    String SERVINGS = "servings";

    @DataType(TEXT)
    String IMAGE = "image";
}
