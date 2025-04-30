package mcjty.theoneprobe.apiimpl.providers;

import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.Utilities;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import lombok.NonNull;

public class JukeboxProvider implements IProbeInfoProvider {

    @Override
    public String getID() {
        return Utilities.getProviderId("jukebox");
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, @NonNull IBlockState blockState, IProbeHitData data) {
        if (blockState.getBlock() instanceof BlockJukebox) {
            TileEntity tileEntity = world.getTileEntity(data.getPos());
            if (tileEntity instanceof BlockJukebox.TileEntityJukebox) {
                BlockJukebox.TileEntityJukebox jukebox = (BlockJukebox.TileEntityJukebox) tileEntity;

                ItemStack record = jukebox.getRecord();
                if (record.isEmpty()) {
                    probeInfo.text(TextStyleClass.WARNING + "{*theoneprobe.probe.empty_indicator*}");
                    return;
                }

                IProbeInfo horizontalPane = probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER));
                horizontalPane.item(record);
                String recordName = (record.getItem() instanceof ItemRecord) ? ((ItemRecord) record.getItem()).getRecordNameLocal() : record.getDisplayName();
                horizontalPane.text(TextStyleClass.INFO + "{*theoneprobe.probe.playing_indicator*} " + I18n.format(recordName));
            }
        }
    }
}
