package com.bau5.everyweeks.accumulator.container

import com.bau5.everyweeks.accumulator.Accumulator
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory._
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.nbt.NBTTagCompound

/**
 * Created by bau5 on 9/2/2015.
 */
class ContainerAccumulator(player: EntityPlayer) extends Container {
  val tag = getTag(player.getHeldItem)
  val crafting = new InventoryCrafting(this, 3, 3)
  val craftResult = new InventoryCraftResult

  var needsUpdate = true

  forSlots(3, 9, 9, 8, 84, 9) { (index, x, y) =>
    new Slot(player.inventory, index, x, y)
  }

  forSlots(1, 9, 9, 8, 142, 0) { (index, x, y) =>
    new Slot(player.inventory, index, x, y)
  }

  forSlots(3, 3, 3, 30, 17, 0) { (index, x, y) =>
    if (tag.hasKey(s"$index")) {
      crafting.setInventorySlotContents(index, ItemStack.loadItemStackFromNBT(tag.getTag(s"$index").asInstanceOf[NBTTagCompound]))
    }
    new Slot(crafting, index, x, y)
  }

  this.addSlotToContainer(new SlotCrafting(player, crafting, craftResult, 0, 124, 35))

  override def slotClick(slotId: Int, clickedButton: Int, mode: Int, playerIn: EntityPlayer): ItemStack = {
    if (slotId < inventorySlots.size() && slotId >= 0) {
      Option(inventorySlots.get(slotId).asInstanceOf[Slot].getStack) match {
        case Some(s) if s.getItem.equals(Accumulator.pocketAccumulator) => return null
        case _ => ;
      }

      if (slotId == 45) {
        onCraftMatrixChanged(crafting)
      }
    }

    val stack = super.slotClick(slotId, clickedButton, mode, playerIn)
    stack
  }

  private def forSlots(rows: Int, cols: Int, rowLength: Int, xOff: Int, yOff: Int, indexOff: Int)(func: (Int, Int, Int) => Slot) {
    for (row <- 0 until rows) {
      for (col <- 0 until cols) {
        val index = col + row * rowLength + indexOff
        val x = xOff + col * 18
        val y = yOff + row * 18
        this.addSlotToContainer(func(index, x, y))
      }
    }
  }

  def onItemStackUpdate() {
    if (needsUpdate) {
      craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(crafting, player.worldObj))

      val tag = new NBTTagCompound
      for (idx <- 0 until crafting.getSizeInventory) {
        Option(crafting.getStackInSlot(idx)).foreach {
          s => tag.setTag(s"$idx", s.writeToNBT(new NBTTagCompound))
        }
      }
      player.getHeldItem.setTagCompound(tag)
      needsUpdate = false
    }
  }

  override def onCraftMatrixChanged(inventoryIn: IInventory) {
    needsUpdate = true
  }

  def getTag(stack: ItemStack): NBTTagCompound =
    Option(stack.getTagCompound).getOrElse {
      stack.setTagCompound(new NBTTagCompound)
      stack.getTagCompound
    }

  override def canInteractWith(playerIn: EntityPlayer) = true

  override def transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack = {
    var itemstack: ItemStack = null
    val slot = this.inventorySlots.get(index).asInstanceOf[Slot]

    if (slot != null && slot.getHasStack) {
      if (slot.getStack.getItem.eq(Accumulator.pocketAccumulator)) {
        return null
      }
      val itemstack1 = slot.getStack
      itemstack = itemstack1.copy()

      if (index < 27) {
        if (!this.mergeItemStack(itemstack1, 27, this.inventorySlots.size(), true)) {
          return null
        }
      } else if (!this.mergeItemStack(itemstack1, 0, 27, false)) {
        return null
      }

      if (itemstack1.stackSize == 0) {
        slot.putStack(null)
        //Otherwise crafting slot wasn't eating crafting components
        slot.onPickupFromSlot(player, itemstack)
      } else {
        slot.onSlotChanged()
      }
    }

    itemstack
  }
}