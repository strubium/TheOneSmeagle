package mcjty.theoneprobe.apiimpl;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.IOverlayRenderer;
import mcjty.theoneprobe.api.IOverlayStyle;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.apiimpl.styles.DefaultOverlayStyle;
import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.rendering.OverlayRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DefaultOverlayRenderer implements IOverlayRenderer {

    @Override
    public IOverlayStyle createDefaultStyle() {
        return ((DefaultOverlayStyle) Config.getDefaultOverlayStyle()).copy();
    }

    @Override
    public IProbeInfo createProbeInfo() {
        return TheOneProbe.theOneProbeImp.create();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(IOverlayStyle style, IProbeInfo probeInfo) {
        OverlayRenderer.renderOverlay(style, probeInfo);
    }
}
