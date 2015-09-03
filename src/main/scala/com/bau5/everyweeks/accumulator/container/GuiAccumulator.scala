package com.bau5.everyweeks.accumulator.container

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.util.ResourceLocation

/**
 * Created by bau5 on 9/2/2015.
 */
class GuiAccumulator(container: ContainerAccumulator) extends GuiContainer(container) {

  private val texture = new ResourceLocation("textures/gui/container/crafting_table.png")

  override def drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int): Unit = {
    this.fontRendererObj.drawString("Accumulator", 8, 6, 4210752)
    this.fontRendererObj.drawString(I18n.format("container.inventory", new Array[AnyRef](0)), 8, this.ySize - 96 + 2, 4210752)
  }

  override def drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int): Unit = {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F)
    this.mc.getTextureManager.bindTexture(texture)
    val k: Int = (this.width - this.xSize) / 2
    val l: Int = (this.height - this.ySize) / 2
    this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize)
  }
}
