package com.bau5.everyweeks.shadowstep;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;


@Mod(modid = ShadowStep.MODID, version = ShadowStep.VERSION)
public class ShadowStep {

    public static final String MODID = "shadowstep";
    public static final String VERSION = "1.0";

    public static final net.minecraft.item.Item teleItem = new ItemCatalyst();

    @EventHandler
    public void init(FMLInitializationEvent event) {
        GameRegistry.registerItem(teleItem, "tele_item");
    }
}
