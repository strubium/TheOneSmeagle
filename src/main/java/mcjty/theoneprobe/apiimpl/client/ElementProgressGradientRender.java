package mcjty.theoneprobe.apiimpl.client;

import mcjty.theoneprobe.ClientTools;
import mcjty.theoneprobe.api.IProgressStyle;
import mcjty.theoneprobe.apiimpl.elements.ElementProgress;
import mcjty.theoneprobe.rendering.RenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ElementProgressGradientRender extends ElementProgressRender {

    public static void render(IProgressStyle style, long current, long max, int x, int y, int w, int h) {
        if (style.isLifeBar()) {
            renderLifeBar(current, x, y, w, h);
        } else if (style.isArmorBar()) {
            renderArmorBar(current, x, y, w, h);
        } else {
            RenderHelper.drawThickBeveledBox(x, y, x + w, y + h, 1, style.getBorderColor(), style.getBorderColor(), style.getBackgroundColor());
            if (current > 0 && max > 0) {
                // Determine the progress bar width, but limit it to the size of the element (minus 2).
                int dx = (int) Math.min((current * (w - 2) / max), w - 2);

                if (dx > 0) {
                    drawGradientBar(x + 1, y + 1, x + dx + 1, y + h - 1, style.getAlternatefilledColor(), style.getFilledColor());
                }
            }
        }

        if (style.isShowText()) {
            RenderHelper.renderText(ClientTools.mc, x + 3, y + 2, style.getPrefix() + ElementProgress.format(current, style.getNumberFormat(), style.getSuffix()));
        }
    }

    private static void drawGradientBar(int startX, int startY, int endX, int endY, int startColor, int endColor) {
        // Save the current OpenGL state
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.shadeModel(7425); // GL_SMOOTH

        // Start drawing a quad
        net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.getInstance();
        net.minecraft.client.renderer.BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_COLOR); // GL_QUADS

        // Convert colors to RGBA components
        float startAlpha = (startColor >> 24 & 255) / 255.0F;
        float startRed = (startColor >> 16 & 255) / 255.0F;
        float startGreen = (startColor >> 8 & 255) / 255.0F;
        float startBlue = (startColor & 255) / 255.0F;

        float endAlpha = (endColor >> 24 & 255) / 255.0F;
        float endRed = (endColor >> 16 & 255) / 255.0F;
        float endGreen = (endColor >> 8 & 255) / 255.0F;
        float endBlue = (endColor & 255) / 255.0F;

        // Define the gradient vertices
        buffer.pos(startX, endY, 0).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.pos(endX, endY, 0).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        buffer.pos(endX, startY, 0).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        buffer.pos(startX, startY, 0).color(startRed, startGreen, startBlue, startAlpha).endVertex();

        // Finish rendering
        tessellator.draw();

        // Restore the OpenGL state
        GlStateManager.shadeModel(7424); // GL_FLAT
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }
}
