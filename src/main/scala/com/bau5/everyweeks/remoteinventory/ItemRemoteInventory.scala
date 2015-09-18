package com.bau5.everyweeks.remoteinventory

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.{ItemStack, Item}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.util.{IChatComponent, EnumFacing, BlockPos}
import net.minecraft.world.World

import com.bau5.lib.RichNBTTagCompound._

/**
 * Created by bau5 on 9/12/15.
 */
class ItemRemoteInventory extends Item {
  this.setMaxStackSize(1)
  this.setCreativeTab(CreativeTabs.tabMisc)
  this.setUnlocalizedName("remoteinv")

  override def onItemUse(stack: ItemStack, playerIn: EntityPlayer, worldIn: World, pos: BlockPos, side: EnumFacing,
     hitX: Float, hitY: Float, hitZ: Float): Boolean = playerIn.isSneaking match {
    case true =>
      Option(worldIn.getTileEntity(pos)) match {
        case Some(inv) if inv.isInstanceOf[IInventory] =>
          val tag = Option(stack.getTagCompound).getOrElse(new NBTTagCompound)
          tag.writeBlockPos("inventory", pos)
          tag.put[Int]("dim", playerIn.dimension)
          stack.setTagCompound(tag)
          println(s"Set ${playerIn.dimension} $pos")
        case _ => ;
      }
      true
    case false =>
      super.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ)
  }

  override def onItemRightClick(itemStackIn: ItemStack, worldIn: World, playerIn: EntityPlayer): ItemStack = {
    if (itemStackIn.hasTagCompound && !worldIn.isRemote) {
      val opt = itemStackIn.getTagCompound.readBlockPos("inventory")
      val dim = Option(itemStackIn.getTagCompound.get[Int]("dim")).getOrElse(0)
      val worldObj = {
        for (world <- MinecraftServer.getServer.worldServers if world.provider.getDimensionId == dim) yield world.asInstanceOf[World]
      }.headOption.getOrElse(worldIn)
      opt.foreach { pos =>
        println(pos)
        Option(worldObj.getTileEntity(pos)) match {
          case Some(te) if te.isInstanceOf[IInventory] =>
            playerIn.openGui(RemoteInventory.instance, 0, worldIn,te.getPos.getX, te.getPos.getY, te.getPos.getZ)
          case _ => ;
        }
      }
    }
    super.onItemRightClick(itemStackIn, worldIn, playerIn)
  }
}

