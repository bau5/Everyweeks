package com.bau5.everyweeks.remoteinventory

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.IChatComponent

/**
 * Created by bau5 on 9/13/15.
 */
class OpenableInventory(inv: IInventory) extends IInventory {
  override def isUseableByPlayer(player: EntityPlayer) = true

  override def decrStackSize(index: Int, count: Int): ItemStack = inv.decrStackSize(index, count)

  override def closeInventory(player: EntityPlayer): Unit = inv.closeInventory(player)

  override def getSizeInventory: Int = inv.getSizeInventory

  override def clear(): Unit = inv.clear()

  override def getInventoryStackLimit: Int = inv.getInventoryStackLimit

  override def markDirty(): Unit = inv.markDirty()

  override def isItemValidForSlot(index: Int, stack: ItemStack): Boolean = inv.isItemValidForSlot(index, stack)

  override def getStackInSlotOnClosing(index: Int): ItemStack = inv.getStackInSlot(index)

  override def openInventory(player: EntityPlayer): Unit = inv.openInventory(player)

  override def getFieldCount: Int = inv.getFieldCount

  override def getField(id: Int): Int = inv.getField(id)

  override def setInventorySlotContents(index: Int, stack: ItemStack): Unit = inv.setInventorySlotContents(index, stack)

  override def getStackInSlot(index: Int): ItemStack = inv.getStackInSlot(index)

  override def setField(id: Int, value: Int): Unit = inv.setField(id, value)

  override def getDisplayName: IChatComponent = inv.getDisplayName

  override def getName: String = inv.getName

  override def hasCustomName: Boolean = inv.hasCustomName
}
