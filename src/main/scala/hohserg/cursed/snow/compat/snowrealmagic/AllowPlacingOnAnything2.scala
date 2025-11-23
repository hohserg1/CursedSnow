package hohserg.cursed.snow.compat.snowrealmagic

import gloomyfolken.hooklib.api._
import hohserg.cursed.snow.AllowPlacingOnAnything
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import snownee.snow.ModSnowBlock

@HookContainer
object AllowPlacingOnAnything2 {

  @Hook
  @OnBegin
  def canPlaceBlockAt(blockSnow: ModSnowBlock, worldIn: World, pos: BlockPos): Boolean =
    AllowPlacingOnAnything.canPlaceBlockAt(blockSnow, worldIn, pos)

}
