package com.bau5.everyweeks.shadowstep;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.World;


/**
 * Created by bau5 on 8/15/15.
 */
public class ItemCatalyst extends Item {

    public ItemCatalyst() {
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof EntityPlayer && worldIn.isRemote) {
            EntityPlayer player = ((EntityPlayer) entityIn);
            ItemStack inUse = player.getItemInUse();
            if (inUse != null && inUse.getItem().equals(this)) {
                MovingObjectPosition mop = getLocationLookingAt(worldIn, player, 10.0D);
                if (mop != null) {
                    BlockPos pos = mop.getBlockPos();
                    double x = 0;
                    double y = 1.5D;
                    double z = 0;
                    double xo = 0;
                    double yo = -2.0D;
                    double zo = 0;
                    switch (mop.sideHit) {
                        case SOUTH:
                            z += 1.0D;
                        case NORTH:
                            x += itemRand.nextDouble();
                            break;
                        case EAST:
                            x += 1.0D;
                        case WEST:
                            z += itemRand.nextDouble();
                            break;
                        case DOWN:
                            y -= 1.0D;
                            x += itemRand.nextDouble();
                            z += itemRand.nextDouble();
                            break;
                        case UP:
                            y += 0.5D;
                            x += itemRand.nextDouble();
                            z += itemRand.nextDouble();
                            break;
                    }
                    worldIn.spawnParticle(EnumParticleTypes.PORTAL, pos.getX() + x, (double) pos.getY() + y, pos.getZ() + z, xo, yo, zo);
                }
            }
        }
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer playerIn, int timeLeft) {
        MovingObjectPosition pos = getLocationLookingAt(worldIn, playerIn, 10.0D);
        if (pos != null && pos.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            // find first open space
            BlockPos hit = pos.getBlockPos();
            BlockPos search = hit.add(0, 1, 0);
            for (int i = 0; i < 5; i++) {
                if (!isValid(search, worldIn)) {
                    search = search.add(0, 1, 0);
                } else {
                    BlockPos playerPos = playerIn.getPosition();
                    // spawn particles before stepping
                    for (int j = 0; j < 30; j++) {
                        worldIn.spawnParticle(EnumParticleTypes.PORTAL, playerPos.getX() + itemRand.nextDouble(),
                                playerPos.getY() + itemRand.nextDouble() * 2, playerPos.getZ() + itemRand.nextDouble(),
                                itemRand.nextDouble() - 0.5D, 0.0D, itemRand.nextDouble() - 0.5D);
                    }
                    double x = search.getX();
                    double y = search.getY();
                    double z = search.getZ();
                    // add resistance if falling, will negate some damage
                    if (playerIn.motionY < -0.5D) {
                        playerIn.addPotionEffect(new PotionEffect(Potion.absorption.id, 10, 4, false, false));
                    }
                    // move player, play sound, spawn particles
                    playerIn.setPositionAndUpdate(x + 0.5, y, z + 0.5);
                    worldIn.playSoundEffect(x, y + 0.5, z, "mob.endermen.portal", 1.0F, 1.0F);
                    for (int j = 0; j < 30; j++) {
                        worldIn.spawnParticle(EnumParticleTypes.PORTAL, x + 0.5D, y, z + 0.5D,
                                itemRand.nextDouble() - 0.5D, itemRand.nextDouble(), itemRand.nextDouble() - 0.5D);
                    }
                    break;
                }
            }
        }

        super.onPlayerStoppedUsing(stack, worldIn, playerIn, timeLeft);
    }

    private boolean isValid(BlockPos position, World world) {
        return world.isAirBlock(position) && world.isAirBlock(position.add(0, 1, 0));
    }

    public MovingObjectPosition getLocationLookingAt(World world, EntityPlayer player, double distance) {
        Vec3 eyes = player.getPositionEyes(1.0F);
        Vec3 look = player.getLook(1.0F);
        Vec3 end = eyes.addVector(look.xCoord * distance, look.yCoord * distance, look.zCoord * distance);
        return world.rayTraceBlocks(eyes, end, true, true, false);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        return itemStackIn;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }
}
