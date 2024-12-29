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
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

import static mcjty.theoneprobe.api.TextStyleClass.*;

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
                harvestName = I18n.translateToLocal(Config.getHarvestLevels()[harvestLevel]);
            }

            // Add text information to the probe
            probeInfo.text(LABEL + I18n.translateToLocal("theoneprobe.probe.tool_indicator") + " " + INFO + I18n.translateToLocal(harvestTool) + " (" + I18n.translateToLocal("theoneprobe.probe.level_indicator") + " " + harvestName + ")");
        }
    }

    static void showCanBeHarvested(IProbeInfo probeInfo, World world, BlockPos pos, Block block, EntityPlayer player) {
        if (ModItems.isProbe(player.getHeldItemMainhand())) {
            return; // No need to show harvestability for the probe itself
        }

        boolean harvestable = block.canHarvestBlock(world, pos, player) && world.getBlockState(pos).getBlockHardness(world, pos) >= 0;
        if (harvestable) {
            probeInfo.text(OK + I18n.translateToLocal("theoneprobe.probe.harvestable_indicator"));
        } else {
            probeInfo.text(WARNING + I18n.translateToLocal("theoneprobe.probe.not_harvestable_indicator"));
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
                            harvestTool = I18n.translateToLocal(testToolEntry.getKey());
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
                harvestName = I18n.translateToLocal(Config.getHarvestLevels()[Config.getHarvestLevels().length - 1]);
            } else {
                harvestName = I18n.translateToLocal(Config.getHarvestLevels()[harvestLevel]);
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
                    .text(OK + ((harvestTool != null) ? harvestTool : "{*theoneprobe.probe.notool_indicator*}"));
        } else {
            if (harvestName == null || harvestName.isEmpty()) {
                horizontal.icon(ICONS, 16, offs, dim, dim, iconStyle)
                        .text(WARNING + ((harvestTool != null) ? harvestTool : "{*theoneprobe.probe.notool_indicator*}"));
            } else {
                horizontal.icon(ICONS, 16, offs, dim, dim, iconStyle)
                        .text(WARNING + ((harvestTool != null) ? harvestTool : I18n.translateToLocal("theoneprobe.probe.notool_indicator")) + " (" + I18n.translateToLocal("theoneprobe.probe.level_indicator") + " " + harvestName + ")");
            }
        }
    }
}
