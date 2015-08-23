package com.bau5.everyweeks.siphon

import com.google.common.base.Predicate
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.play.server.S35PacketUpdateTileEntity
import net.minecraft.network.{NetworkManager, Packet}
import net.minecraft.server.gui.IUpdatePlayerListBox
import net.minecraft.tileentity.TileEntity
import scala.collection.JavaConverters._
import scala.util.Try


/**
 * Created by bau5 on 8/22/15.
 */
class   TileEntitySiphon extends TileEntity with IUpdatePlayerListBox {

  val maxSleep = 20 // 1 second
  var sleepTick = 0

  var seekStack: ItemStack = null
  var held: ItemStack = null

  var connectedInventory: IInventory = null

  def attuneTo(stack: ItemStack) {
    if (!stack.isItemEqual(seekStack)) {
      seekStack = stack.copy()
      seekStack.stackSize = 1
    } else {
      seekStack = null
    }
  }

  def handleConnectedInventory(player: EntityPlayer) {
    if (player.isSneaking && connectedInventory == null) {
      val pos = player.getPosition.down()
      val inv = worldObj.getTileEntity(pos)
      if (inv != null && inv.isInstanceOf[IInventory]) {
        connectedInventory = inv.asInstanceOf[IInventory]
      }
    }
    if (connectedInventory != null && held != null) {
      val inv = for (i <- 0 until connectedInventory.getSizeInventory) yield (i, connectedInventory.getStackInSlot(i))
      val matched = inv.find(e => held.isItemEqual(e._2) && e._2.stackSize < connectedInventory.getInventoryStackLimit)
      matched match {
        case Some((_, stack)) =>
          stack.stackSize += 1
          held.stackSize -= 1

          if (held.stackSize == 0) {
            held = null
          }
          syncToClient()
        case None =>
          inv.find(_._2 == null).foreach { t =>
            connectedInventory.setInventorySlotContents(t._1, {
              val stack = held.copy()
              stack.stackSize = 1
              stack
            })
            held.stackSize -= 1
          }

          if (held.stackSize == 0) {
            held = null
          }
          syncToClient()
      }

    }
  }

  override def update() {
    sleepTick += 1
    if (sleepTick > maxSleep) {
      sleepTick = 0
      val players = scan().sortBy(_._1)
      players.headOption.foreach { case (_, player) =>
        handleConnectedInventory(player)

        val inv = player.inventory.mainInventory
        val items = for (i <- 0 until inv.length if inv(i) != null) yield (i, inv(i))
        items.find(_._2.isItemEqual(seekStack)).foreach { case (index, is) =>
          if (held == null) {
            held = is.copy()
            held.stackSize = 0
          }

          if (is.isItemEqual(held)) {
            held.stackSize += 1
            if (!worldObj.isRemote) {
              is.stackSize -= 1
              syncToClient()
            }
            if (is.stackSize == 0) {
              player.inventory.setInventorySlotContents(index, null)
            }
          }
        }
      }
    }
  }

  def scan(): List[(Double, EntityPlayer)] = {
    worldObj.getPlayers(classOf[EntityPlayer], new Predicate[EntityPlayer] {
      override def apply(p: EntityPlayer) = p.getDistance(getPos.getX, getPos.getY, getPos.getZ) < 10
    }).asScala.toList.map { e =>
      val player = e.asInstanceOf[EntityPlayer]
      (player.getDistance(pos.getX, pos.getY, pos.getZ), player)
    }
  }

  def syncToClient() = worldObj.markBlockForUpdate(pos)

  override def writeToNBT(compound: NBTTagCompound) {
    val tag = new NBTTagCompound
    if (held != null) {
      tag.setTag("held", held.writeToNBT(new NBTTagCompound))
    }
    if (seekStack != null) {
      tag.setTag("seek", seekStack.writeToNBT(new NBTTagCompound))
    }
    compound.setTag("siphon", tag);
    super.writeToNBT(compound)
  }


  override def readFromNBT(compound: NBTTagCompound) {
    if (compound.hasKey("siphon")) {
      val tag = compound.getCompoundTag("siphon")
      held = Try {
        ItemStack.loadItemStackFromNBT(tag.getCompoundTag("held"))
      }.recover { case (ex) => null }.get
      seekStack = Try {
        ItemStack.loadItemStackFromNBT(tag.getCompoundTag("seek"))
      }.recover { case (ex) => null }.get
    }
    super.readFromNBT(compound)
  }


  override def onDataPacket(net: NetworkManager, pkt: S35PacketUpdateTileEntity) {
    readFromNBT(pkt.getNbtCompound)
  }

  override def getDescriptionPacket: Packet = {
    val tag = new NBTTagCompound
    writeToNBT(tag)
    new S35PacketUpdateTileEntity(pos, 0, tag)
  }
}
