package com.bau5.lib

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.BlockPos
import com.bau5.lib.RichNBTTagCompound._

/**
 * Created by bau5 on 9/12/15.
 */

object RichNBTTagCompound {

  implicit def toRichNBT(nbt: NBTTagCompound): RichNBTTagCompound = new RichNBTTagCompound(nbt)

  implicit val intReader: Reader[Int] = new Reader[Int] {
    override def read(nbt: NBTTagCompound, str: String): Int = nbt.getInteger(str)
  }

  implicit val intWriter: Writer[Int] = new Writer[Int] {
    override def write(nbt: NBTTagCompound, str: String, value: Int) = nbt.setInteger(str, value)
  }
}

class RichNBTTagCompound(tag: NBTTagCompound) {
  def readBlockPos(name: String): Option[BlockPos] = {
    Option(tag.getCompoundTag(name)).map { posTag =>
      new BlockPos(posTag.get[Int]("x"), posTag.get[Int]("y"), posTag.get[Int]("z"))
    }
  }

  def writeBlockPos(name: String, pos: BlockPos): NBTTagCompound = {
    val posTag = new NBTTagCompound
    posTag.put[Int]("x", pos.getX)
    posTag.put[Int]("y", pos.getY)
    posTag.put[Int]("z", pos.getZ)
    tag.setTag(name, posTag)
    this.tag
  }

  def get[T](name: String)(implicit reader: Reader[T]): T = reader.read(tag, name)
  def put[T](name: String, value: T)(implicit writer: Writer[T]) = writer.write(tag, name, value)
}

trait Reader[A] {
  def read(nbt: NBTTagCompound, str: String): A
}

trait Writer[A] {
  def write(nbt: NBTTagCompound, str: String, value: A)
}