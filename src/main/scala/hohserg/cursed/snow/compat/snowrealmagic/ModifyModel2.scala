package hohserg.cursed.snow.compat.snowrealmagic

import gloomyfolken.hooklib.api._
import hohserg.cursed.snow.ModifyModel
import net.minecraft.block.state.BlockStateContainer
import snownee.snow.ModSnowBlock

@HookContainer
object ModifyModel2 {

  @Hook
  @OnReturn
  def createBlockState(blockSnow: ModSnowBlock, @ReturnValue result: BlockStateContainer @ReturnValue): BlockStateContainer =
    ModifyModel.createBlockState(blockSnow, result)
}
