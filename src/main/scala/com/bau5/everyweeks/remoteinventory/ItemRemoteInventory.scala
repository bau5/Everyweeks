package com.bau5.everyweeks.remoteinventory

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.{ItemStack, Item}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{EnumFacing, BlockPos}
import net.minecraft.world.World

import com.bau5.lib.RichNBTTagCompound._

/**
 * Created by bau5 on 9/12/15.
 */
class ItemRemoteInventory extends Item {
  this.setMaxStackSize(1)
  this.setCreativeTab(CreativeTabs.tabMisc)

  override def onItemUse(stack: ItemStack, playerIn: EntityPlayer, worldIn: World, pos: BlockPos, side: EnumFacing,
     hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    Option(worldIn.getTileEntity(pos)) match {
      case Some(inv) if inv.isInstanceOf[IInventory] =>
        val tag = Option(stack.getTagCompound).getOrElse(new NBTTagCompound)
        tag.writeBlockPos("inventory", pos)
        stack.setTagCompound(tag)
      case _ => ;
    }
    super.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ)
  }

  override def onItemRightClick(itemStackIn: ItemStack, worldIn: World, playerIn: EntityPlayer): ItemStack = {
    if (itemStackIn.hasTagCompound) {
      val pos = itemStackIn.getTagCompound.readBlockPos("inventory")
      println(pos)
    }
    super.onItemRightClick(itemStackIn, worldIn, playerIn)
  }
}
