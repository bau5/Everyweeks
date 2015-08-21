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

    private final double damageScalar = 5.0D;
    private final double maxDistance = 25.0D;
    private final int maxScalar = 50;

    private int lastInUseDuration = 0;
    private int repairCounter = 0;
    private BlockPos lastBlockPos = null;

    public ItemCatalyst() {
        this.setUnlocalizedName(ShadowStep.MODID + "_catalyst");
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setMaxDamage(100);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof EntityPlayer) {
            EntityPlayer player = ((EntityPlayer) entityIn);

            ItemStack stackInUse = player.getItemInUse();
            if (stackInUse != null && stackInUse.getItem().equals(this)) {
                lastInUseDuration = player.getItemInUseDuration();

                MovingObjectPosition mop = getLocationLookingAt(worldIn, player,
                        getScaledDistance(lastInUseDuration));
                if (mop != null) {
                    BlockPos pos = mop.getBlockPos();
                    if (lastBlockPos == null || lastBlockPos.compareTo(pos) != 0) {
                        player.playSound("mob.endermen.portal", 1.0F, 0.5F);
                    }

                    lastBlockPos = pos;
                    double x = 0;
                    double y = 1.5D;
                    double z = 0;
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
                    worldIn.spawnParticle(EnumParticleTypes.PORTAL, pos.getX() + x, (double) pos.getY() + y,
                            pos.getZ() + z, 0.0D, -2.0D, 0.0D);
                }
            } else {
                boolean isNight = worldIn.getWorldTime() > 135000 && worldIn.getWorldTime() < 24000;
                if (stack.isItemDamaged() && isNight) {
                    repairCounter++;
                    if (repairCounter > 225) {
                        stack.damageItem(-1, player);
                        repairCounter = 0;
                    }
                }
            }
        }
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer playerIn, int timeLeft) {
        double scaled = getScaledDistance(lastInUseDuration);
        MovingObjectPosition pos = getLocationLookingAt(worldIn, playerIn,
                scaled);
        if (pos != null && pos.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            // find first open space
            BlockPos hit = pos.getBlockPos();
            BlockPos search = hit.up();
            for (int i = 0; i < 5; i++) {
                if (!isValid(search, worldIn)) {
                    search = search.up();
                } else {
                    int damage = (int) Math.ceil((scaled / maxDistance) * damageScalar);
                    if (stack.getItemDamage() + damage < getMaxDamage()) {
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
                        worldIn.playSoundEffect(playerIn.posX, playerIn.posY + 0.5, playerIn.posZ, "mob.endermen.portal",
                                1.0F, 1.0F);
                        playerIn.setPositionAndUpdate(x + 0.5, y, z + 0.5);

                        worldIn.playSoundEffect(x, y + 0.5, z, "mob.endermen.portal", 1.0F, 1.0F);
                        for (int j = 0; j < 30; j++) {
                            worldIn.spawnParticle(EnumParticleTypes.PORTAL, x + 0.5D, y, z + 0.5D,
                                    itemRand.nextDouble() - 0.5D, itemRand.nextDouble(), itemRand.nextDouble() - 0.5D);
                        }
                        // damage item based on how far it was used to step
                        stack.damageItem(damage, playerIn);
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
        Vec3 look = player.getLookVec();
        Vec3 end = eyes.addVector(look.xCoord * distance, look.yCoord * distance, look.zCoord * distance);
        return world.rayTraceBlocks(eyes, end, true, true, false);
    }

    /**
     * Get the scaled distance from how long the player has been using the item.
     * Uses a separate "max" duration. getMaxDuration() is how long user can use the item,
     * we'll leave this high so they can hold it for a long time. But we want to quickly
     * scale up to the max distance, so we have a cap on that scalar.
     *
     * @param itemInUseDuration how long the player has been using the item (non normalized)
     * @return scaled distance
     */
    private double getScaledDistance(double itemInUseDuration) {
        // normalize the time in use, and bring it up to a minimum just in case
        double durationInUse = Math.min(maxScalar, Math.max(itemInUseDuration, 10));
        return ((durationInUse / maxScalar) * maxDistance);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        return itemStackIn;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
