package com.bau5.lib

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

/**
 * Created by bau5 on 9/16/2015.
 */
abstract class ProxyBase extends IGuiHandler {
  def registerRenderingInformation()
}

trait CommonProxyBase extends ProxyBase {
  override def registerRenderingInformation() {}
  override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = null
}
