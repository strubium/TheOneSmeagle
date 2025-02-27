package mcjty.theoneprobe.apiimpl.providers;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IIconStyle;
import mcjty.theoneprobe.api.ILayoutStyle;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

import static mcjty.theoneprobe.api.TextStyleClass.*;
import static mcjty.theoneprobe.api.IProbeInfo.STARTLOC;
import static mcjty.theoneprobe.api.IProbeInfo.ENDLOC;

public class HarvestInfoTools {

    private static final ResourceLocation ICONS = new ResourceLocation(TheOneProbe.MODID, "textures/gui/icons.png");

    private static final HashMap<String, ItemStack> testTools = new HashMap<>();
    static {
        testTools.put("theoneprobe.probe.shovel", new ItemStack(Items.WOODEN_SHOVEL));
        testTools.put("theoneprobe.probe.axe", new ItemStack(Items.WOODEN_AXE));
        testTools.put("theoneprobe.probe.pickaxe", new ItemStack(Items.WOODEN_PICKAXE));
    }

    static void showHarvestLevel(IProbeInfo probeInfo, IBlockState blockState, Block block) {
        String harvestTool = block.getHarvestTool(blockState);
        if (harvestTool != null) {
            int harvestLevel = block.getHarvestLevel(blockState);
            String harvestName;

            // Handle out-of-bounds or negative harvest levels by converting to string
            if (harvestLevel < 0 || harvestLevel >= Config.getHarvestLevels().length) {
                harvestName = Integer.toString(harvestLevel);
            } else {
                // Use server-side translation
                harvestName = STARTLOC + Config.getHarvestLevels()[harvestLevel] + ENDLOC;
            }

            // Add text information to the probe
            probeInfo.text(LABEL + STARTLOC + "theoneprobe.probe.tool_indicator" + ENDLOC + " " + INFO + STARTLOC + (harvestTool) + ENDLOC + " (" + STARTLOC + "theoneprobe.probe.level_indicator" + ENDLOC + " " + harvestName + ")");
        }
    }

    static void showCanBeHarvested(IProbeInfo probeInfo, World world, BlockPos pos, Block block, EntityPlayer player) {
        if (ModItems.isProbe(player.getHeldItemMainhand())) {
            return; // No need to show harvestability for the probe itself
        }

        boolean harvestable = block.canHarvestBlock(world, pos, player) && world.getBlockState(pos).getBlockHardness(world, pos) >= 0;
        if (harvestable) {
            probeInfo.text(OK + STARTLOC +"theoneprobe.probe.harvestable_indicator" + ENDLOC);
        } else {
            probeInfo.text(WARNING + STARTLOC + "theoneprobe.probe.not_harvestable_indicator" + ENDLOC);
        }
    }

    static void showHarvestInfo(IProbeInfo probeInfo, World world, BlockPos pos, Block block, IBlockState blockState, EntityPlayer player) {
        boolean harvestable = block.canHarvestBlock(world, pos, player) && world.getBlockState(pos).getBlockHardness(world, pos) >= 0;

        String harvestTool = block.getHarvestTool(blockState);
        String harvestName = null;

        if (harvestTool == null) {
            // The block doesn't have an explicitly-set harvest tool, so we're going to test our wooden tools against the block.
            float blockHardness = blockState.getBlockHardness(world, pos);
            if (blockHardness > 0f) {
                for (Map.Entry<String, ItemStack> testToolEntry : testTools.entrySet()) {
                    ItemStack testTool = testToolEntry.getValue();

                    if (testTool != null && testTool.getItem() instanceof ItemTool) {
                        ItemTool toolItem = (ItemTool) testTool.getItem();
                        if (testTool.getDestroySpeed(blockState) >= toolItem.toolMaterial.getEfficiency()) {
                            // Use server-side translation
                            harvestTool = STARTLOC + testToolEntry.getKey()+ ENDLOC;
                            break;
                        }
                    }
                }
            }
        }

        if (harvestTool != null) {
            int harvestLevel = block.getHarvestLevel(blockState);
            if (harvestLevel < 0) {
                // If harvest level is out of bounds, set the name manually
            } else if (harvestLevel >= Config.getHarvestLevels().length) {
                harvestName = STARTLOC + Config.getHarvestLevels()[Config.getHarvestLevels().length - 1] + ENDLOC;
            } else {
                harvestName = STARTLOC + Config.getHarvestLevels()[harvestLevel] + ENDLOC;
            }
        }

        boolean harvestStyleVanilla = Config.getHarvestStyleVanilla();
        int offs = harvestStyleVanilla ? 16 : 0;
        int dim = harvestStyleVanilla ? 13 : 16;

        ILayoutStyle alignment = probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER);
        IIconStyle iconStyle = probeInfo.defaultIconStyle().width(harvestStyleVanilla ? 18 : 20).height(harvestStyleVanilla ? 14 : 16).textureWidth(32).textureHeight(32);
        IProbeInfo horizontal = probeInfo.horizontal(alignment);
        if (harvestable) {
            horizontal.icon(ICONS, 0, offs, dim, dim, iconStyle)
                    .text(OK + ((harvestTool != null) ? Tools.capitalize(harvestTool) : "{*theoneprobe.probe.notool_indicator*}"));
        } else {
            if (harvestName == null || harvestName.isEmpty()) {
                horizontal.icon(ICONS, 16, offs, dim, dim, iconStyle)
                        .text(WARNING + ((harvestTool != null) ? Tools.capitalize(harvestTool) : "{*theoneprobe.probe.notool_indicator*}"));
            } else {
                horizontal.icon(ICONS, 16, offs, dim, dim, iconStyle)
                        .text(WARNING + ((harvestTool != null) ? Tools.capitalize(harvestTool) :  STARTLOC + "theoneprobe.probe.notool_indicator" + ENDLOC) + " (" + STARTLOC + "theoneprobe.probe.level_indicator" + ENDLOC + " " + harvestName + ")");
            }
        }
    }
}
