package hohserg.cursed.snow

import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.event.ConfigChangedEvent.PostConfigChangedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.ForgeRegistries

object ConfigCache {
  @SubscribeEvent
  def onConfigChange(e: PostConfigChangedEvent): Unit = {
    if (e.getModID == "cursed_snow") {
      rebuild()
    }
  }

  private var snowBaseBlacklistCache: Set[Block] = null
  private var selectionHitboxUsingWhitelistCache: Set[Block] = null
  private var hardcoreDimensionsBlacklistCache: Set[Int] = null

  private def rebuild(): Unit = {
    snowBaseBlacklistCache = blockSet(Configuration.snowBaseBlacklist)

    selectionHitboxUsingWhitelistCache = blockSet(Configuration.selectionHitboxUsingWhitelist)

    hardcoreDimensionsBlacklistCache = Configuration.hardcoreDimensionsBlacklist.toSet
  }

  private def blockSet(config: Array[String]) = {
    config
      .map(new ResourceLocation(_))
      .map(ForgeRegistries.BLOCKS.getValue)
      .filter(_ != null)
      .filter(_ != Blocks.AIR)
      .toSet
  }

  def snowBaseBlacklist: Set[Block] = {
    if (snowBaseBlacklistCache == null)
      rebuild()
    snowBaseBlacklistCache
  }

  def selectionHitboxUsingWhitelist: Set[Block] = {
    if (selectionHitboxUsingWhitelistCache == null)
      rebuild()
    selectionHitboxUsingWhitelistCache
  }

  def hardcoreDimensionsBlacklist: Set[Int] = {
    if (hardcoreDimensionsBlacklistCache == null)
      rebuild()
    hardcoreDimensionsBlacklistCache
  }
}
