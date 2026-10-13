package icu.kudikan.make_ars_lectern_great_again;

import icu.kudikan.make_ars_lectern_great_again.utils.ConfigBuilder;
import net.neoforged.neoforge.common.ModConfigSpec;


public class Config {
    public static final Config INSTANCE;
    public static final ModConfigSpec SPEC;

    static {
        var builder = new ConfigBuilder();
        INSTANCE = new Config(builder);
        SPEC = builder.build();
    }

    public ModConfigSpec.BooleanValue disableSearchAutoFocus;
    public ModConfigSpec.IntValue rowCount;
    public ModConfigSpec.IntValue maxRows;
    public ModConfigSpec.IntValue columnCount;
    public ModConfigSpec.IntValue maxColumns;
    public ModConfigSpec.IntValue sideMargin;

    private Config(ConfigBuilder builder) {
        disableSearchAutoFocus = builder.start("disableSearchAutoFocus",
                        "Disable Search Filed Auto Focus",
                        "Whether to disable search filed auto focus.")
                .define("disableSearchAutoFocus", true);
        rowCount = builder.start("rowCount",
                        "Storage Row Count",
                        "Number of storage rows. 0 = adapt to the window height.")
                .defineInRange("rowCount", 0, 0, 100);
        maxRows = builder.start("maxRows",
                        "Max Storage Rows",
                        "Upper limit for the adaptive row count. 0 = unlimited.")
                .defineInRange("maxRows", 0, 0, 100);
        columnCount = builder.start("columnCount",
                        "Storage Column Count",
                        "Number of storage columns. 0 = adapt to the window width.")
                .defineInRange("columnCount", 0, 0, 100);
        maxColumns = builder.start("maxColumns",
                        "Max Storage Columns",
                        "Upper limit for the adaptive column count. 0 = unlimited.")
                .defineInRange("maxColumns", 15, 0, 100);
        sideMargin = builder.start("sideMargin",
                        "Side Margin",
                        "Space (in GUI pixels) kept free on each side of the lectern for JEI when adapting the column count.")
                .defineInRange("sideMargin", 120, 0, 1000);
    }
}
