package io.monteirodev.baking.database;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(
        authority = BakingProvider.AUTHORITY,
        database = BakingDatabase.class)
public class BakingProvider {

    public static final String AUTHORITY = "io.monteirodev.baking.provider.BakingProvider";

    @TableEndpoint(table = BakingDatabase.BAKING_RECIPES)
    public static class BakingRecipes {
        @ContentUri(
                path = "recipes",
                type = "vnd.android.cursor.dir/recipes",
                defaultSort = BakingContract.COLUMN_NAME + " DESC")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/recipes");
    }
}
