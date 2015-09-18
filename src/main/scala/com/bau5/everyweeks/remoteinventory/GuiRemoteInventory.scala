package com.bau5.everyweeks.remoteinventory

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

/**
 * Created by bau5 on 9/17/2015.
 */
class GuiRemoteInventory(container: ContainerRemoteInventory) extends GuiContainer(container) {
  val inventory = container.inventory
  val numRows = container.totalRows
  ySize = 112 + numRows * 18

  val texture = new ResourceLocation("textures/gui/container/generic_54.png");

  override def drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
    this.fontRendererObj.drawString(inventory.getDisplayName.getUnformattedText, 8, 6, 4210752)
    this.fontRendererObj.drawString(container.player.inventory.getDisplayName.getUnformattedText, 8, this.ySize - 96 + 2, 4210752)
  }

  override def drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int): Unit = {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F)
    this.mc.getTextureManager.bindTexture(texture)
    val k: Int = (this.width - this.xSize) / 2
    val l: Int = (this.height - this.ySize) / 2
    this.drawTexturedModalRect(k, l, 0, 0, xSize, numRows * 18 + 17)
    this.drawTexturedModalRect(k, l + numRows * 18 + 17, 0, 126, this.xSize, 96)
  }
}
