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
class ContainerAccumulator(player: EntityPlayer, damage: Int) extends Container {
  val tag = getTag(player.getHeldItem)
  val crafting = new InventoryCrafting(this, 3, 3)
  val craftResult = new InventoryCraftResult

  loadInventoryFromNBT(tag)

  var needsUpdate = true

  forSlots(3, 9, 9, 8, 84, 9) { (index, x, y) =>
    new Slot(player.inventory, index, x, y)
  }

  forSlots(1, 9, 9, 8, 142, 0) { (index, x, y) =>
    new Slot(player.inventory, index, x, y)
  }

  forSlots(3, 3, 3, 30, 17, 0) { (index, x, y) =>
    new Slot(crafting, index, x, y)
  }

  this.addSlotToContainer(new SlotCrafting(player, crafting, craftResult, 0, 124, 35))

  override def slotClick(slotId: Int, clickedButton: Int, mode: Int, playerIn: EntityPlayer): ItemStack = {
    if (slotId < inventorySlots.size() && slotId >= 0) {
      Option(inventorySlots.get(slotId).asInstanceOf[Slot].getStack) match {
        case Some(s) if s.getItem.equals(Accumulator.accumulator) => return null
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
      val stacks = {
        for (i <- 0 until crafting.getSizeInventory) yield Option(crafting.getStackInSlot(i))
      }.filter(_.isDefined).map { opt =>
        val s = opt.get.copy()
        s.stackSize = 1
        s
      }
      stacks.headOption match {
        case Some(s) if stacks.size > 1 =>
          if (stacks.forall(_.isItemEqual(s))) {
            craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(crafting, player.worldObj))

            val tag = new NBTTagCompound
            for (idx <- 0 until crafting.getSizeInventory) {
              Option(crafting.getStackInSlot(idx)).foreach {
                s => tag.setTag(s"$idx", s.writeToNBT(new NBTTagCompound))
              }
            }
            player.getHeldItem.setTagCompound(tag)
          }
        case _ => ;
      }
      needsUpdate = false
    }
  }

  def loadInventoryFromNBT(tag: NBTTagCompound) {
    for (index <- 0 until 9) {
      if (tag.hasKey(s"$index")) {
        crafting.setInventorySlotContents(index, ItemStack.loadItemStackFromNBT(tag.getTag(s"$index").asInstanceOf[NBTTagCompound]))
      }
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


  override def onContainerClosed(playerIn: EntityPlayer): Unit = {
    super.onContainerClosed(playerIn)

    val acc = playerIn.getHeldItem
    acc.setItemDamage(damage)

    if (playerIn.worldObj.isRemote && damage == 1) {
      playerIn.playSound("accumulator:rope", 0.5F, 1.0F)
    }

    if (craftResult.getStackInSlot(0) == null && !playerIn.worldObj.isRemote) {
      InventoryHelper.dropInventoryItems(playerIn.worldObj, playerIn.getPosition, crafting)
      for (i <- 0 until crafting.getSizeInventory) {
        crafting.setInventorySlotContents(i, null)
        
        if (acc.getTagCompound.hasKey(s"$i")) {
          acc.getTagCompound.removeTag(s"$i")
        }
      }

      if (acc.getTagCompound.hasKey("result")) {
        acc.getTagCompound.removeTag("result")
      }
    } else if (craftResult.getStackInSlot(0) != null) {
      val stack = playerIn.getHeldItem
      val tag = Option(stack.getTagCompound).getOrElse(new NBTTagCompound)
      tag.setTag("result", craftResult.getStackInSlot(0).writeToNBT(new NBTTagCompound))
      stack.setTagCompound(tag)
    }
  }

  override def canInteractWith(playerIn: EntityPlayer) = true

  override def transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack = {
    var itemstack: ItemStack = null
    val slot = this.inventorySlots.get(index).asInstanceOf[Slot]

    if (slot != null && slot.getHasStack) {
      if (slot.getStack.getItem.eq(Accumulator.accumulator)) {
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
