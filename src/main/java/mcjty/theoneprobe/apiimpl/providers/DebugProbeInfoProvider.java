package mcjty.theoneprobe.apiimpl.providers;

import mcjty.lib.api.power.IBigPower;
import mcjty.theoneprobe.compat.RedstoneFluxTools;
import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.LayoutStyle;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

import static mcjty.theoneprobe.api.TextStyleClass.INFO;
import static mcjty.theoneprobe.api.TextStyleClass.LABEL;

public class DebugProbeInfoProvider implements IProbeInfoProvider {

    @Override
    public String getID() {
        return TheOneProbe.MODID + ":debug";
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        if (mode == ProbeMode.DEBUG && Config.showDebugInfo) {
            Block block = blockState.getBlock();
            BlockPos pos = data.getPos();
            showDebugInfo(probeInfo, world, blockState, pos, block, data.getSideHit());
        }
    }

    private void showDebugInfo(IProbeInfo probeInfo, World world, IBlockState blockState, BlockPos pos, Block block, EnumFacing side) {
        String simpleName = block.getClass().getSimpleName();
        IProbeInfo vertical = probeInfo.vertical(new LayoutStyle().borderColor(0xffff4444).spacing(2))
                .text(LABEL + "{*theoneprobe.debug_block.reg_name_indicator*} " + INFO + Objects.requireNonNull(block.getRegistryName()))
                .text(LABEL + "{*theoneprobe.debug_block.unlocalized_name_indicator*} " + INFO + block.getTranslationKey())
                .text(LABEL + "{*theoneprobe.debug_block.meta_indicator*} " + INFO + blockState.getBlock().getMetaFromState(blockState))
                .text(LABEL + "{*theoneprobe.debug_block.class_indicator*} " + INFO + simpleName)
                .text(LABEL + "{*theoneprobe.debug_block.hardness_indicator*} " + INFO + block.getBlockHardness(blockState, world, pos))
                .text(LABEL + "{*theoneprobe.debug_block.power_w_indicator*} " + INFO + block.getWeakPower(blockState, world, pos, side.getOpposite())
                        + LABEL + ", " + "{*theoneprobe.debug_block.power_s_indicator*} " + INFO + block.getStrongPower(blockState, world, pos, side.getOpposite()));

        int lightValue = block.getLightValue(blockState, world, pos);
        if (lightValue > 0) {
            vertical.text(LABEL + "{*theoneprobe.debug_block.light_indicator*} " + INFO + lightValue);
        }

        TileEntity te = world.getTileEntity(pos);
        if (te != null) {
            vertical.text(LABEL + "{*theoneprobe.debug_block.tile_entity_indicator*} " + INFO + te.getClass().getSimpleName());
            if (te instanceof IBigPower) {
                vertical.text(LABEL + "{*theoneprobe.debug_block.energy_indicator*} " + INFO + RedstoneFluxTools.getEnergy(te))
                        .text(LABEL + "{*theoneprobe.debug_block.max_energy_indicator*} " + INFO + RedstoneFluxTools.getMaxEnergy(te));
            }
        }
    }
}
