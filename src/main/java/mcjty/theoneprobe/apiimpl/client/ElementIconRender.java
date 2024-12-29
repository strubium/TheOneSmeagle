package mcjty.theoneprobe.apiimpl.client;

import mcjty.theoneprobe.ClientTools;
import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.rendering.RenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ElementIconRender {

    public static void render(ResourceLocation icon, int x, int y, int w, int h, int u, int v, int txtw, int txth) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (icon == null) {
            return;
        }

        if (u == -1) {
            TextureAtlasSprite sprite = ClientTools.mc.getTextureMapBlocks().getAtlasSprite(icon.toString());
            ClientTools.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            RenderHelper.drawTexturedModalRect(x, y, sprite, w, h);
        } else {
            ClientTools.mc.getTextureManager().bindTexture(icon);
            RenderHelper.drawTexturedModalRect(x, y, u, v, w, h, txtw, txth);
        }
    }
}
