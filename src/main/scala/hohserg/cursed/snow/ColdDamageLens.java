package hohserg.cursed.snow;

import gloomyfolken.hooklib.api.*;
import net.minecraft.entity.player.*;

@HookContainer
public class ColdDamageLens {
    @FieldLens(createField = true)
    public static FieldAccessor<EntityPlayerMP, Integer> inSnowTime = FieldAccessor.defaultValue(0);

    @FieldLens(createField = true)
    public static FieldAccessor<EntityPlayerMP, Boolean> freezingPlayed = FieldAccessor.defaultValue(false);
}
