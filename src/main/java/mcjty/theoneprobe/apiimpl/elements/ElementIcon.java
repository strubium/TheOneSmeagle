package mcjty.theoneprobe.apiimpl.elements;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.api.IIconStyle;
import mcjty.theoneprobe.apiimpl.TheOneProbeImp;
import mcjty.theoneprobe.apiimpl.client.ElementIconRender;
import mcjty.theoneprobe.apiimpl.styles.IconStyle;
import mcjty.theoneprobe.network.NetworkTools;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

public class ElementIcon implements IElement {

    private final ResourceLocation icon;
    private final short u;
    private final short v;
    private final short w;
    private final short h;
    private final IIconStyle style;

    public ElementIcon(ResourceLocation icon, int u, int v, int w, int h, IIconStyle style) {
        this.icon = icon;
        this.u = (short) u;
        this.v = (short) v;
        this.w = (short) w;
        this.h = (short) h;
        this.style = style;
    }

    public ElementIcon(ByteBuf buf) {
        icon = new ResourceLocation(
                Objects.requireNonNull(NetworkTools.readStringCompact(buf)),
                Objects.requireNonNull(NetworkTools.readStringCompact(buf))
        );
        u = buf.readShort();
        v = buf.readShort();
        w = buf.readShort();
        h = buf.readShort();
        style = new IconStyle()
                .width(buf.readShort())
                .height(buf.readShort())
                .textureWidth(buf.readShort())
                .textureHeight(buf.readShort());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(int x, int y) {
        ElementIconRender.render(icon, x, y, w, h, u, v, style.getTextureWidth(), style.getTextureHeight());
    }

    @Override
    public int getWidth() {
        return style.getWidth();
    }

    @Override
    public int getHeight() {
        return style.getHeight();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writeStringCompact(buf, icon.getNamespace());
        NetworkTools.writeStringCompact(buf, icon.getPath());
        buf.writeShort(u);
        buf.writeShort(v);
        buf.writeShort(w);
        buf.writeShort(h);
        buf.writeShort((short) style.getWidth());
        buf.writeShort((short) style.getHeight());
        buf.writeShort((short) style.getTextureWidth());
        buf.writeShort((short) style.getTextureHeight());
    }

    @Override
    public int getID() {
        return TheOneProbeImp.ELEMENT_ICON;
    }
}
