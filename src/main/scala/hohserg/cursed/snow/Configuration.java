package hohserg.cursed.snow;

import net.minecraftforge.common.config.*;
import net.minecraftforge.common.config.Config.*;

@Config(modid = "cursed_snow")
public class Configuration {
    @Comment("list of block's registry names on which snow cant be placed")
    public static String[] snowBaseBlacklist = {};

    public static boolean blacklistAllSlabs = false;

    @Comment({
        "list of model locations for custom snow-like blocks",
        "example: \"minecraft:snow_layer#layers=9\""
    })
    public static String[] extraSnowLikeBlocksModelLocations = {};

    @Comment("list of dimension id's where no hardcode snowfall and chill damage")
    public static int[] hardcoreDimensionsBlacklist = {};
}
