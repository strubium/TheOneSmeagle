package mcjty.theoneprobe.apiimpl.providers;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.config.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.UsernameCache;

import java.text.DecimalFormat;
import java.util.UUID;

import static mcjty.theoneprobe.api.TextStyleClass.*;

public class HorseInfoProvider implements IProbeInfoEntityProvider {

    private static final DecimalFormat dfCommas = new DecimalFormat("##.#");

    @Override
    public String getID() {
        return TheOneProbe.MODID + ":entity.horse";
    }

    @Override
    public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {
        if (!(entity instanceof EntityHorse)) {
            return;
        }

        EntityHorse horse = (EntityHorse) entity;
        IProbeConfig config = Config.getRealConfig();

        if (Tools.show(mode, config.getHorseStatSetting())) {
            double jumpStrength = horse.getHorseJumpStrength();
            double jumpHeight = -0.1817584952 * jumpStrength * jumpStrength * jumpStrength + 3.689713992 * jumpStrength * jumpStrength + 2.128599134 * jumpStrength - 0.343930367;
            probeInfo.text(LABEL + "{*theoneprobe.probe.jump_height_indicator*} " + INFO + dfCommas.format(jumpHeight));

            IAttributeInstance speedAttribute = horse.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
            probeInfo.text(LABEL + "{*theoneprobe.probe.speed_indicator*} " + INFO + dfCommas.format(speedAttribute.getAttributeValue()));

            probeInfo.text(LABEL + "Temper " + INFO + dfCommas.format(horse.getTemper()));

        }
    }
}
