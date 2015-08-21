package com.bau5.everyweeks.shadowstep;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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

            if(worldIn.isRemote) {
                ItemStack stackInUse = player.getItemInUse();
                if (stackInUse != null && stackInUse.getItem().equals(this)) {
                    MovingObjectPosition mop = getMovingObjectPositionFromPlayerWithDuration(worldIn, player,
                            player.getItemInUseDuration(), false);

                    if (mop != null) {
                        BlockPos pos = mop.getBlockPos();
                        if (lastBlockPos == null || lastBlockPos.compareTo(pos) != 0) {
                            player.playSound("mob.endermen.portal", 0.5F, 0.5F);
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
                }
            } else {
                // Repair only needs to happen on Server, syncs the changes to Client. If both do it
                //client will incorrectly show the repaired amount being twice what it actually is
                boolean isNight = worldIn.getWorldTime() > 13500 && worldIn.getWorldTime() < 23500;
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
        int ticks = getMaxItemUseDuration(stack) - timeLeft;
        double scaled = getScaledDistance(ticks);

        MovingObjectPosition pos = getMovingObjectPositionFromPlayerWithDuration(worldIn, playerIn, ticks, false);
        if (pos != null && pos.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            // find first open space
            BlockPos hit = pos.getBlockPos();
            BlockPos search = hit.up();
            for (int i = 0; i < 5; i++) {
                if (!isValid(search, worldIn)) {
                    search = search.up();
                } else {
                    int damage = (int) Math.ceil(Math.sqrt(playerIn.getDistanceSq(hit)) / maxDistance * damageScalar);
                    if (stack.getItemDamage() + damage < getMaxDamage() || playerIn.capabilities.isCreativeMode) {
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

                        if (playerIn instanceof EntityPlayerMP) {
                            ((EntityPlayerMP)playerIn).playerNetServerHandler
                                    .setPlayerLocation(x + 0.5, y, z + 0.5, playerIn.rotationYaw, playerIn.rotationPitch);
                        } else {
                            playerIn.setPosition(x + 0.5, y, z + 0.5);
                        }

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

    /**
     *   Modified version of super.getMovingObjectPositionFromPlayer. Needed to add the scaled
     * distance, and it's different between client and server, therefore better to do it this way.
     */
    private MovingObjectPosition getMovingObjectPositionFromPlayerWithDuration(World worldIn, EntityPlayer playerIn, int duration, boolean useLiquids) {
        float f = playerIn.prevRotationPitch + (playerIn.rotationPitch - playerIn.prevRotationPitch);
        float f1 = playerIn.prevRotationYaw + (playerIn.rotationYaw - playerIn.prevRotationYaw);
        double d0 = playerIn.prevPosX + (playerIn.posX - playerIn.prevPosX);
        double d1 = playerIn.prevPosY + (playerIn.posY - playerIn.prevPosY) + (double)playerIn.getEyeHeight();
        double d2 = playerIn.prevPosZ + (playerIn.posZ - playerIn.prevPosZ);
        Vec3 vec3 = new Vec3(d0, d1, d2);
        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d3 = getScaledDistance(duration);
        Vec3 vec31 = vec3.addVector((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
        return worldIn.rayTraceBlocks(vec3, vec31, useLiquids, !useLiquids, false);
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
