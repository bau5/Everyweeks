package com.bau5.everyweeks.remoteinventory

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.{Slot, IInventory, Container}
import net.minecraft.item.ItemStack

/**
 * Created by bau5 on 9/16/2015.
 */
class ContainerRemoteInventory(val inventory: IInventory, val player: EntityPlayer) extends Container {
  inventory.openInventory(player)
  val size = inventory.getSizeInventory
  val fullRows = size / 9
  val extra = size % 9
  val hasExtra = extra >= 0
  val totalRows = if (hasExtra) fullRows + 1 else fullRows

  val offset = (totalRows - 4) * 18

  var idx = 0
  for (row <- 0 until totalRows) {
    if (hasExtra && row + 1 == totalRows) {
      val defWidth = 9 * 18
      val rowWidth = extra * 18
      val start = (defWidth - rowWidth) / 2
      for (col <- 0 until extra) {
        addSlotToContainer(new Slot(inventory, idx, 8 + start + col * 18, 18 + row * 18))
        idx += 1
      }
    } else {
      for (col <- 0 until 9) {
        addSlotToContainer(new Slot(inventory, idx, 8 + col * 18, 18 + row * 18))
        idx += 1
      }
    }
  }

  for (row <- 0 until 3) {
    for (col <- 0 until 9) {
      addSlotToContainer(new Slot(player.inventory, col + row * 9 + 9, 8 + col * 18, 103 + row * 18 + offset))
    }
  }

  for (col <- 0 until 9) {
    addSlotToContainer(new Slot(player.inventory, col, 8 + col * 18, 161 + offset))
  }

  override def canInteractWith(playerIn: EntityPlayer): Boolean = true

  override def transferStackInSlot (playerIn: EntityPlayer, index: Int): ItemStack = {
    var itemstack: ItemStack = null
    val slot: Slot = this.inventorySlots.get(index).asInstanceOf[Slot]
    if (slot != null && slot.getHasStack) {
      val itemstack1: ItemStack = slot.getStack
      itemstack = itemstack1.copy
      if (index < this.totalRows * 9) {
        if (!this.mergeItemStack(itemstack1, this.totalRows * 9, this.inventorySlots.size, true)) {
          return null
        }
      }
      else if (!this.mergeItemStack(itemstack1, 0, this.totalRows * 9, false)) {
        return null
      }
      if (itemstack1.stackSize == 0) {
        slot.putStack(null)
      }
      else {
        slot.onSlotChanged()
      }
    }
    itemstack
  }

  override def onContainerClosed(playerIn: EntityPlayer): Unit = {
    super.onContainerClosed(playerIn)
    inventory.closeInventory(playerIn)
  }
}
