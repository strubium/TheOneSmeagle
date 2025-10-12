/**
 * This class was created by <TechLord22>. It was distributed as
 * part of the TOPExtras Mod. Get the Source Code in github:
 * https://github.com/TechLord22/TOPExtras
 *
 * TOPExtras is Open Source and distributed under the
 * MIT License: https://github.com/TechLord22/TOPExtras/blob/master/LICENSE
 */
package mcjty.theoneprobe.apiimpl.providers;

import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.Utilities;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import lombok.NonNull;

public class EnchantingPowerInfoProvider implements IProbeInfoProvider {

    @Override
    public String getID() {
        return Utilities.getProviderId("enchanting_power");
    }

    @Override
    public void addProbeInfo(ProbeMode mode, @NonNull IProbeInfo probeInfo, EntityPlayer player, @NonNull World world, @NonNull IBlockState blockState, @NonNull IProbeHitData data) {
        Block block = blockState.getBlock();
        BlockPos pos = data.getPos();

        // ===============================
        // CASE 1: Looking at Enchantment Table
        // ===============================
        if (block.hasTileEntity(blockState) && world.getTileEntity(pos) instanceof TileEntityEnchantmentTable) {
            float enchantingPower = 0.0F;
            BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && z == 0) continue;

                    // Air gaps around table
                    if (world.isAirBlock(checkPos.setPos(pos.getX() + z, pos.getY(), pos.getZ() + x))
                            && world.isAirBlock(checkPos.setPos(pos.getX() + z, pos.getY() + 1, pos.getZ() + x))) {

                        enchantingPower += ForgeHooks.getEnchantPower(world, checkPos.setPos(pos.getX() + z * 2, pos.getY(), pos.getZ() + x * 2));
                        enchantingPower += ForgeHooks.getEnchantPower(world, checkPos.setPos(pos.getX() + z * 2, pos.getY() + 1, pos.getZ() + x * 2));

                        if (x != 0 && z != 0) {
                            enchantingPower += ForgeHooks.getEnchantPower(world, checkPos.setPos(pos.getX() + z * 2, pos.getY(), pos.getZ() + x));
                            enchantingPower += ForgeHooks.getEnchantPower(world, checkPos.setPos(pos.getX() + z * 2, pos.getY() + 1, pos.getZ() + x));
                            enchantingPower += ForgeHooks.getEnchantPower(world, checkPos.setPos(pos.getX() + z, pos.getY(), pos.getZ() + x * 2));
                            enchantingPower += ForgeHooks.getEnchantPower(world, checkPos.setPos(pos.getX() + z, pos.getY() + 1, pos.getZ() + x * 2));
                        }
                    }
                }
            }

            if (enchantingPower > 0.0F) {
                probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                        .item(new ItemStack(Items.ENCHANTED_BOOK), probeInfo.defaultItemStyle().width(16).height(16))
                        .text(TextStyleClass.LABEL + "{*theoneprobe.probe.enchanting_power_indicator*} "
                                + TextFormatting.LIGHT_PURPLE + Utilities.FORMAT.format(enchantingPower));
            }
            return;
        }

        // ===============================
        // CASE 2: Looking at a Bookshelf block directly
        // ===============================
        if (block == Blocks.BOOKSHELF) {
            // ForgeHooks.getEnchantPower works here too
            float power = ForgeHooks.getEnchantPower(world, pos);

            if (power > 0.0F) {
                probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                        .item(new ItemStack(Items.ENCHANTED_BOOK), probeInfo.defaultItemStyle().width(16).height(16))
                        .text(TextStyleClass.LABEL + "{*theoneprobe.probe.enchanting_power_indicator*} "
                                + TextFormatting.LIGHT_PURPLE + Utilities.FORMAT.format(power));
            }
        }
    }
}
