package com.bau5.everyweeks.accumulator

import com.bau5.everyweeks.accumulator.container.ContainerAccumulator
import com.bau5.lib.ProxyBase
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.player.EntityItemPickupEvent
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.common.{Mod, SidedProxy}


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
  var proxy: ProxyBase = _

  val accumulator = new ItemAccumulator()

  @EventHandler
  def init(ev: FMLInitializationEvent) {
    GameRegistry.registerItem(accumulator, "accumulator")
    MinecraftForge.EVENT_BUS.register(new ItemPickupHandler())
    NetworkRegistry.INSTANCE.registerGuiHandler(Accumulator.instance, Accumulator.proxy)
    proxy.registerRenderingInformation()
  }
}

class ItemPickupHandler {
  @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
  def onEvent(ev: EntityItemPickupEvent) = ev.isCanceled match {
    case false =>
      // the item to be added
      val addToInv = ev.item.getEntityItem
      if (ev.entityPlayer.inventory.hasItem(Accumulator.accumulator)) {
        //if env is full, event repeats
        // get player's inventory and find accumulators
        val playerStacks = for (i <- 0 until ev.entityPlayer.inventory.getSizeInventory) yield ev.entityPlayer.inventory.getStackInSlot(i)
        val accumulators = playerStacks.filter(e => e != null && e.getItem.equals(Accumulator.accumulator))
        val use = accumulators.filter(_.hasTagCompound)
        var done = false
        // for each accumulator do...
        for (acc <- use if !done && acc.getItemDamage == 0) {
          val tag = acc.getTagCompound
          // load stacks from tag and find matching ones
          val stackTags = for (i <- 0 until 9) yield (i, Option(tag.getTag(s"$i")))
          val stacks = stackTags.filter(_._2.isDefined)
            .map(e => e._1 -> ItemStack.loadItemStackFromNBT(e._2.get.asInstanceOf[NBTTagCompound]))
          val matching = stacks.filter(_._2.isItemEqual(addToInv))

          val numMatches = matching.size
          if (numMatches > 0) {
            // compute the new stack size, we distribute the items evenly across all occurrences in the inv
            val existing = stacks.map(_._2.stackSize).sum
            val newTotal = existing + addToInv.stackSize
            val summedLeftOver = newTotal % numMatches

            matching foreach (_._2.stackSize = newTotal / numMatches)
            for (i <- 0 until summedLeftOver) matching(i)._2.stackSize += 1

            // collect any items that exceeded max stack size, will be added by vanilla code
            var leftOver = 0
            matching foreach { case (idx, stack) =>
              val extra = stack.stackSize - stack.getMaxStackSize
              if (extra > 0) {
                stack.stackSize = stack.getMaxStackSize
                leftOver += extra
              }
            }
            ev.item.getEntityItem.stackSize = leftOver

            // save to nbt
            matching foreach { case (idx, stack) =>
              tag.setTag(s"$idx", stack.writeToNBT(new NBTTagCompound))
            }
            acc.setTagCompound(tag)

            ev.entityPlayer.openContainer match {
              case cont: ContainerAccumulator =>
                cont.loadInventoryFromNBT(tag)
                cont.needsUpdate = true
                cont.onItemStackUpdate()
              case _ => ;
            }

            done = leftOver == 0
          }
        }
      }
    case true => ;
  }
}
