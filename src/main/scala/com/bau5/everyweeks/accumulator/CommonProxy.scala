package com.bau5.everyweeks.accumulator

import com.bau5.everyweeks.accumulator.container.{ContainerAccumulator, GuiAccumulator}
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.model.{ModelBakery, ModelResourceLocation}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

/**
 * Created by bau5 on 9/2/2015.
 */
class CommonProxy extends IGuiHandler {

  def registerRenderingInformation() {}

  override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = null

  override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = ID match {
    case 0 => new ContainerAccumulator(player, x)
  }
}

class ClientProxy extends CommonProxy {

  override def registerRenderingInformation() {
    Minecraft.getMinecraft.getRenderItem.getItemModelMesher.register(
      Accumulator.accumulator, 2,
      new ModelResourceLocation(Accumulator.MOD_ID + ":accumulator", "inventory"))
    Minecraft.getMinecraft.getRenderItem.getItemModelMesher.register(
      Accumulator.accumulator, 1,
      new ModelResourceLocation(Accumulator.MOD_ID + ":accumulator_closed", "inventory"))
    Minecraft.getMinecraft.getRenderItem.getItemModelMesher.register(
      Accumulator.accumulator, 0,
      new ModelResourceLocation(Accumulator.MOD_ID + ":accumulator", "inventory"))

    ModelBakery.addVariantName(Accumulator.accumulator,
      "accumulator:accumulator", "accumulator:accumulator_closed")
  }

  override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = ID match {
    case 0 => new GuiAccumulator(new ContainerAccumulator(player, x))
  }
}
