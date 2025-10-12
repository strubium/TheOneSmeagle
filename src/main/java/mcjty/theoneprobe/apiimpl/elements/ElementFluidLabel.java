package mcjty.theoneprobe.apiimpl.elements;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.apiimpl.TheOneProbeImp;
import mcjty.theoneprobe.apiimpl.client.ElementTextRender;
import mcjty.theoneprobe.network.NetworkTools;
import mcjty.theoneprobe.setup.proxy.CommonProxy;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ElementFluidLabel implements IElement {

    private final String fluidName;
    private final int amount;
    private String translatedName;

    public ElementFluidLabel(FluidStack fluid) {
        if (fluid == null) {
            this.fluidName = "";
            this.amount = 0;
        } else {
            this.fluidName = fluid.getFluid().getName();
            this.amount = fluid.amount;
        }
    }

    public ElementFluidLabel(ByteBuf buf) {
        this.fluidName = NetworkTools.readStringCompact(buf);
        this.amount = buf.readInt();
        this.translatedName = getTranslatedName();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writeStringCompact(buf, fluidName);
        buf.writeInt(amount);
    }

    @Override
    public void render(int x, int y) {
        if (translatedName != null && !translatedName.isEmpty()) {
            ElementTextRender.render(translatedName, x, y);
        }
    }

    @Override
    public int getWidth() {
        if (translatedName != null && !translatedName.isEmpty()) {
            return ElementTextRender.getWidth(translatedName);
        }

        return 10;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public int getID() {
        return TheOneProbeImp.ELEMENT_FLUIDLABEL;
    }

    @SideOnly(Side.CLIENT)
    private String getTranslatedName() {
        if (fluidName == null || fluidName.isEmpty()) return ""; // Empty Tank

        Fluid fluid = FluidRegistry.getFluid(fluidName);

        if (fluid == null) {
            // This should never happen, but just in case, return the plain fluid name
            CommonProxy.getLogger().error("Could not find fluid with name {}", fluidName);
            return fluidName;
        }

        return fluid.getLocalizedName(new FluidStack(fluid, amount));
    }
}
