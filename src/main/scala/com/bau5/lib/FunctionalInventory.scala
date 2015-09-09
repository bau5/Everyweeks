package com.bau5.lib

import com.bau5.lib.FunctionalInventory.Inventory
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack


/**
 * Created by bau5 on 9/7/15.
 */
object FunctionalInventory {
  type Inventory = IInventory

  implicit def toRichInv(inv: Inventory): FunctionalInventory = new FunctionalInventory(inv)
}

class FunctionalInventory (inv: Inventory) extends Iterable[PositionedStack] {
  override def iterator: Iterator[PositionedStack] = new Iterator[PositionedStack] {
    var idx = 0

    override def hasNext: Boolean = idx < inv.getSizeInventory

    override def next(): PositionedStack = {
      val entry = (idx, inv.getStackInSlot(idx))
      idx += 1
      PositionedStack(entry)
    }
  }

  /**
   * This function will modify the inventory's contents by executing func and applying the
   * result to the original index in the inventory. Lists returned from here will not
   * exclude null stacks, unlike getPositionedStacks and getStacks
   *
   * @param func
   * @return
   */
  def mapInventory(func: PositionedStack => PositionedStack): List[PositionedStack] = {
    getPositionedStacks().map { pos =>
      val ret = func(pos)
      inv.setInventorySlotContents(ret.idx, ret.stack)
      ret
    }
  }

  def hasItemStack(find: ItemStack, strict: Boolean = false): Boolean = strict match {
    case true => getStacks().exists(_.getIsItemStackEqual(find))
    case false => getStacks().exists(_.isItemEqual(find))
  }

  def isDefined(idx: Int): Boolean = inv.getStackInSlot(idx) != null

  /**
   * Gets all stacks from the inventory <b>that are NOT null</b>
   * @return non-null Positioned stacks
   */
  def getPositionedStacks(): List[PositionedStack] = getAllPositionedStacks().filter(_.stack != null)
  /**
   * Gets all stacks from the inventory <b>that are NOT null</b>
   * @return non-null stacks
   */
  def getStacks(): List[ItemStack] = getAllStacks().filter(_ != null)

  def getAllStacks(): List[ItemStack] = getAllPositionedStacks().map(_.stack)

  def getAllPositionedStacks(): List[PositionedStack] = iterator.toList
}

object PositionedStack {
  def apply(tup: (Int, ItemStack)): PositionedStack = new PositionedStack(tup._1, tup._2)
}

case class PositionedStack(idx: Int, stack: ItemStack) {
  lazy val nulled = new PositionedStack(idx, null)
}
