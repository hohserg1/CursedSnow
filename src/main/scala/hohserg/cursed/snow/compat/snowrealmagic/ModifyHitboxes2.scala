package hohserg.cursed.snow.compat.snowrealmagic

import gloomyfolken.hooklib.api._
import hohserg.cursed.snow.isSnow
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.world.{IBlockAccess, World}
import snownee.snow.ModSnowBlock

@HookContainer
object ModifyHitboxes2 {

  @Hook
  @OnBegin
  def getCollisionBoundingBox(blockSnow: ModSnowBlock, blockState: IBlockState, worldIn: IBlockAccess, pos: BlockPos): AxisAlignedBB = {
    Block.NULL_AABB
  }

  @Hook
  @OnBegin
  def canFallThrough(blockSnow: ModSnowBlock, state: IBlockState, worldIn: World, pos: BlockPos): ReturnSolve[java.lang.Boolean] =
    if (isSnow(state))
      ReturnSolve.yes(false)
    else
      ReturnSolve.no()
}
