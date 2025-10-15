package mcjty.theoneprobe.apiimpl.client;

import mcjty.theoneprobe.ClientTools;
import mcjty.theoneprobe.rendering.RenderHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ElementTextRender {

    public static void render(String text, int x, int y) {
        RenderHelper.renderText(ClientTools.MC, x, y, ClientTools.stylifyString(text));
    }

    public static int getWidth(String text) {
        return ClientTools.MC.fontRenderer.getStringWidth(ClientTools.stylifyString(text));
    }
}
