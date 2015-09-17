package com.bau5.everyweeks.accumulator

import com.bau5.everyweeks.accumulator.container.{ContainerAccumulator, GuiAccumulator}
import com.bau5.lib.{CommonProxyBase, ItemModelRegistrar}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World


/**
 * Created by bau5 on 9/2/2015.
 */
class CommonProxy extends CommonProxyBase {
  override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = ID match {
    case 0 => new ContainerAccumulator(player, x)
  }
}

class ClientProxy extends CommonProxy {
  override def registerRenderingInformation() {
    val accSpecifications = List(
      (0, "accumulator"),
      (1, "accumulator_closed"),
      (2, "accumulator")
    )
    ItemModelRegistrar.registerModelsWithMetadata(Accumulator.accumulator, Accumulator.MOD_ID, "inventory", accSpecifications)
  }

  override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = ID match {
    case 0 => new GuiAccumulator(new ContainerAccumulator(player, x))
  }
}
