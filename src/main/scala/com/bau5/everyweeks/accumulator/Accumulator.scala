package com.bau5.everyweeks.accumulator

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.registry.GameRegistry

@Mod(modid = Accumulator.MOD_ID, name = Accumulator.NAME, version = Accumulator.VERSION)
class Accumulator {
  val pocketAccumulator = new ItemPocketAccumulator()

  @EventHandler
  def init(ev: FMLInitializationEvent) {
    GameRegistry.registerItem(pocketAccumulator, "pocket_accumulator")
  }
}

object Accumulator {
  final val MOD_ID = "accumulator"
  final val VERSION = "1.0"
  final val NAME = "Pocket Accumulator"
}