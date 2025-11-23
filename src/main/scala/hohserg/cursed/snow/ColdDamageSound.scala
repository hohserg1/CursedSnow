package hohserg.cursed.snow

import net.minecraft.util.{ResourceLocation, SoundEvent}

object ColdDamageSound extends CustomSoundEvent(new ResourceLocation(Main.modid, "freezing"))

class CustomSoundEvent(name: ResourceLocation) extends SoundEvent(name) {
  setRegistryName(name)
}
