package hohserg.cursed.snow.compat.snowrealmagic;

import gloomyfolken.hooklib.api.*;
import hohserg.cursed.snow.ModifyModelHooks;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.BlockStateContainer;

@HookContainer
public class ModifyModelHooks2 {

    @Hook
    @OnReturn
    public static BlockStateContainer createBlockState(BlockSnow blockSnow, @ReturnValue BlockStateContainer result) {
        return ModifyModelHooks.createBlockState(blockSnow, result);
    }
}
