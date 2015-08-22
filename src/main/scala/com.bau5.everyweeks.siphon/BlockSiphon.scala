package com.bau5.everyweeks.siphon

import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World


/**
 * Created by bau5 on 8/22/15.
 */
class BlockSiphon extends BlockContainer(Material.rock) {
  setCreativeTab(CreativeTabs.tabDecorations)
  setUnlocalizedName("siphon")

  override def createNewTileEntity(worldIn: World, meta: Int): TileEntity = meta match {
    case 0 => new TileEntitySiphon
  }
}
