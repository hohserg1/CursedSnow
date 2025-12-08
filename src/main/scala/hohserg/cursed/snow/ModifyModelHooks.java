package hohserg.cursed.snow;

import com.google.common.collect.ImmutableList;
import gloomyfolken.hooklib.api.*;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

@HookContainer
public class ModifyModelHooks {
    
    @Hook
    @OnReturn
    public static BlockStateContainer createBlockState(BlockSnow blockSnow, @ReturnValue BlockStateContainer result) {
        if (result instanceof ExtendedBlockState extended)
            return new ExtendedBlockState(
                blockSnow,
                extended.getProperties().toArray(new IProperty[0]),
                ImmutableList.<IUnlistedProperty>builder().addAll(extended.getUnlistedProperties()).add(ModifyModel.blockBaseProperty()).build().toArray(new IUnlistedProperty[0])
            );
        else
            return new ExtendedBlockState(blockSnow, result.getProperties().toArray(new IProperty[0]), new IUnlistedProperty[]{ModifyModel.blockBaseProperty()});
    }
}
