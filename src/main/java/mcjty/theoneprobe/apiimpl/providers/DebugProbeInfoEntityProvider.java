package mcjty.theoneprobe.apiimpl.providers;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.LayoutStyle;
import mcjty.theoneprobe.config.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
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
        if (mode == ProbeMode.DEBUG && Config.showDebugInfo) {
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
                        .text(LABEL + "{*theoneprobe.debug_probe.step_height_indicator*} " + INFO + stepHeight)
                        .text(LABEL + "{*theoneprobe.debug_probe.total_armor_indicator*} " + INFO + totalArmorValue)
                        .text(LABEL + "{*theoneprobe.debug_probe.age_indicator*} " + INFO + age)
                        .text(LABEL + "{*theoneprobe.debug_probe.absorption_indicator*} " + INFO + absorptionAmount)
                        .text(LABEL + "{*theoneprobe.debug_probe.ai_move_speed_indicator*} " + INFO + aiMoveSpeed)
                        .text(LABEL + "{*theoneprobe.debug_probe.revenge_timer_indicator*} " + INFO + revengeTimer)
                        .text(LABEL + "{*theoneprobe.debug_probe.on_fire_indicator*} " + INFO + isOnFire)
                        .text(LABEL + "{*theoneprobe.debug_probe.position_indicator*} " + INFO + String.format("X: %.2f, Y: %.2f, Z: %.2f", posX, posY, posZ))
                        .text(LABEL + "{*theoneprobe.debug_probe.motion_indicator*} " + INFO + String.format("X: %.2f, Y: %.2f, Z: %.2f", motionX, motionY, motionZ))
                        .text(LABEL + "{*theoneprobe.probe.health_indicator*} " + INFO + health + " / " + maxHealth);

                if (Config.showDebugUUID) {
                    vertical.text(LABEL + "{*theoneprobe.debug_probe.uuid_indicator*} " + INFO + uuid);
                }
                if (entityLivingBase.hasCustomName()) {
                    vertical.text(LABEL + "{*theoneprobe.debug_probe.custom_name_indicator*} " + INFO + entityName);
                }
                if (!activeEffects.isEmpty()) {
                    vertical.text(LABEL + "{*theoneprobe.debug_probe.active_effects_indicator*} ");
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
                        .text(LABEL + "{*theoneprobe.debug_probe.is_left_handed_indicator*} " + INFO + isLeftHanded)
                        .text(LABEL + "{*theoneprobe.debug_probe.max_fall_height_indicator*} " + INFO + maxFallHeight)
                        .text(LABEL + "{*theoneprobe.debug_probe.max_spawnable_in_chunk_indicator*} " + INFO + maxInChunk);
            }

            if (entity instanceof EntityAgeable) {
                EntityAgeable entityAgeable = (EntityAgeable) entity;
                int growingAge = entityAgeable.getGrowingAge();
                vertical
                        .text(LABEL + "{*theoneprobe.debug_probe.growing_age_indicator*} " + INFO + growingAge);
            }

            if (entity instanceof EntityWaterMob) {
                EntityWaterMob entityWaterMob = (EntityWaterMob) entity;
                boolean canBreatheUnderwater = entityWaterMob.canBreatheUnderwater();
                vertical
                        .text(LABEL + "{*theoneprobe.debug_probe.can_breathe_underwater_indicator*} " + INFO + canBreatheUnderwater)
                        .text(LABEL + "{*theoneprobe.debug_probe.in_water_indicator*} " + INFO + entityWaterMob.isInWater());
            }
            if (entity instanceof EntityChicken) {
                EntityChicken entityChicken = (EntityChicken) entity;
                vertical
                        .text(LABEL + "{*theoneprobe.debug_probe.next_egg_in_indicator*} " + INFO + entityChicken.timeUntilNextEgg);
            }

            if (entity instanceof EntityPlayer) {
                EntityPlayer entityPlayer = (EntityPlayer) entity;
                int foodLevel = entityPlayer.getFoodStats().getFoodLevel();
                float saturationLevel = entityPlayer.getFoodStats().getSaturationLevel();
                float luck = entityPlayer.getLuck();
                BlockPos bedLocation = entityPlayer.getBedLocation();

                vertical
                        .text(LABEL + "{*theoneprobe.debug_probe.food_level_indicator*} " + INFO + foodLevel)
                        .text(LABEL + "{*theoneprobe.debug_probe.saturation_level_indicator*} " + INFO + saturationLevel)
                        .text(LABEL + "{*theoneprobe.debug_probe.luck_indicator*} " + INFO + luck)
                        .text(LABEL + "{*theoneprobe.debug_probe.bed_location_indicator*} " + INFO + bedLocation);
            }
        }
    }
}

