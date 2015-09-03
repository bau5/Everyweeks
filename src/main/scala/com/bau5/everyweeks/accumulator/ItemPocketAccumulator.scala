package com.bau5.everyweeks.accumulator

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
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

  override def onItemRightClick(itemStackIn: ItemStack, worldIn: World, playerIn: EntityPlayer): ItemStack = {
    if (!worldIn.isRemote) {
      playerIn.openGui(Accumulator.instance, 0, worldIn, 0, 0, 0)
    }

    super.onItemRightClick(itemStackIn, worldIn, playerIn)
  }
}
