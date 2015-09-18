package com.bau5.everyweeks.remoteinventory

import com.bau5.everyweeks.accumulator.Accumulator
import net.minecraftforge.fml.common.{SidedProxy, Mod}
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
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

  @Mod.Instance(Accumulator.MOD_ID)
  var instance = this

  @SidedProxy(
    serverSide = "com.bau5.everyweeks.remoteinventory.CommonProxy",
    clientSide = "com.bau5.everyweeks.remoteinventory.ClientProxy"
  )
  var proxy: CommonProxy = _

  @EventHandler
  def init(ev: FMLInitializationEvent) {
    GameRegistry.registerItem(remote, "remote")
    NetworkRegistry.INSTANCE.registerGuiHandler(RemoteInventory.instance, proxy)
  }
}
