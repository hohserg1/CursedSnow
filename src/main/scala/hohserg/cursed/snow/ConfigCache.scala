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

  private def rebuild(): Unit = {
    snowBaseBlacklistCache = Configuration.snowBaseBlacklist
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
}
