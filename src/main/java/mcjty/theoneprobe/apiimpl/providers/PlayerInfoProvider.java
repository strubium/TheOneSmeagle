package mcjty.theoneprobe.apiimpl.providers;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import static mcjty.theoneprobe.api.TextStyleClass.*;

public class PlayerInfoProvider implements IProbeInfoEntityProvider {

    @Override
    public String getID() {
        return TheOneProbe.MODID + ":entity.player";
    }

    @Override
    public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {
        if (!(entity instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer targetPlayer = (EntityPlayer) entity;

        probeInfo.text(LABEL + "{*theoneprobe.probe.experience_indicator*} " + INFO + targetPlayer.experienceLevel);


        // Display player's inventory status
        int inventorySize = targetPlayer.inventory.getSizeInventory();
        int filledSlots = 0;
        for (int i = 0; i < inventorySize; i++) {
            if (!targetPlayer.inventory.getStackInSlot(i).isEmpty()) {
                filledSlots++;
            }
        }
        probeInfo.text(LABEL + "{*theoneprobe.probe.inventory_indicator*} " + INFO + filledSlots + "/" + inventorySize);

    }
}
