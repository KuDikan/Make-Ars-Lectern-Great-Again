package icu.kudikan.make_ars_lectern_great_again.utils;

public class Constants {
    public static final int VANILLA_ROW_COUNT = 3;
    public static final int MIN_ROW_COUNT = 3;
    public static final int VANILLA_COLUMN_COUNT = 9;
    public static final int SLOT_SIZE = 18;
    public static final int ROW_HEIGHT = SLOT_SIZE;
    public static final int EXPEND_EXTRA_ROWS = 4; // Rows gained when the crafting grid is collapsed
    public static final int SCREEN_MARGIN = 8;     // Vertical space kept free around the GUI
    public static final int LEFT_BUTTONS_WIDTH = 18; // Sort / tab buttons sticking out on the left of the GUI

    // Runtime layout. Shared with the (common) menu mixin, so it must not depend on client classes.
    // Stays at the vanilla values on a dedicated server; the client screen updates it on every init().
    private static volatile int rowCount = VANILLA_ROW_COUNT;
    private static volatile int columnCount = VANILLA_COLUMN_COUNT;

    public static int rows() {
        return rowCount;
    }

    public static int expandedRows() {
        return rowCount + EXPEND_EXTRA_ROWS;
    }

    public static int extraRows() {
        return rowCount - VANILLA_ROW_COUNT;
    }

    public static int columns() {
        return columnCount;
    }

    public static int extraColumns() {
        return columnCount - VANILLA_COLUMN_COUNT;
    }

    // The storage "book" is widened on both sides: offset of its left edge relative to the vanilla GUI
    public static int leftShift() {
        return extraColumns() * SLOT_SIZE / 2;
    }

    // ... and offset of its right edge
    public static int rightShift() {
        return extraColumns() * SLOT_SIZE - leftShift();
    }

    public static void setRows(int rows) {
        rowCount = Math.max(MIN_ROW_COUNT, rows);
    }

    public static void setColumns(int columns) {
        columnCount = Math.max(VANILLA_COLUMN_COUNT, columns);
    }

    /**
     * @param screenHeight GUI-scaled screen height
     * @param imageHeight  height of the vanilla (3 rows) GUI
     * @param fixedRows    configured row count, 0 = adaptive
     * @param maxRows      cap for the adaptive mode, 0 = unlimited
     */
    public static int computeRows(int screenHeight, int imageHeight, int fixedRows, int maxRows) {
        if (fixedRows > 0) return Math.max(MIN_ROW_COUNT, fixedRows);
        int free = screenHeight - imageHeight - SCREEN_MARGIN * 2;
        int rows = VANILLA_ROW_COUNT + Math.floorDiv(free, ROW_HEIGHT);
        if (maxRows > 0) rows = Math.min(rows, maxRows);
        return Math.max(MIN_ROW_COUNT, rows);
    }

    /**
     * The GUI stays centered, so each extra column takes half a slot on each side.
     *
     * @param screenWidth  GUI-scaled screen width
     * @param imageWidth   width of the vanilla GUI
     * @param fixedColumns configured column count, 0 = adaptive
     * @param maxColumns   cap for the adaptive mode, 0 = unlimited
     * @param sideMargin   space kept free on each side of the GUI (for JEI)
     */
    public static int computeColumns(int screenWidth, int imageWidth, int fixedColumns, int maxColumns, int sideMargin) {
        if (fixedColumns > 0) return Math.max(VANILLA_COLUMN_COUNT, fixedColumns);
        int perSide = (screenWidth - imageWidth) / 2 - Math.max(sideMargin, LEFT_BUTTONS_WIDTH);
        int columns = VANILLA_COLUMN_COUNT + Math.max(0, perSide * 2 / SLOT_SIZE);
        if (maxColumns > 0) columns = Math.min(columns, maxColumns);
        return Math.max(VANILLA_COLUMN_COUNT, columns);
    }
}
