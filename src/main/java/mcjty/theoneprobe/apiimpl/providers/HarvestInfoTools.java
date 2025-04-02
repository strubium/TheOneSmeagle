package mcjty.theoneprobe.apiimpl.providers;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IIconStyle;
import mcjty.theoneprobe.api.ILayoutStyle;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Objects;

import static mcjty.theoneprobe.api.TextStyleClass.*;
import static mcjty.theoneprobe.api.IProbeInfo.STARTLOC;
import static mcjty.theoneprobe.api.IProbeInfo.ENDLOC;

public class HarvestInfoTools {

    private static final ResourceLocation ICONS = new ResourceLocation(TheOneProbe.MODID, "textures/gui/icons.png");

    /**
     * Combines the functionality of showing harvest tool, harvest level, and harvestability.
     */
    static void showHarvestInfo(IProbeInfo probeInfo, World world, BlockPos pos, Block block, IBlockState blockState, EntityPlayer player) {
        if (ModItems.isProbe(player.getHeldItemMainhand())) {
            return; // Skip probe tool
        }

        boolean harvestable = canBlockBeHarvested(block, world, pos, player);
        String harvestTool = getHarvestTool(block, blockState, world, pos);
        String harvestLevelName = getHarvestLevelName(block, blockState);

        boolean harvestStyleVanilla = Config.getHarvestStyleVanilla();
        int offs = harvestStyleVanilla ? 16 : 0;
        int dim = harvestStyleVanilla ? 13 : 16;

        ILayoutStyle alignment = probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER);
        IIconStyle iconStyle = probeInfo.defaultIconStyle()
                .width(harvestStyleVanilla ? 18 : 20)
                .height(harvestStyleVanilla ? 14 : 16)
                .textureWidth(32)
                .textureHeight(32);

        IProbeInfo horizontal = probeInfo.horizontal(alignment);

        if (harvestable) {
            horizontal.icon(ICONS, 0, offs, dim, dim, iconStyle)
                    .text(OK + formatToolInfo(harvestTool));
        } else {
            String text = WARNING + formatToolInfo(harvestTool);
            if (harvestLevelName != null) {
                text += " (" + STARTLOC + "theoneprobe.probe.level_indicator" + ENDLOC + " " + STARTLOC + harvestLevelName + ENDLOC + ")";
            }
            else {
                text += "";
            }

            horizontal.icon(ICONS, 16, offs, dim, dim, iconStyle).text(text);
        }
    }

    private static boolean canBlockBeHarvested(Block block, World world, BlockPos pos, EntityPlayer player) {
        return block.canHarvestBlock(world, pos, player)
                && world.getBlockState(pos).getBlockHardness(world, pos) >= 0;
    }

    private static String getHarvestTool(Block block, IBlockState blockState, World world, BlockPos pos) {
        String tool = convertToTranslationKey(block, blockState);

        if (tool == null) {
            float hardness = blockState.getBlockHardness(world, pos);
            if (hardness > 0f) {
                for (Map.Entry<String, ItemStack> entry : Config.getHarvestToolTests().entrySet()) {
                    ItemStack testTool = entry.getValue();
                    if (testTool != null && testTool.getItem() instanceof ItemTool) {
                        ItemTool toolItem = (ItemTool) testTool.getItem();
                        if (testTool.getDestroySpeed(blockState) >= toolItem.toolMaterial.getEfficiency()) {
                            return entry.getKey();
                        }
                    }
                }
            }
        }

        return tool;
    }

    private static String getHarvestLevelName(Block block, IBlockState blockState) {
        int harvestLevel = block.getHarvestLevel(blockState);

        if (harvestLevel < 0) {
            return null;
        } else if (harvestLevel >= Config.getHarvestLevels().length) {
            return Config.getHarvestLevels()[Config.getHarvestLevels().length - 1];
        } else {
            return Config.getHarvestLevels()[harvestLevel];
        }
    }

    private static String formatToolInfo(String harvestTool) {
        if (harvestTool != null) {
            return STARTLOC + harvestTool + ENDLOC;
        }
        return STARTLOC + "theoneprobe.probe.notool_indicator" + ENDLOC;
    }

    private static String convertToTranslationKey(Block block, IBlockState blockState) {
        String harvestTool = block.getHarvestTool(blockState);

        String key = Config.getHarvestToolTranslationKeys().get(harvestTool);
        if (key == null) return "";
        return key;
    }

    /**
     * Separate helpers for optional simple text display (if you want to call them separately)
     */
    static void showHarvestLevel(IProbeInfo probeInfo, IBlockState blockState, Block block) {
        String harvestTool = convertToTranslationKey(block, blockState);
        if (harvestTool != null) {
            String harvestLevelName = getHarvestLevelName(block, blockState);

            if(harvestLevelName != null) {
                probeInfo.text(LABEL + STARTLOC + "theoneprobe.probe.tool_indicator" + ENDLOC + " " +
                        INFO + STARTLOC + harvestTool + ENDLOC + " (" +
                        STARTLOC + "theoneprobe.probe.level_indicator" + ENDLOC + " " +  STARTLOC + harvestLevelName + ENDLOC + ")");
            }
            else {
                probeInfo.text(LABEL + STARTLOC + "theoneprobe.probe.tool_indicator" + ENDLOC + " " +
                        INFO + STARTLOC + harvestTool + ENDLOC);
            }


        }
    }

    static void showCanBeHarvested(IProbeInfo probeInfo, World world, BlockPos pos, Block block, EntityPlayer player) {
        if (ModItems.isProbe(player.getHeldItemMainhand())) {
            return;
        }

        if (canBlockBeHarvested(block, world, pos, player)) {
            probeInfo.text(OK + STARTLOC + "theoneprobe.probe.harvestable_indicator" + ENDLOC);
        } else {
            probeInfo.text(WARNING + STARTLOC + "theoneprobe.probe.not_harvestable_indicator" + ENDLOC);
        }
    }
}
