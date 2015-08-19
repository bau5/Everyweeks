package com.bau5.everyweeks.shadowstep;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;


/**
 * Created by bau5 on 8/15/15.
 */
@Mod(modid = ShadowStep.MODID, version = ShadowStep.VERSION)
public class ShadowStep {

    public static final String MODID = "shadowstep";
    public static final String VERSION = "1.0";

    public static final Item catalyst = new ItemCatalyst();

    @EventHandler
    public void init(FMLInitializationEvent event) {
        GameRegistry.registerItem(catalyst, "catalyst");
        if (event.getSide() == Side.CLIENT) {
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                    .register(catalyst, 0, new ModelResourceLocation(ShadowStep.MODID + ":catalyst", "inventory"));
        }
    }
}
