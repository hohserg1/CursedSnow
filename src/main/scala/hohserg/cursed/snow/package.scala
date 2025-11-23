package hohserg.cursed

import hohserg.cursed.snow.ModifyModel.prepareCoord
import net.minecraft.block.BlockSnow
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import java.util
import scala.annotation.tailrec
import scala.collection.JavaConverters._

package object snow {

  def clamp[N: Numeric](v: N, min: N, max: N): N = {
    val n = implicitly[Numeric[N]]
    n.max(n.min(v, max), min)
  }

  @tailrec
  def getBottomNonSnowBlock(world: IBlockAccess, pos: BlockPos): (BlockPos, IBlockState) = {
    val state = world.getBlockState(pos)
    if (isSnow(state))
      getBottomNonSnowBlock(world, pos.down)
    else
      (pos, state.getActualState(world, pos))
  }

  case class Box(x: Double, y: Double, z: Double,
                 x2: Double, y2: Double, z2: Double) {
    def erase(other: Box): Seq[Box] = {
      val ix = math.max(x, other.x)
      val iz = math.max(z, other.z)
      val ix2 = math.min(x2, other.x2)
      val iz2 = math.min(z2, other.z2)
      if (ix < ix2 && iz < iz2) {
        Seq(
          Box(x, y, z, x2, y2, iz),
          Box(x, y, iz, ix, y2, iz2),
          Box(ix2, y, iz, x2, y2, iz2),
          Box(x, y, iz2, x2, y2, z2)
        ).filter(_.nonEmpty)
      } else
        Seq(this)
    }

    def nonEmpty: Boolean = x < x2 && y < y2 && z < z2

    def offsetAABB(pos: BlockPos): AxisAlignedBB =
      new AxisAlignedBB(x, y, z, x2, y2, z2).offset(pos)
  }

  def addTopBox(boxes: Seq[Box], newBox: Box): Seq[Box] = {
    boxes.flatMap(_.erase(newBox)) :+ newBox
  }


  @SideOnly(Side.CLIENT)
  def getBaseRelatedLocalBoxes(world: IBlockAccess, snowPos: BlockPos): Seq[Box] = {
    val height = world.getBlockState(snowPos).getValue(BlockSnow.LAYERS) * 1d / 8
    val (pos, state) = getBottomNonSnowBlock(world, snowPos)
    val haveFullBlockDown = world.isSideSolid(pos.down, EnumFacing.UP, false)
    val baseBoxes = new util.ArrayList[AxisAlignedBB]()
    state.getBlock.addCollisionBoxToList(state, Minecraft.getMinecraft.world, pos, new AxisAlignedBB(pos), baseBoxes, null, true)
    overlapBoxes((
      baseBoxes.asScala
               .map { aabb =>
                 val y = prepareCoord(aabb.maxY, pos.getY)
                 Box(
                   prepareCoord(aabb.minX, pos.getX),
                   y - 1,
                   prepareCoord(aabb.minZ, pos.getZ),
                   prepareCoord(aabb.maxX, pos.getX),
                   y - 1 + height,
                   prepareCoord(aabb.maxZ, pos.getZ)
                 )
               },
      haveFullBlockDown,
      height
    ))
  }

  val overlapBoxes: ((Seq[Box], Boolean, Double)) => Seq[Box] = Memoized({ case (boxes: Seq[Box], haveFullBlockDown: Boolean, snowHeight: Double) =>
    boxes
      .sortBy(_.y)
      .foldLeft(if (haveFullBlockDown) Seq(Box(0, -1, 0, 1, -1 + snowHeight, 1)) else Seq.empty)(addTopBox)
  })

  @SideOnly(Side.CLIENT)
  def getBaseRelatedAbsoluteBoxes(world: IBlockAccess, snowPos: BlockPos): Seq[AxisAlignedBB] = {
    getBaseRelatedLocalBoxes(world, snowPos).map(_.offsetAABB(snowPos))
  }

  def isSnow(b: IBlockState): Boolean =
    b.getBlock.isInstanceOf[BlockSnow]

  var maxInSnowTime = 20 * 10

  def progressInSnowTime(player: EntityPlayer, v: Int): Int = {
    if (player.isInsideOfMaterial(Material.SNOW))
      math.min(maxInSnowTime, v + 1)
    else
      math.max(0, v - 3)
  }

  def isHardcoreSnowDimension(world: World): Boolean =
    !ConfigCache.hardcoreDimensionsBlacklist.contains(world.provider.getDimension)
}
