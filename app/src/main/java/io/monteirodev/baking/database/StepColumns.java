package io.monteirodev.baking.database;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface StepColumns {

    @DataType(INTEGER) @PrimaryKey @AutoIncrement @NotNull
    String ID = "_id";

    @DataType(INTEGER)
    @References(table = BakingDatabase.Tables.RECIPES, column = RecipeColumns.ID)
    String RECIPE_ID = "recipeId";

    @DataType(INTEGER)
    String STEP = "step";

    @DataType(TEXT)
    String SHORT_DESCRIPTION = "shortDescription";

    @DataType(TEXT)
    String DESCRIPTION = "description";

    @DataType(TEXT)
    String VIDEO_URL = "videoURL";

    @DataType(TEXT)
    String THUMBNAIL_URL = "thumbnailURL";
}
