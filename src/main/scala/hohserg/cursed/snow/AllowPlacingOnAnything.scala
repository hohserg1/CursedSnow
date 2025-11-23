package hohserg.cursed.snow

import gloomyfolken.hooklib.api._
import net.minecraft.block.{BlockSlab, BlockSnow}
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@HookContainer
object AllowPlacingOnAnything {

  @Hook
  @OnBegin
  def canPlaceBlockAt(blockSnow: BlockSnow, worldIn: World, pos: BlockPos): Boolean = {
    val baseState = worldIn.getBlockState(pos.down)
    val base = baseState.getBlock
    if (ConfigCache.snowBaseBlacklist.contains(base))
      false
    else if (Configuration.blacklistAllSlabs)
      !base.isInstanceOf[BlockSlab]
    else if (isSnow(baseState))
      baseState.getValue(BlockSnow.LAYERS) == 8
    else
      base != Blocks.AIR
  }

}
