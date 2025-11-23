package hohserg.cursed.snow

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GlStateManager, Tessellator}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}
import net.minecraftforge.fml.common.gameevent.TickEvent.{ClientTickEvent, Phase}
import net.minecraftforge.fml.relauncher.Side
import org.lwjgl.opengl.GL11

object ColdDamageClientIndicator {

  var inSnowTime = 0

  val texture = new ResourceLocation(Main.modid, "textures/gui/cold_vignette.png")

  @SubscribeEvent(priority = EventPriority.LOW)
  def draw(e: RenderGameOverlayEvent.Pre): Unit = {
    if (e.getType == ElementType.VIGNETTE) {
      if (!e.isCanceled) {
        if (inSnowTime > 0) {
          GlStateManager.disableDepth()
          GlStateManager.depthMask(false)
          GlStateManager.enableBlend()
          GlStateManager.disableAlpha()
          GlStateManager.color(1, 1, 1, 0.5f * (inSnowTime + e.getPartialTicks) / maxInSnowTime)


          Minecraft.getMinecraft.getTextureManager.bindTexture(texture)

          val w = e.getResolution.getScaledWidth.toDouble
          val h = e.getResolution.getScaledHeight.toDouble

          val buffer = Tessellator.getInstance.getBuffer
          buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
          buffer.pos(0, h, -90).tex(0, 1).endVertex()
          buffer.pos(w, h, -90).tex(1, 1).endVertex()
          buffer.pos(w, 0, -90).tex(1, 0).endVertex()
          buffer.pos(0, 0, -90).tex(0, 0).endVertex()
          Tessellator.getInstance.draw()
        }
      }
    }
  }

  @SubscribeEvent
  def tick(e: ClientTickEvent): Unit = {
    if (e.phase == Phase.END) {
      val player = Minecraft.getMinecraft.player
      if (player != null) {
        if (!Minecraft.getMinecraft.isGamePaused) {
          if (isHardcoreSnowDimension(player.world)) {
            inSnowTime = progressInSnowTime(player, inSnowTime)
          }
        }
      }
    }
  }
}
