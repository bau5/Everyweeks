package com.bau5.everyweeks.accumulator

import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.{SidedProxy, Mod}
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.{FMLPostInitializationEvent, FMLInitializationEvent}
import net.minecraftforge.fml.common.registry.GameRegistry

@Mod(modid = Accumulator.MOD_ID, name = Accumulator.NAME,
  version = Accumulator.VERSION, modLanguage = "scala")
object Accumulator {
  final val MOD_ID = "accumulator"
  final val VERSION = "1.0"
  final val NAME = "Pocket Accumulator"

  @Mod.Instance(Accumulator.MOD_ID)
  var instance = this

  @SidedProxy(
    serverSide = "com.bau5.everyweeks.accumulator.CommonProxy",
    clientSide = "com.bau5.everyweeks.accumulator.ClientProxy"
  )
  var proxy: CommonProxy = _

  val pocketAccumulator = new ItemPocketAccumulator()

  @EventHandler
  def init(ev: FMLInitializationEvent) {
    GameRegistry.registerItem(pocketAccumulator, "pocket_accumulator")
  }

  @EventHandler
  def postInit(ev: FMLPostInitializationEvent) {
    NetworkRegistry.INSTANCE.registerGuiHandler(Accumulator.instance, Accumulator.proxy)
  }
}