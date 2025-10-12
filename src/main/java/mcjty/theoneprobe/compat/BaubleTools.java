package mcjty.theoneprobe.compat;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import mcjty.theoneprobe.items.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Utility class for handling operations related to baubles.
 */
public class BaubleTools {

    /**
     * Checks if the given player is wearing Probe Goggles.
     *
     * @param player The {@link EntityPlayer} to check.
     * @return True if the player is wearing Probe Goggles, false otherwise.
     */
    public static boolean hasProbeGoggle(EntityPlayer player) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        if (handler == null) {
            return false;
        }
        ItemStack stackInSlot = handler.getStackInSlot(4);
        return !stackInSlot.isEmpty() && stackInSlot.getItem() == ModItems.probeGoggles;
    }

    /**
     * Initializes and returns a new instance of Probe Goggles.
     *
     * @return A new {@link ProbeGoggles} instance.
     */
    public static Item initProbeGoggle() {
        return new ProbeGoggles();
    }

}
