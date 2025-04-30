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
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
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
        // Only proceed if the block has a TileEntity and is an enchantment table
        if (!blockState.getBlock().hasTileEntity(blockState) || !(world.getTileEntity(data.getPos()) instanceof TileEntityEnchantmentTable)) {
            return;
        }

        float enchantingPower = 0.0F;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        // Calculate enchanting power based on surrounding blocks
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue; // Skip the center block

                // Set position to current check (air blocks around the enchantment table)
                if (world.isAirBlock(pos.setPos(data.getPos().add(z, 0, x))) && world.isAirBlock(pos.setPos(data.getPos().add(z, 1, x)))) {

                    // Using MutableBlockPos#setPos to avoid unnecessary object creation
                    enchantingPower += ForgeHooks.getEnchantPower(world, pos.setPos(data.getPos().add(z * 2, 0, x * 2)));
                    enchantingPower += ForgeHooks.getEnchantPower(world, pos.setPos(data.getPos().add(z * 2, 1, x * 2)));

                    if (x != 0 && z != 0) {
                        enchantingPower += ForgeHooks.getEnchantPower(world, pos.setPos(data.getPos().add(z * 2, 0, x)));
                        enchantingPower += ForgeHooks.getEnchantPower(world, pos.setPos(data.getPos().add(z * 2, 1, x)));
                        enchantingPower += ForgeHooks.getEnchantPower(world, pos.setPos(data.getPos().add(z, 0, x * 2)));
                        enchantingPower += ForgeHooks.getEnchantPower(world, pos.setPos(data.getPos().add(z, 1, x * 2)));
                    }
                }
            }
        }
        if (enchantingPower > 0.0F) {
            probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                    .item(new ItemStack(Items.ENCHANTED_BOOK), probeInfo.defaultItemStyle().width(16).height(16))
                    .text(TextStyleClass.LABEL + "{*theoneprobe.probe.enchanting_power_indicator*} " + TextFormatting.LIGHT_PURPLE + Utilities.FORMAT.format(enchantingPower));
        }
    }
}
