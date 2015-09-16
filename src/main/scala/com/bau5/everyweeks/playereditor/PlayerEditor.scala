package com.bau5.everyweeks.playereditor

import net.minecraft.command.{WrongUsageException, CommandBase, ICommandSender}
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.{FMLServerStartingEvent, FMLInitializationEvent}
import net.minecraftforge.fml.common.Mod


@Mod(modid = PlayerEditor.MOD_ID, name = PlayerEditor.MOD_ID,
  version = PlayerEditor.VERSION, modLanguage = "scala")
object PlayerEditor {
  final val MOD_ID = "playereditor"
  final val VERSION = "1.0"
  final val NAME = "Player Editor"

  @Mod.Instance(PlayerEditor.MOD_ID)
  var instance = this

  @EventHandler
  def onServerStart(ev: FMLServerStartingEvent) {
    ev.registerServerCommand(new CommandOpenInventory)
  }
}

class CommandOpenInventory extends CommandBase {
  override def getName: String = "pedit"

  override def getCommandUsage(sender: ICommandSender): String = "pedit <player>"

  override def execute(sender: ICommandSender, args: Array[String]) = args.length match {
    case n if n > 1 =>
      val senderPlayer = CommandBase.getCommandSenderAsPlayer(sender)
      val otherPlayer = CommandBase.func_175768_b(sender, args(1))
      println("Got command! " + args)
      println(otherPlayer)
    case _ => throw new WrongUsageException(getCommandUsage(sender))
  }


}