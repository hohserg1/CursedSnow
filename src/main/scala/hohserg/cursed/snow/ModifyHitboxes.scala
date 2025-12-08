package hohserg.cursed.snow

import gloomyfolken.hooklib.api._
import net.minecraft.block.state.IBlockState
import net.minecraft.block.{Block, BlockSnow}
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemSnow
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.util.{EnumFacing, EnumHand}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@HookContainer
object ModifyHitboxes {

  @Hook
  @OnBegin
  def getCollisionBoundingBox(blockSnow: BlockSnow, blockState: IBlockState, worldIn: IBlockAccess, pos: BlockPos): AxisAlignedBB = {
    Block.NULL_AABB
  }

  @Hook(targetMethod = "onItemUse")
  @OnMethodCall(value = "getCollisionBoundingBox", shift = Shift.INSTEAD)
  def placingNextLayerByHand1(itemSnow: ItemSnow, player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): AxisAlignedBB =
    Block.FULL_BLOCK_AABB

  @Hook(targetMethod = "onItemUse")
  @OnMethodCall(value = "checkNoEntityCollision", shift = Shift.INSTEAD)
  def placingNextLayerByHand2(itemSnow: ItemSnow, player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean =
    true

  @SideOnly(Side.CLIENT)
  @Hook(createMethod = true)
  @OnBegin
  def getSelectedBoundingBox(blockSnow: BlockSnow, state: IBlockState, worldIn: World, pos: BlockPos): ReturnSolve[AxisAlignedBB] = {
    val boxes = getBaseRelatedAbsoluteBoxes(worldIn, pos)
    if (boxes.isEmpty)
      ReturnSolve.no()
    else {
      ReturnSolve.yes(new AxisAlignedBB(
        boxes.minBy(_.minX).minX,
        boxes.minBy(_.minY).minY,
        boxes.minBy(_.minZ).minZ,
        boxes.maxBy(_.maxX).maxX,
        boxes.maxBy(_.maxY).maxY,
        boxes.maxBy(_.maxZ).maxZ
      ))
    }
  }

  @Hook(createMethod = true)
  @OnBegin
  def onEntityCollision(blockSnow: BlockSnow,
                        worldIn: World, pos: BlockPos, state: IBlockState, entityIn: Entity): Unit = {
    val layers = state.getValue(BlockSnow.LAYERS)
    val height = layers * 1f / 8
    if (entityIn.posY < pos.getY + height) {
      val slowdown = (9 - layers) * 0.1f
      entityIn.fallDistance -= layers
      entityIn.motionX *= slowdown
      entityIn.motionY *= clamp(slowdown * 1.5f, 0f, 0.95f)
      entityIn.motionZ *= slowdown
    }
  }

  @Hook
  @OnBegin
  def isPassable(blockSnow: BlockSnow,
                 worldIn: IBlockAccess, pos: BlockPos): Boolean = true
}
