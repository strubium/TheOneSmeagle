package mcjty.theoneprobe.apiimpl.providers;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.Tools;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import static mcjty.theoneprobe.api.IProbeInfo.*;
import static mcjty.theoneprobe.api.TextStyleClass.*;

public class VillagerProbeInfoProvider implements IProbeInfoEntityProvider {

    @Override
    public String getID() {
        return TheOneProbe.MODID + ":entity.villager";
    }

    @Override
    public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, net.minecraft.entity.Entity entity, IProbeHitEntityData data) {
        if (!(entity instanceof EntityVillager)) {
            return;
        }

        EntityVillager villager = (EntityVillager) entity;

        // Show profession
        String professionName = Tools.capitalize(villager.getProfessionForge().getRegistryName().getPath());
        probeInfo.text(LABEL + STARTLOC + "theoneprobe.probe.profession_indicator" + ENDLOC + ": "  + INFO + professionName);
    }
}

