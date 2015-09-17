package com.bau5.lib

import net.minecraft.client.Minecraft
import net.minecraft.client.resources.model.{ModelBakery, ModelResourceLocation}
import net.minecraft.item.Item

/**
 * Created by bau5 on 9/16/2015.
 */
object ItemModelRegistrar {
  def registerModelsWithMetadata(item: Item, modId: String, renderType: String, specifications: List[(Int, String)]) = {
    val specs = specifications.map { case (meta, resourceLoc) =>
      val spec = ModelSpecification(item, meta, modId, resourceLoc, renderType)
      registerModel(spec)
    }
    val strings = specs.map(_.qualifiedName).distinct
    ModelBakery.addVariantName(item, strings:_*)
  }

  def registerModel(spec: ModelSpecification): ModelSpecification = {
    Minecraft.getMinecraft.getRenderItem.getItemModelMesher.register(
      spec.item, spec.meta,
      new ModelResourceLocation(spec.qualifiedName, spec.renderType))
    spec
  }
}


object ModelSpecification {
  def apply(item: Item, meta: Int, modId: String, resourceLoc: String, renderType: String): ModelSpecification = {
    val str = s"$modId:$resourceLoc"
    new ModelSpecification(item, meta, str, renderType)
  }
}

case class ModelSpecification(item: Item, meta: Int, qualifiedName: String, renderType: String)