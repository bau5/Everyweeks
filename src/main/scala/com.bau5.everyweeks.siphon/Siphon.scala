package com.bau5.everyweeks.siphon

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.registry.GameRegistry


/**
 * Created by bau5 on 8/22/15.
 */
@Mod(modid = Siphon.MODID, version = Siphon.VERSION)
class Siphon {

  @EventHandler
  def init(ev: FMLInitializationEvent) {
    GameRegistry.registerTileEntity(classOf[TileEntitySiphon], "te_siphon")
    GameRegistry.registerBlock(Siphon.siphonBlock, "block_siphon")
  }
}

object Siphon {
  final val MODID = "siphon"
  final val VERSION = "1.0"

  val siphonBlock = new BlockSiphon()
}
