package com.bau5.everyweeks.remoteinventory

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.registry.GameRegistry

/**
 * Created by bau5 on 9/12/15.
 */
@Mod(modid = RemoteInventory.MODID, name = RemoteInventory.MOD_NAME,
  version = "1.0", modLanguage = "scala")
object RemoteInventory {
  final val MODID = "remoteinv"
  final val MOD_NAME = "Remote Inventory"

  val remote = new ItemRemoteInventory

  @EventHandler
  def init(ev: FMLInitializationEvent) {
    GameRegistry.registerItem(remote, "remote")
  }
}
