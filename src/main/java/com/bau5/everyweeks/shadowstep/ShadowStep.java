package com.bau5.everyweeks.shadowstep;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
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
        addToChestLoot(catalyst);
    }

    private void addToChestLoot(Item item) {
        WeightedRandomChestContent content = new WeightedRandomChestContent(new ItemStack(item, 1, 0), 1, 1, 5);
        ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, content);
        ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_LIBRARY, content);
        ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_CORRIDOR, content);
        ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_CROSSING, content);
        ChestGenHooks.addItem(ChestGenHooks.PYRAMID_DESERT_CHEST, content);
        ChestGenHooks.addItem(ChestGenHooks.PYRAMID_JUNGLE_CHEST, content);
        ChestGenHooks.addItem(ChestGenHooks.PYRAMID_JUNGLE_DISPENSER, content);
        ChestGenHooks.addItem(ChestGenHooks.NETHER_FORTRESS, content);
        ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(
                new ItemStack(item, 1, 0), 1, 1, 1
        ));
    }
}
