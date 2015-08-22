package com.bau5.everyweeks.siphon

import com.google.common.base.Predicate
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.gui.IUpdatePlayerListBox
import net.minecraft.tileentity.TileEntity
import scala.collection.JavaConverters._


/**
 * Created by bau5 on 8/22/15.
 */
class TileEntitySiphon extends TileEntity with IUpdatePlayerListBox {

  var sleepTick = 0
  val maxSleep = 20 // 1 second

  override def update() {
    sleepTick += 1
    if (sleepTick > maxSleep) {
      sleepTick = 0
      val players = scan()
      println("After scan:")
      players foreach println
    }
  }

  def scan(): List[EntityPlayer] = {
    worldObj.getPlayers(classOf[EntityPlayer], new Predicate[EntityPlayer] {
      override def apply(p: EntityPlayer) = p.getDistance(getPos.getX, getPos.getY, getPos.getZ) < 10
    }).asScala.toList.map(_.asInstanceOf[EntityPlayer])
  }
}
