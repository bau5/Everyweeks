package com.bau5.lib

import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack


/**
 * Created by bau5 on 9/7/15.
 */
object FunctionalInventory {
  type Inventory = IInventory

  implicit def toRichInv(inv: Inventory) = new FunctionalInventory(inv)
}

class FunctionalInventory (inv: FunctionalInventory.Inventory) {
  def indexedStacks(): List[(Int, ItemStack)] = {
    for(i <- 0 until inv.getSizeInventory) yield (i, inv.getStackInSlot(i))
  }.toList

  def collectStacks(): List[ItemStack] = {
    for(i <- 0 until inv.getSizeInventory) yield Option(inv.getStackInSlot(i))
  }.filter(_.isDefined).map(_.get).toList
}