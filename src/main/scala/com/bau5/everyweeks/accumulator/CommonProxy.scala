package com.bau5.everyweeks.accumulator

import com.bau5.everyweeks.accumulator.container.{GuiAccumulator, ContainerAccumulator}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

/**
 * Created by bau5 on 9/2/2015.
 */
class CommonProxy extends IGuiHandler {
  override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = null

  override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = ID match {
      case 0 => new ContainerAccumulator(player)
    }
}

class ClientProxy extends CommonProxy {
  override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = ID match {
    case 0 => new GuiAccumulator(new ContainerAccumulator(player))
  }
}
