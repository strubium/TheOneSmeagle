/**
 * This class was created by <TechLord22>. It was distributed as
 * part of the TOPExtras Mod. Get the Source Code in github:
 * https://github.com/TechLord22/TOPExtras
 *
 * TOPExtras is Open Source and distributed under the
 * MIT License: https://github.com/TechLord22/TOPExtras/blob/master/LICENSE
 */
package mcjty.theoneprobe;

import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import mcjty.theoneprobe.apiimpl.styles.ItemStyle;
import mcjty.theoneprobe.apiimpl.styles.LayoutStyle;
import mcjty.theoneprobe.config.Config;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

public class Utilities {

    public static final DecimalFormat FORMAT = new DecimalFormat("#,###.#");

    /**
     * Generates a provider ID for a given name.
     *
     * @param name The name to include in the provider ID.
     * @return The generated provider ID.
     */
    @Nonnull
    public static String getProviderId(@Nonnull String name) {
        return String.format("%s:%s_provider", TheOneProbe.MODID, name);
    }

    /**
     * Adds an ItemStack to a list, merging it with existing stacks if possible.
     *
     * @param stacks The list of {@link ItemStack}s to add to.
     * @param foundItems A set of {@link Item}s already present in the stacks list.
     * @param stack The {@link ItemStack} to add.
     */
    public static void addItemStack(@Nonnull List<ItemStack> stacks, @Nonnull Set<Item> foundItems, @Nonnull ItemStack stack) {
        if (stack.isEmpty()) return;
        if (foundItems.contains(stack.getItem())) {
            for (ItemStack s : stacks) {
                if (ItemHandlerHelper.canItemStacksStack(s, stack)) {
                    s.grow(stack.getCount());
                    return;
                }
            }
        }
        // If we come here we need to append a new stack
        stacks.add(stack.copy());
        foundItems.add(stack.getItem());
    }

    /**
     * Displays the contents of a chest in the IProbeInfo.
     *
     * @param probeInfo The {@link IProbeInfo} to display the chest contents in.
     * @param stacks The list of ItemStacks representing the chest contents.
     * @param mode The current {@link ProbeMode}.
     */
    public static void showChestContents(@Nonnull IProbeInfo probeInfo, @Nonnull List<ItemStack> stacks, @Nonnull ProbeMode mode) {
        IProbeInfo vertical = probeInfo.vertical(probeInfo.defaultLayoutStyle().borderColor(Config.chestContentsBorderColor).spacing(0));
        int rows = 0;
        int idx = 0;

        if (Tools.show(mode, Config.getRealConfig().getShowChestContentsDetailed()) && stacks.size() <= Config.showItemDetailThresshold) {
            for (ItemStack stackInSlot : stacks) {
                vertical.horizontal(new LayoutStyle().spacing(10).alignment(ElementAlignment.ALIGN_CENTER))
                        .item(stackInSlot, new ItemStyle().width(16).height(16))
                        .text(TextStyleClass.INFO + stackInSlot.getDisplayName());
            }
        } else {
            IProbeInfo horizontal = null;
            for (ItemStack stackInSlot : stacks) {
                if (idx % 10 == 0) {
                    horizontal = vertical.horizontal(new LayoutStyle().spacing(0));
                    rows++;
                    if (rows > 4) break;
                }
                horizontal.item(stackInSlot);
                idx++;
            }
        }
    }
}

