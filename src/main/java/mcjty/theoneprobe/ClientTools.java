package mcjty.theoneprobe;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientTools {

    /**
     * Constant for {@link Minecraft#getMinecraft()}
     */
    public static final Minecraft mc = Minecraft.getMinecraft();
}
