package mcjty.theoneprobe.compat;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import mcjty.theoneprobe.probe.ProbeBase;
import net.minecraft.item.ItemStack;

/**
 * Represents the Probe Goggles item, which implements {@link IBauble} for compatibility with baubles.
 */
public class ProbeGoggles extends ProbeBase implements IBauble {

    public ProbeGoggles() {
        super("probe_goggles");
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.HEAD;
    }

}
