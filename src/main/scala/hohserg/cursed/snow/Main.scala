package hohserg.cursed.snow

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@Mod(modid = Main.modid, name = "CursedSnow", modLanguage = "scala")
object Main {
  final val modid = "cursed_snow"

  @SideOnly(Side.CLIENT)
  @EventHandler
  def preInitClient(e: FMLPreInitializationEvent): Unit = {
    MinecraftForge.EVENT_BUS.register(ColdDamageClientIndicator)
    MinecraftForge.EVENT_BUS.register(ModifyModel)
  }

  @EventHandler
  def preInit(e: FMLPreInitializationEvent): Unit = {
    MinecraftForge.EVENT_BUS.register(ColdDamageServer)
    MinecraftForge.EVENT_BUS.register(ConfigCache)
  }

}
