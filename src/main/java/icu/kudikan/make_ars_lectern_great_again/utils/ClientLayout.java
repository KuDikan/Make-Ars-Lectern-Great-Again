package icu.kudikan.make_ars_lectern_great_again.utils;

import icu.kudikan.make_ars_lectern_great_again.Config;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;

// Client-only layout helpers (never load from the common menu mixin).
public class ClientLayout {
    public static int desiredRows(int screenHeight, int imageHeight) {
        return Constants.computeRows(screenHeight, imageHeight,
                Config.INSTANCE.rowCount.getAsInt(), Config.INSTANCE.maxRows.getAsInt());
    }

    // The recipe book sits on the left of the GUI: no widening while it is open.
    public static int desiredColumns(Screen screen, int imageWidth) {
        if (screen instanceof RecipeUpdateListener listener && listener.getRecipeBookComponent().isVisible()) {
            return Constants.VANILLA_COLUMN_COUNT;
        }
        return Constants.computeColumns(screen.width, imageWidth,
                Config.INSTANCE.columnCount.getAsInt(), Config.INSTANCE.maxColumns.getAsInt(),
                Config.INSTANCE.sideMargin.getAsInt());
    }
}
