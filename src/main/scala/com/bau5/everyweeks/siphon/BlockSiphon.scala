package com.bau5.everyweeks.siphon

import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.{InventoryBasic, InventoryHelper}
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{EnumFacing, BlockPos}
import net.minecraft.world.World


/**
 * Created by bau5 on 8/22/15.
 */
class BlockSiphon extends BlockContainer(Material.rock) {
  setCreativeTab(CreativeTabs.tabDecorations)
  setUnlocalizedName("siphon")


  override def onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer,
    side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {

    val siphon = worldIn.getTileEntity(pos).asInstanceOf[TileEntitySiphon]
    if (playerIn.isSneaking) {
      siphon.connectedInventory = null
    } else {
      val is = playerIn.getHeldItem
      if (is != null) {
        siphon.attuneTo(is)
      }
    }
    super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY, hitZ)
  }


  override def breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
    InventoryHelper.dropInventoryItems(worldIn, pos, {
      val inv = new InventoryBasic("internal", false, 1)
      inv.setInventorySlotContents(0,
        worldIn.getTileEntity(pos).asInstanceOf[TileEntitySiphon]
          .held
      )
      inv
    })
  }

  override def createNewTileEntity(worldIn: World, meta: Int): TileEntity = meta match {
    case 0 => new TileEntitySiphon
  }
}
