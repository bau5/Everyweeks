package com.bau5.everyweeks.accumulator

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.world.World

/**
 * Created by bau5 on 9/2/2015.
 */
class ItemPocketAccumulator() extends Item {
  this.setMaxStackSize(1)
  this.setUnlocalizedName("pocket_accumulator")
  this.setCreativeTab(CreativeTabs.tabMisc)

  override def onUpdate(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {
    super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected)
  }
}
