package mcjty.theoneprobe.apiimpl.providers;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.LayoutStyle;
import mcjty.theoneprobe.config.ConfigSetup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.UUID;

import static mcjty.theoneprobe.api.TextStyleClass.INFO;
import static mcjty.theoneprobe.api.TextStyleClass.LABEL;

public class DebugProbeInfoEntityProvider implements IProbeInfoEntityProvider {

    @Override
    public String getID() {
        return TheOneProbe.MODID + ":entity.debug";
    }

    @Override
    public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {
        if (mode == ProbeMode.DEBUG && ConfigSetup.showDebugInfo) {
            IProbeInfo vertical = null;
            if (entity instanceof EntityLivingBase) {
                vertical = probeInfo.vertical(new LayoutStyle().borderColor(0xffff4444).spacing(2));

                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                float stepHeight = entityLivingBase.stepHeight;
                int totalArmorValue = entityLivingBase.getTotalArmorValue();
                int age = entityLivingBase.getIdleTime();
                float absorptionAmount = entityLivingBase.getAbsorptionAmount();
                float aiMoveSpeed = entityLivingBase.getAIMoveSpeed();
                int revengeTimer = entityLivingBase.getRevengeTimer();
                boolean isOnFire = entityLivingBase.isBurning();
                double posX = entityLivingBase.posX;
                double posY = entityLivingBase.posY;
                double posZ = entityLivingBase.posZ;
                double motionX = entityLivingBase.motionX;
                double motionY = entityLivingBase.motionY;
                double motionZ = entityLivingBase.motionZ;
                float health = entityLivingBase.getHealth();
                float maxHealth = entityLivingBase.getMaxHealth();
                Collection<PotionEffect> activeEffects = entityLivingBase.getActivePotionEffects();
                UUID uuid = entityLivingBase.getUniqueID();
                String entityName = entityLivingBase.getCustomNameTag();

                vertical
                        .text(LABEL + "Step Height: " + INFO + stepHeight)
                        .text(LABEL + "Total Armor: " + INFO + totalArmorValue)
                        .text(LABEL + "Age: " + INFO + age)
                        .text(LABEL + "Absorption: " + INFO + absorptionAmount)
                        .text(LABEL + "AI Move Speed: " + INFO + aiMoveSpeed)
                        .text(LABEL + "Revenge Timer: " + INFO + revengeTimer)
                        .text(LABEL + "On Fire: " + INFO + isOnFire)
                        .text(LABEL + "Position: " + INFO + String.format("X: %.2f, Y: %.2f, Z: %.2f", posX, posY, posZ))
                        .text(LABEL + "Motion: " + INFO + String.format("X: %.2f, Y: %.2f, Z: %.2f", motionX, motionY, motionZ))
                        .text(LABEL + "{*theoneprobe.probe.health_indicator*} " + INFO + health + " / " + maxHealth);

                if (ConfigSetup.showDebugUUID) {
                    vertical.text(LABEL + "UUID: " + INFO + uuid);
                }
                if (entityLivingBase.hasCustomName()) {
                    vertical.text(LABEL + "Custom Name: " + INFO + entityName);
                }
                if (!activeEffects.isEmpty()) {
                    vertical.text(LABEL + "Active Effects: ");
                    for (PotionEffect effect : activeEffects) {
                        vertical.text(INFO + effect.getEffectName() + " (" + effect.getAmplifier() + ") - " + effect.getDuration() + " ticks");
                    }
                }
            }


            if (entity instanceof EntityLiving) {
                EntityLiving entityLiving = (EntityLiving) entity;
                boolean isLeftHanded = entityLiving.isLeftHanded();
                int maxFallHeight = entityLiving.getMaxFallHeight();
                int maxInChunk = entityLiving.getMaxSpawnedInChunk();
                vertical
                        .text(LABEL + "Is Left Handed: " + INFO + isLeftHanded)
                        .text(LABEL + "Max Fall Height: " + INFO + maxFallHeight)
                        .text(LABEL + "Max Spawnable in Chunk: " + INFO + maxInChunk);
            }

            if (entity instanceof EntityAgeable) {
                EntityAgeable entityAgeable = (EntityAgeable) entity;
                int growingAge = entityAgeable.getGrowingAge();
                vertical
                        .text(LABEL + "Growing Age: " + INFO + growingAge);
            }

            if (entity instanceof EntityWaterMob) {
                EntityWaterMob entityWaterMob = (EntityWaterMob) entity;
                boolean canBreatheUnderwater = entityWaterMob.canBreatheUnderwater();
                vertical
                        .text(LABEL + "Can Breathe Underwater: " + INFO + canBreatheUnderwater)
                        .text(LABEL + "In Water: " + INFO + entityWaterMob.isInWater());
            }

            if (entity instanceof EntityPlayer) {
                EntityPlayer entityPlayer = (EntityPlayer) entity;
                int foodLevel = entityPlayer.getFoodStats().getFoodLevel();
                float saturationLevel = entityPlayer.getFoodStats().getSaturationLevel();
                float luck = entityPlayer.getLuck();
                BlockPos bedLocation = entityPlayer.getBedLocation();

                vertical
                        .text(LABEL + "Food Level: " + INFO + foodLevel)
                        .text(LABEL + "Saturation Level: " + INFO + saturationLevel)
                        .text(LABEL + "Luck: " + INFO + luck)
                        .text(LABEL + "Bed Location: "+ INFO + bedLocation);
            }
        }
    }
}

