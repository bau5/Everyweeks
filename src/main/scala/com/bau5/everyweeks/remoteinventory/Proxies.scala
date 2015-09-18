package com.bau5.everyweeks.remoteinventory

import com.bau5.lib.CommonProxyBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.util.BlockPos
import net.minecraft.world.World

/**
 * Created by bau5 on 9/16/2015.
 */
class CommonProxy extends CommonProxyBase{
  override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
    Some(world.getTileEntity(new BlockPos(x, y, z))) match {
      case Some(te) if te.isInstanceOf[IInventory] =>
        new ContainerRemoteInventory(te.asInstanceOf[IInventory], player)
      case _ => null
    }
  }
}

class ClientProxy extends CommonProxy {
  override def registerRenderingInformation(): Unit = super.registerRenderingInformation()

  override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
    Some(world.getTileEntity(new BlockPos(x, y, z))) match {
      case Some(te) if te.isInstanceOf[IInventory] =>
        new GuiRemoteInventory(new ContainerRemoteInventory(te.asInstanceOf[IInventory], player))
      case _ => null
    }
  }
}
