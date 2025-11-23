package hohserg.cursed.snow

import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.{DamageSource, SoundCategory, SoundEvent}
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent

object ColdDamageServer {
  @SubscribeEvent
  def tick(e: PlayerTickEvent): Unit = {
    e.player match {
      case player: EntityPlayerMP =>
        val inSnowTime = ColdDamageLens.inSnowTime.get(player)
        ColdDamageLens.inSnowTime.set(player, progressInSnowTime(player, inSnowTime))
        if (inSnowTime > maxInSnowTime / 3) {
          if (!ColdDamageLens.freezingPlayed.get(player)) {
            ColdDamageLens.freezingPlayed.set(player, true)
            player.world.playSound(null, player.posX, player.posY, player.posZ, ColdDamageSound, SoundCategory.AMBIENT, 1, 1)
          }
        }
        if (inSnowTime > maxInSnowTime / 3 * 2) {
          if (player.getHealth > 1) {
            if (player.isInsideOfMaterial(Material.SNOW)) {
              if (player.ticksExisted % math.min(25, 1000 / inSnowTime) == 0) {
                player.attackEntityFrom(DamageSource.MAGIC, 1)
              }
            }
          }
        } else if (inSnowTime == 0) {
          ColdDamageLens.freezingPlayed.set(player, false)
        }
      case _ =>
    }
  }

  @SubscribeEvent
  def registerSounds(e: RegistryEvent.Register[SoundEvent]): Unit = {
    e.getRegistry.register(ColdDamageSound)
  }
}
