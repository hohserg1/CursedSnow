package hohserg.cursed.snow;

import gloomyfolken.hooklib.api.*;
import net.minecraft.block.*;
import net.minecraft.block.state.*;
import net.minecraft.init.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;

@HookContainer
public class SnowfallHarder {

    @Hook
    @OnMethodCall("isAir")
    public static ReturnSolve<Boolean> canSnowAtBody(World world, BlockPos pos, boolean checkLight,
                                                     @LocalVariable(id = 5) IBlockState iblockstate1) {
        if (package$.MODULE$.isSnow(iblockstate1) && iblockstate1.getValue(BlockSnow.LAYERS) < 8 && package$.MODULE$.isHardcoreSnowDimension(world))
            return ReturnSolve.yes(true);
        else
            return ReturnSolve.no();
    }

    boolean patternSetSnow(WorldServer self, BlockPos blockpos1) {
        return self.setBlockState(blockpos1, Blocks.SNOW_LAYER.getDefaultState());
    }

    @Hook(targetMethod = "updateBlocks")
    @OnExpression(expressionPattern = "patternSetSnow", shift = Shift.INSTEAD)
    public static boolean increaseSnowLayerInsteadOnSet(WorldServer self, @LocalVariable(id = 9) BlockPos blockpos1) {
        IBlockState current = self.getBlockState(blockpos1);
        int currentLayers = package$.MODULE$.isSnow(current) ? current.getValue(BlockSnow.LAYERS) : 0;
        return self.setBlockState(blockpos1, Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS, currentLayers + 1));
    }

    @FieldLens
    public static FieldAccessor<Chunk, int[]> precipitationHeightMap;

    @Hook(targetMethod = "getPrecipitationHeight")
    @OnReturn
    public static BlockPos snowingOverSnow(Chunk chunk, BlockPos pos,
                                           @LocalVariable(id = 4) int k,
                                           @ReturnValue BlockPos result) {
        if (package$.MODULE$.isHardcoreSnowDimension(chunk.getWorld())) {
            IBlockState top = chunk.getBlockState(result);
            if (isFullSnow(top)) {
                while (isFullSnow(top)) {
                    result = result.up();
                    top = chunk.getBlockState(result);
                }
                precipitationHeightMap.get(chunk)[k] = result.getY();
                return result;
            }
        }
        return result;
    }

    private static boolean isFullSnow(IBlockState top) {
        return package$.MODULE$.isSnow(top) && top.getValue(BlockSnow.LAYERS) == 8;
    }
}
