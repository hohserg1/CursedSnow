package hohserg.cursed.snow

import com.google.common.collect.ImmutableList
import gloomyfolken.hooklib.api._
import hohserg.endothermic.quad.immutable.LazyUnpackedQuad
import net.minecraft.block.BlockSnow
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.{BakedQuad, IBakedModel, ItemOverrideList, ModelResourceLocation}
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraft.world.IBlockAccess
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad
import net.minecraftforge.common.property.{ExtendedBlockState, IExtendedBlockState, IUnlistedProperty}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import java.util.{List => JList}
import scala.collection.JavaConverters._

@HookContainer
object ModifyModel {

  lazy val blockBaseProperty = new IUnlistedProperty[Seq[Box]] {
    override def getName: String = "base"

    override def isValid(value: Seq[Box]): Boolean = true

    override def getType: Class[Seq[Box]] = classOf[Seq[Box]]

    override def valueToString(value: Seq[Box]): String = value.toString
  }

  @Hook
  @OnReturn
  def createBlockState(blockSnow: BlockSnow, @ReturnValue result: BlockStateContainer @ReturnValue): BlockStateContainer = {
    result match {
      case extended: ExtendedBlockState =>
        new ExtendedBlockState(blockSnow, extended.getProperties.asScala.toArray, extended.getUnlistedProperties.asScala.toArray :+ blockBaseProperty)
      case simple =>
        new ExtendedBlockState(blockSnow, simple.getProperties.asScala.toArray, Array(blockBaseProperty))
    }
  }

  @Hook(createMethod = true)
  @OnBegin
  def getExtendedState(blockSnow: BlockSnow, state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState = {
    state match {
      case state: IExtendedBlockState =>
        val boxes = getBaseRelatedLocalBoxes(world, pos)
        state.withProperty(blockBaseProperty, boxes)
      case _ =>
        println("wtf, not IExtendedBlockState passed to BlockSnow#getExtendedState", world, pos)
        state
    }
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  def replaceModel(e: ModelBakeEvent): Unit = {
    for (i <- 1 to 8)
      e.getModelRegistry.putObject(
        new ModelResourceLocation(new ResourceLocation("minecraft", "snow_layer"), "layers=" + i),
        ComplicatedSnowModel
      )

    for (customSnowLoc <- Configuration.extraSnowLikeBlocksModelLocations)
      e.getModelRegistry.putObject(
        new ModelResourceLocation(customSnowLoc),
        ComplicatedSnowModel
      )
  }

  def prepareCoord(v: Double, pos: Int): Double =
    (clamp(v, pos, pos + 1) - pos).toFloat

  @SideOnly(Side.CLIENT)
  object ComplicatedSnowModel extends IBakedModel {
    val texture = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite("minecraft:blocks/snow")

    override def getQuads(state: IBlockState, side: EnumFacing, rand: Long): JList[BakedQuad] = {
      state match {
        case extended: IExtendedBlockState =>
          val baseBoxes = extended.getValue(blockBaseProperty)
          if (baseBoxes != null)
            baseBoxes.flatMap(makeSnowBoxMem).asJava
          else
            defaultModel(extended.getValue(BlockSnow.LAYERS))
        case simple =>
          ImmutableList.of()
      }
    }

    val defaultModel: Int => JList[BakedQuad] = Memoized(l => makeSnowBoxMem(Box(0, 0, 0, 1, l * 1d / 8, 1)).asJava)

    val makeSnowBoxMem: Box => Seq[BakedQuad] = Memoized({
      box: Box =>
        val minX = box.x.toFloat
        val maxX = box.x2.toFloat
        val minY = box.y.toFloat
        val maxY = box.y2.toFloat
        val minZ = box.z.toFloat
        val maxZ = box.z2.toFloat

        val w = maxX - minX
        val h = maxY - minY
        val d = maxZ - minZ

        val baseQuad = LazyUnpackedQuad.apply(emptyQuad)
                                       .reconstruct(
                                         v1_u = texture.getMinU, v1_v = texture.getMinV, v1_lu = 0, v1_lv = 0,
                                         v2_u = texture.getMaxU, v2_v = texture.getMinV, v2_lu = 0, v2_lv = 0,
                                         v3_u = texture.getMaxU, v3_v = texture.getMaxV, v3_lu = 0, v3_lv = 0,
                                         v4_u = texture.getMinU, v4_v = texture.getMaxV, v4_lu = 0, v4_lv = 0
                                       )
        val xSideQuad = baseQuad.reconstruct(
                                  v1_x = minX, v1_y = 0, v1_z = 0,
                                  v2_x = minX, v2_y = 0, v2_z = 1,
                                  v3_x = minX, v3_y = 1, v3_z = 1,
                                  v4_x = minX, v4_y = 1, v4_z = 0
                                )
                                .slice(minZ, 0, maxZ, 0, maxZ, h, minZ, h)
                                .reconstruct(v1_y = minY, v2_y = minY, v3_y = maxY, v4_y = maxY)
        val zSideQuad = baseQuad.reconstruct(
                                  v1_x = 0, v1_y = 0, v1_z = minZ,
                                  v2_x = 1, v2_y = 0, v2_z = minZ,
                                  v3_x = 1, v3_y = 1, v3_z = minZ,
                                  v4_x = 0, v4_y = 1, v4_z = minZ
                                )
                                .slice(minX, 0, maxX, 0, maxX, h, minX, h)
                                .reconstruct(v1_y = minY, v2_y = minY, v3_y = maxY, v4_y = maxY)
        Seq(
          baseQuad.reconstruct(
                    v1_x = 0, v1_y = maxY, v1_z = 0,
                    v2_x = 1, v2_y = maxY, v2_z = 0,
                    v3_x = 1, v3_y = maxY, v3_z = 1,
                    v4_x = 0, v4_y = maxY, v4_z = 1
                  )
                  .slice(minX, minZ, maxX, minZ, maxX, maxZ, minX, maxZ)
                  .reverse
                  .recalculateNormals
                  .toBakedQuad,

          xSideQuad
            .reconstruct(v1_x = maxX, v2_x = maxX, v3_x = maxX, v4_x = maxX)
            .reverse
            .toBakedQuad,

          xSideQuad
            .toBakedQuad, //todo: fuck, if it first it somehow broke other x side

          zSideQuad
            .reverse
            .toBakedQuad,
          zSideQuad
            .reconstruct(v1_z = maxZ, v2_z = maxZ, v3_z = maxZ, v4_z = maxZ)
            .toBakedQuad
        )
    })

    def emptyQuad: BakedQuad = {
      val builder = new UnpackedBakedQuad.Builder(DefaultVertexFormats.BLOCK)
      builder.setTexture(texture)
      for (_ <- 1 to 4) {
        for (i <- 0 until DefaultVertexFormats.BLOCK.getElementCount) {
          builder.put(i, 1, 1, 1, 1)
        }
      }
      builder.build()
    }

    override def isAmbientOcclusion: Boolean = false

    override def isGui3d: Boolean = true

    override def isBuiltInRenderer: Boolean = false

    override def getParticleTexture: TextureAtlasSprite = texture

    override def getOverrides: ItemOverrideList = ItemOverrideList.NONE
  }
}
