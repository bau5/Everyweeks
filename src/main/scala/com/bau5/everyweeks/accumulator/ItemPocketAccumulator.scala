package com.bau5.everyweeks.accumulator

import com.bau5.everyweeks.accumulator.container.ContainerAccumulator
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World

/**
 * Created by bau5 on 9/2/2015.
 */
class ItemPocketAccumulator() extends Item {
  this.setMaxStackSize(1)
  this.setMaxDamage(1)
  this.setCreativeTab(CreativeTabs.tabMisc)
  this.setUnlocalizedName("accumulator")

  override def onUpdate(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {
    entityIn match {
      case player: EntityPlayer =>
        player.openContainer match {
          case acc: ContainerAccumulator => acc.onItemStackUpdate()
          case _ => ;
        }
      case _ => ;
    }
    super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected)
  }

  override def onItemRightClick(itemStackIn: ItemStack, worldIn: World, playerIn: EntityPlayer): ItemStack = {
    playerIn.isSneaking match {
      case true =>
        val damage = if (itemStackIn.getItemDamage == 1) 0 else 1
        playerIn.getHeldItem.setItemDamage(damage)
      case false =>
        if (!worldIn.isRemote) {
          playerIn.openGui(Accumulator.instance, 0, worldIn, 0, 0, 0)
        }
    }

    println(playerIn.getHeldItem.getItemDamage)

    super.onItemRightClick(itemStackIn, worldIn, playerIn)
  }
}
