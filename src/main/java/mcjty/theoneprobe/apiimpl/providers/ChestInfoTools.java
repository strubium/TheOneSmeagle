package mcjty.theoneprobe.apiimpl.providers;

import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.Utilities;
import mcjty.theoneprobe.api.IProbeConfig;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.config.Config;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.*;

public class ChestInfoTools {

    static void showChestInfo(ProbeMode mode, IProbeInfo probeInfo, World world, BlockPos pos, IProbeConfig config) {
        List<ItemStack> stacks = null;
        IProbeConfig.ConfigMode chestMode = config.getShowChestContents();

        // Determine the chestMode based on configuration and world state
        if (chestMode == IProbeConfig.ConfigMode.EXTENDED
                && (Config.showSmallChestContentsWithoutSneaking > 0 || !Config.getInventoriesToShow().isEmpty())) {
            if (Config.getInventoriesToShow().contains(world.getBlockState(pos).getBlock().getRegistryName())) {
                chestMode = IProbeConfig.ConfigMode.NORMAL;
            } else if (Config.showSmallChestContentsWithoutSneaking > 0) {
                stacks = new ArrayList<>();
                int slots = getChestContents(world, pos, stacks);
                if (slots <= Config.showSmallChestContentsWithoutSneaking) {
                    chestMode = IProbeConfig.ConfigMode.NORMAL;
                }
            }
        } else if (chestMode == IProbeConfig.ConfigMode.NORMAL && !Config.getInventoriesToNotShow().isEmpty()) {
            if (Config.getInventoriesToNotShow().contains(world.getBlockState(pos).getBlock().getRegistryName())) {
                chestMode = IProbeConfig.ConfigMode.EXTENDED;
            }
        }

        // Check if chest contents should be shown based on the mode and configuration
        if (Tools.show(mode, chestMode)) {
            if (stacks == null) {
                stacks = new ArrayList<>();
                getChestContents(world, pos, stacks);
            }

            if (!stacks.isEmpty()) {
                // Call the updated showChestContents method with mode, probeInfo, and stacks
               Utilities.showChestContents(probeInfo, stacks, mode);
            }
        }
    }

    private static void addItemStack(List<ItemStack> stacks, Set<Item> foundItems, @Nonnull ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (foundItems != null && foundItems.contains(stack.getItem())) {
            for (ItemStack s : stacks) {
                if (ItemHandlerHelper.canItemStacksStack(s, stack)) {
                    s.grow(stack.getCount());
                    return;
                }
            }
        }
        // If we come here we need to append a new stack
        stacks.add(stack.copy());
        if (foundItems != null) {
            foundItems.add(stack.getItem());
        }
    }

    private static int getChestContents(World world, BlockPos pos, List<ItemStack> stacks) {
        TileEntity te = world.getTileEntity(pos);

        Set<Item> foundItems = Config.compactEqualStacks ? new HashSet<>() : null;
        int maxSlots = 0;
        try {
            if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
                IItemHandler capability = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                maxSlots = Objects.requireNonNull(capability).getSlots();
                for (int i = 0; i < maxSlots; i++) {
                    addItemStack(stacks, foundItems, capability.getStackInSlot(i));
                }
            } else if (te instanceof IInventory) {
                IInventory inventory = (IInventory) te;
                maxSlots = inventory.getSizeInventory();
                for (int i = 0; i < maxSlots; i++) {
                    addItemStack(stacks, foundItems, inventory.getStackInSlot(i));
                }
            }
        } catch(RuntimeException e) {
            throw new RuntimeException("{*theoneprobe.probe.get_contents*} " + world.getBlockState(pos).getBlock().getRegistryName() + " (" + te.getClass().getName() + ")", e);
        }
        return maxSlots;
    }
}
