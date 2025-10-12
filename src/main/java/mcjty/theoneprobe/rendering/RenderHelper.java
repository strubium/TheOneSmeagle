package mcjty.theoneprobe.rendering;

import mcjty.theoneprobe.ClientTools;
import mcjty.theoneprobe.network.ThrowableIdentity;
import mcjty.theoneprobe.setup.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

/**
 * Helper class for rendering operations.
 *
 * @since 5/16/2016
 * @author McJty
 */
public class RenderHelper {
    public static float rot = 0.0f;

    /**
     * Renders an entity at the specified screen coordinates with a given scale.
     *
     * @param entity The {@link Entity} to be rendered.
     * @param xPos The x-coordinate on the screen where the entity should be rendered.
     * @param yPos The y-coordinate on the screen where the entity should be rendered.
     * @param scale The scale factor for rendering the entity.
     */
    public static void renderEntity(Entity entity, int xPos, int yPos, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.color(1f, 1f, 1f);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(xPos + 8, yPos + 24, 50F);
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(135F, 0.0F, 1.0F, 0.0F);
        net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(rot, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
        entity.rotationPitch = 0.0F;
        GlStateManager.translate(0.0F, (float) entity.getYOffset() + (entity instanceof EntityHanging ? 0.5F : 0.0F), 0.0F);
        ClientTools.mc.getRenderManager().playerViewY = 180F;
        try {
            ClientTools.mc.getRenderManager().renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        } catch (Exception e) {
            CommonProxy.getLogger().error("Error rendering entity!", e);
        }
        GlStateManager.popMatrix();
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

        GlStateManager.disableRescaleNormal();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
        GlStateManager.enableDepth();
        GlStateManager.disableColorMaterial();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    /**
     * Renders an item stack at the specified coordinates, including its count if greater than one.
     *
     * This method checks the count of the item stack and, based on the count, calls {@link #renderItemStack(Minecraft, RenderItem, ItemStack, int, int, String, boolean)}
     * with the appropriate text to display.
     *
     * @param mc The {@link Minecraft} instance.
     * @param itemRender The {@link RenderItem} instance used to render the item.
     * @param itm The {@link ItemStack} to be rendered.
     * @param xo The x-coordinate where the item stack should be rendered.
     * @param yo The y-coordinate where the item stack should be rendered.
     * @param highlight A boolean indicating whether to highlight the item stack.
     * @return true if the item stack was rendered successfully, false otherwise.
     */
    public static boolean renderItemStackWithCount(Minecraft mc, RenderItem itemRender, ItemStack itm, int xo, int yo, boolean highlight) {
        if (itm.getCount() <= 1) {
            return renderItemStack(mc, itemRender, itm, xo, yo, "", highlight);
        } else {
            return renderItemStack(mc, itemRender, itm, xo, yo, "" + itm.getCount(), highlight);
        }
    }

    public static boolean renderItemStack(Minecraft mc, RenderItem itemRender, ItemStack itm, int x, int y, String txt, boolean highlight) {
        GlStateManager.color(1F, 1F, 1F);

        boolean rc = false;
        if (highlight) {
            GlStateManager.disableLighting();
            drawGradientRect(x, y, x + 16, y + 16, 0x80ffffff, 0xffffffff, true);
        }
        if (!itm.isEmpty()) {
            rc = true;
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 32.0F);
            GlStateManager.color(1F, 1F, 1F, 1F);
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableLighting();
            short short1 = 240;
            short short2 = 240;
            net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1, short2);
            itemRender.renderItemAndEffectIntoGUI(itm, x, y);
            itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, itm, x, y, txt);
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableLighting();
        }

        return rc;
    }

    /**
     * Draws a rectangle with a gradient between the specified colors.
     *
     * @param x1      The x-coordinate of the first corner of the rectangle.
     * @param y1      The y-coordinate of the first corner of the rectangle.
     * @param x2      The x-coordinate of the opposite corner of the rectangle (not included).
     * @param y2      The y-coordinate of the opposite corner of the rectangle (not included).
     * @param color1  The starting color of the gradient (ARGB format).
     * @param color2  The ending color of the gradient (ARGB format).
     * @param isVertical A boolean indicating whether the gradient should be vertical (true) or horizontal (false).
     */
    public static void drawGradientRect(int x1, int y1, int x2, int y2, int color1, int color2, boolean isVertical) {
        float zLevel = 0.0f;

        // Extract color components
        float a1 = (color1 >> 24 & 255) / 255.0F;
        float r1 = (color1 >> 16 & 255) / 255.0F;
        float g1 = (color1 >> 8 & 255) / 255.0F;
        float b1 = (color1 & 255) / 255.0F;
        float a2 = (color2 >> 24 & 255) / 255.0F;
        float r2 = (color2 >> 16 & 255) / 255.0F;
        float g2 = (color2 >> 8 & 255) / 255.0F;
        float b2 = (color2 & 255) / 255.0F;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        if (isVertical) {
            // Vertical gradient
            buffer.pos(x2, y1, zLevel).color(r1, g1, b1, a1).endVertex();
            buffer.pos(x1, y1, zLevel).color(r1, g1, b1, a1).endVertex();
            buffer.pos(x1, y2, zLevel).color(r2, g2, b2, a2).endVertex();
            buffer.pos(x2, y2, zLevel).color(r2, g2, b2, a2).endVertex();
        } else {
            // Horizontal gradient
            buffer.pos(x1, y1, zLevel).color(r1, g1, b1, a1).endVertex();
            buffer.pos(x1, y2, zLevel).color(r1, g1, b1, a1).endVertex();
            buffer.pos(x2, y2, zLevel).color(r2, g2, b2, a2).endVertex();
            buffer.pos(x2, y1, zLevel).color(r2, g2, b2, a2).endVertex();
        }

        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    /**
     * Draws a vertical line on the screen.
     *
     * @deprecated This method is deprecated. Use {@link #drawLine(int, int, int, int, int, int, String)} with
     * "vertical" orientation instead.
     */
    @Deprecated
    public static void drawVerticalLine(int x1, int y1, int y2, int color) {
        Gui.drawRect(x1, y1, x1 + 1, y2, color);
    }

    /**
     * Draws a horizontal line on the screen.
     *
     * @deprecated This method is deprecated. Use {@link #drawLine(int, int, int, int, int, int, String)} with
     * "horizontal" orientation instead.
     */
    @Deprecated
    public static void drawHorizontalLine(int x1, int y1, int x2, int color) {
        Gui.drawRect(x1, y1, x2, y1 + 1, color);
    }

    /**
     * Draws a line on the screen in the specified orientation.
     *
     * @param x1 The starting x-coordinate of the line.
     * @param y1 The starting y-coordinate of the line (y-coordinate for horizontal, x-coordinate for vertical).
     * @param x2 The ending x-coordinate of the line (not used for vertical lines).
     * @param y2 The ending y-coordinate of the line (not used for horizontal lines).
     * @param color The color of the line.
     * @param thickness The thickness of the line.
     * @param orientation The orientation of the line (horizontal or vertical).
     */
    public static void drawLine(int x1, int y1, int x2, int y2, int color, int thickness, String orientation) {
        switch (orientation.toLowerCase()) {
            case "vertical":
                Gui.drawRect(x1, y1, x1 + thickness, y2, color); // Fixed x1 and used y2 for ending
                break; // Added break statement

            case "horizontal":
                Gui.drawRect(x1, y1, x2, y1 + thickness, color); // Fixed y1 and used y2 for ending
                break; // Added break statement

            default:
                throw new IllegalArgumentException("Invalid orientation: " + orientation);
        }
    }

    /**
     * Draws a small triangle at specified coordinates.
     *
     * @param x The x-coordinate of the reference point of the triangle.
     * @param y The y-coordinate of the reference point of the triangle.
     * @param color The color of the triangle.
     * @param direction The direction of the triangle: "left", "right", "up", "down".
     */
    public static void drawTriangle(int x, int y, int color, String direction) {
        switch (direction.toLowerCase()) {
            case "left":
                drawLine(x, y, x + 2, y, color, 1, "vertical"); // Base of the triangle
                drawLine(x + 1, y - 1, x + 1, y + 1, color, 1, "vertical"); // Left side
                drawLine(x + 2, y - 2, x + 2, y + 2, color, 1, "vertical"); // Right side
                break;
            case "right":
                drawLine(x, y, x - 2, y, color, 1, "vertical"); // Base of the triangle
                drawLine(x - 1, y - 1, x - 1, y + 1, color, 1, "vertical"); // Right side
                drawLine(x - 2, y - 2, x - 2, y + 2, color, 1,"vertical"); // Left side
                break;
            case "up":
                drawLine(x, y, x, y + 2, color, 1, "horizontal"); // Base of the triangle
                drawLine(x - 1, y + 1, x + 1, y + 1, color, 1, "horizontal"); // Left side
                drawLine(x - 2, y + 2, x + 2, y + 2, color, 1, "horizontal"); // Right side
                break;
            case "down":
                drawLine(x, y, x, y - 2, color, 1, "horizontal"); // Base of the triangle
                drawLine(x - 1, y - 1, x + 1, y - 1, color, 1, "horizontal"); // Left side
                drawLine(x - 2, y - 2, x + 2, y - 2, color, 1, "horizontal"); // Right side
                break;
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }


    /**
     * Draw a button box. x2 and y2 are not included.
     */
    public static void drawFlatButtonBox(int x1, int y1, int x2, int y2, int bright, int average, int dark) {
        drawBeveledBox(x1, y1, x2, y2, bright, dark, average);
    }

    /**
     * Draw a button box. x2 and y2 are not included.
     */
    public static void drawFlatButtonBoxGradient(int x1, int y1, int x2, int y2, int bright, int average1, int average2, int dark) {
        drawGradientRect(x1 + 1, y1 + 1, x2 - 1, y2 - 1, average2, average1, true);
        drawLine(x1, y1, x2 - 1, y1, bright, 1, "horizontal"); // Top edge
        drawLine(x1, y1, x1, y2 - 1, bright, 1, "vertical"); // Left edge
        drawLine(x2 - 1, y1, x2 - 1, y2 - 1, dark, 1, "vertical"); // Right edge
        drawLine(x1, y2 - 1, x2, y2 - 1, dark, 1, "horizontal"); // Bottom edge
    }

    /**
     * Draw a beveled box. x2 and y2 are not included.
     */
    public static void drawBeveledBox(int x1, int y1, int x2, int y2, int topleftcolor, int botrightcolor, int fillcolor) {
        if (fillcolor != -1) {
            Gui.drawRect(x1 + 1, y1 + 1, x2 - 1, y2 - 1, fillcolor);
        }
        drawLine(x1, y1, x2 - 1, y1, topleftcolor, 1, "horizontal"); // Top edge
        drawLine(x1, y1, x1, y2 - 1, topleftcolor, 1, "vertical"); // Left edge
        drawLine(x2 - 1, y1, x2 - 1, y2 - 1, botrightcolor, 1, "vertical"); // Right edge
        drawLine(x1, y2 - 1, x2, y2 - 1, botrightcolor, 1, "horizontal"); // Bottom edge
    }

    /**
     * Draw a thick beveled box. x2 and y2 are not included.
     */
    public static void drawThickBeveledBox(int x1, int y1, int x2, int y2, int thickness, int topleftcolor, int botrightcolor, int fillcolor) {
        if (fillcolor != -1) {
            Gui.drawRect(x1 + 1, y1 + 1, x2 - 1, y2 - 1, fillcolor);
        }
        Gui.drawRect(x1, y1, x2 - 1, y1 + thickness, topleftcolor);
        Gui.drawRect(x1, y1, x1 + thickness, y2 - 1, topleftcolor);
        Gui.drawRect(x2 - thickness, y1, x2, y2 - 1, botrightcolor);
        Gui.drawRect(x1, y2 - thickness, x2, y2, botrightcolor);
    }

    /**
     * Draws a textured rectangle at the stored z-value.
     *
     * @param x       The x-coordinate of the rectangle's position.
     * @param y       The y-coordinate of the rectangle's position.
     * @param u       The x-coordinate of the texture's position.
     * @param v       The y-coordinate of the texture's position.
     * @param width   The width of the rectangle to draw.
     * @param height  The height of the rectangle to draw.
     * @param twidth  The width of the texture.
     * @param theight The height of the texture.
     */
    public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, int twidth, int theight) {
        float zLevel = 0.01f;
        float f = (1.0f/twidth);
        float f1 = (1.0f/theight);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        buffer.pos((x), (y + height), zLevel).tex(((u) * f), ((v + height) * f1)).endVertex();
        buffer.pos((x + width), (y + height), zLevel).tex(((u + width) * f), ((v + height) * f1)).endVertex();
        buffer.pos((x + width), (y), zLevel).tex(((u + width) * f), ((v) * f1)).endVertex();
        buffer.pos((x), (y), zLevel).tex(((u) * f), ((v) * f1)).endVertex();
        tessellator.draw();
    }

    /**
     * Draws a textured rectangle at the stored z-value.
     *
     * @param x      The x-coordinate of the rectangle's position.
     * @param y      The y-coordinate of the rectangle's position.
     * @param u      The x-coordinate of the texture's position.
     * @param v      The y-coordinate of the texture's position.
     * @param width  The width of the rectangle to draw.
     * @param height The height of the rectangle to draw.
     */
    public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
        float zLevel = 0.01f;
        float textureScaleX = 1 / 256.0f;
        float textureScaleY = 1 / 256.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + height, zLevel).tex(u * textureScaleX, (v + height) * textureScaleY).endVertex();
        buffer.pos(x + width, y + height, zLevel).tex((u + width) * textureScaleX, (v + height) * textureScaleY).endVertex();
        buffer.pos(x + width, y, zLevel).tex((u + width) * textureScaleX, v * textureScaleY).endVertex();
        buffer.pos(x, y, zLevel).tex(u * textureScaleX, v * textureScaleY).endVertex();
        tessellator.draw();
    }

    /**
     * Draws a textured rectangle on the screen using a specified texture sprite.
     *
     * @param x The x-coordinate on the screen where the rectangle starts.
     * @param y The y-coordinate on the screen where the rectangle starts.
     * @param sprite The {@link TextureAtlasSprite} containing the texture to be drawn.
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     */
    public static void drawTexturedModalRect(int x, int y, TextureAtlasSprite sprite, int width, int height) {
        float zLevel = 0.01f;

        float u1 = sprite.getMinU();
        float v1 = sprite.getMinV();
        float u2 = sprite.getMaxU();
        float v2 = sprite.getMaxV();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos((x), (y + height), zLevel).tex(u1, v1).endVertex();
        buffer.pos((x + width), (y + height), zLevel).tex(u1, v2).endVertex();
        buffer.pos((x + width), (y), zLevel).tex(u2, v2).endVertex();
        buffer.pos((x), (y), zLevel).tex(u2, v1).endVertex();
        tessellator.draw();
    }

    /**
     * Renders a <b>bright</b> textured quad (billboard) that always faces the player with a specified scale.
     *
     * This method performs the following steps:
     * 1. Calculates the brightness values for the quad.
     * 2. Pushes the current OpenGL matrix to preserve transformations.
     * 3. Rotates the quad to face the player's view direction using {@link #rotateToPlayer()}.
     * 4. Renders a bright quad using the specified scale and light map values for increased brightness.
     * 5. Pops the OpenGL matrix to restore previous transformations.
     *
     * @param scale The scale factor for the size of the quad.
     */
    public static void renderBillboardQuadBright(double scale) {
        int brightness = 240;
        int b1 = brightness >> 16 & 65535;
        int b2 = brightness & 65535;
        GlStateManager.pushMatrix();
        RenderHelper.rotateToPlayer();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
        buffer.pos(-scale, -scale, 0.0D).tex(0.0D, 0.0D).lightmap(b1, b2).color(255, 255, 255, 128).endVertex();
        buffer.pos(-scale, scale, 0.0D).tex(0.0D, 1.0D).lightmap(b1, b2).color(255, 255, 255, 128).endVertex();
        buffer.pos(scale, scale, 0.0D).tex(1.0D, 1.0D).lightmap(b1, b2).color(255, 255, 255, 128).endVertex();
        buffer.pos(scale, -scale, 0.0D).tex(1.0D, 0.0D).lightmap(b1, b2).color(255, 255, 255, 128).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
    }

    /**
     * Renders a textured quad (billboard) that always faces the player with a specified scale.
     *
     * This method performs the following steps:
     * 1. Pushes the current OpenGL matrix to preserve transformations.
     * 2. Rotates the quad to face the player's view direction using {@link #rotateToPlayer()}.
     * 3. Renders a quad using the specified scale.
     * 4. Pops the OpenGL matrix to restore previous transformations.
     *
     * @param scale The scale factor for the size of the quad.
     */
    public static void renderBillboardQuad(double scale) {
        GlStateManager.pushMatrix();

        rotateToPlayer();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-scale, -scale, 0).tex(0, 0).endVertex();
        buffer.pos(-scale, +scale, 0).tex(0, 1).endVertex();

        buffer.pos(+scale, +scale, 0).tex(1, 1).endVertex();

        buffer.pos(+scale, -scale, 0).tex(1, 0).endVertex();

        tessellator.draw();
        GlStateManager.popMatrix();
    }

    /**
     * Renders a textured quad (billboard) that always faces the player with a specified rotation and scale.
     *
     * This method performs the following steps:
     * 1. Pushes the current OpenGL matrix to preserve transformations.
     * 2. Rotates the quad to face the player's view direction using {@link #rotateToPlayer()}.
     * 3. Applies an additional rotation around the Z-axis.
     * 4. Renders a quad using the specified scale.
     * 5. Pops the OpenGL matrix to restore previous transformations.
     *
     * @param rot The rotation angle in degrees to apply around the Z-axis.
     * @param scale The scale factor for the size of the quad.
     */
    public static void renderBillboardQuadWithRotation(float rot, double scale) {
        GlStateManager.pushMatrix();

        rotateToPlayer();

        GlStateManager.rotate(rot, 0, 0, 1);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-scale, -scale, 0).tex(0, 0).endVertex();
        buffer.pos(-scale, +scale, 0).tex(0, 1).endVertex();

        buffer.pos(+scale, +scale, 0).tex(1, 1).endVertex();

        buffer.pos(+scale, -scale, 0).tex(1, 0).endVertex();

        tessellator.draw();
        GlStateManager.popMatrix();
    }

    public static void rotateToPlayer() {
        GlStateManager.rotate(-ClientTools.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(ClientTools.mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
    }

    /**
     * Draws a beam with specified thickness between two points in 3D space.
     *
     * @param S     The start point of the beam as a Vec3d.
     * @param E     The end point of the beam as a Vec3d.
     * @param P     A point to determine the normal vector for the beam's thickness.
     * @param width The thickness of the beam.
     */
    public static void drawBeam(Vec3d S, Vec3d E, Vec3d P, float width) {
        Vec3d PS = Sub(S, P);
        Vec3d SE = Sub(E, S);

        Vec3d normal = Cross(PS, SE);
        normal = normal.normalize();

        Vec3d half = Mul(normal, width);
        Vec3d p1 = Add(S, half);
        Vec3d p2 = Sub(S, half);
        Vec3d p3 = Add(E, half);
        Vec3d p4 = Sub(E, half);

        drawQuad(Tessellator.getInstance(), p1, p3, p4, p2);
    }


    /**
     * Draws a quadrilateral using the specified Tessellator and four corner points.
     *
     * @param tessellator The {@link Tessellator} instance used for drawing.
     * @param p1 The first vertex of the quadrilateral.
     * @param p2 The second vertex of the quadrilateral.
     * @param p3 The third vertex of the quadrilateral.
     * @param p4 The fourth vertex of the quadrilateral.
     */
    private static void drawQuad(Tessellator tessellator, Vec3d p1, Vec3d p2, Vec3d p3, Vec3d p4) {
        int brightness = 240;
        int b1 = 0;
        int b2 = brightness & 65535;

        // Start buffer and define the vertex format
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);

        // Define each vertex of the quadrilateral
        buffer.pos(p1.x, p1.y, p1.z).tex(0.0D, 0.0D).lightmap(b1, b2).color(255, 255, 255, 128).endVertex();
        buffer.pos(p2.x, p2.y, p2.z).tex(1.0D, 0.0D).lightmap(b1, b2).color(255, 255, 255, 128).endVertex();
        buffer.pos(p3.x, p3.y, p3.z).tex(1.0D, 1.0D).lightmap(b1, b2).color(255, 255, 255, 128).endVertex();
        buffer.pos(p4.x, p4.y, p4.z).tex(0.0D, 1.0D).lightmap(b1, b2).color(255, 255, 255, 128).endVertex();

        // Finish rendering
        tessellator.draw();
    }

    /**
     * Renders an ItemStack at a specified position with an optional overlay text.
     *
     * @param mc The {@link Minecraft} instance.
     * @param itemRender The {@link RenderItem} instance to be used to render the item.
     * @param itm The {@link ItemStack} to be rendered.
     * @param x The x-coordinate at which to render the item.
     * @param y The y-coordinate at which to render the item.
     * @param txt The overlay text to render on the item, can be null.
     * @return {@code true} if the item was successfully rendered, {@code false} if an error occurred during rendering.
     */
    public static boolean renderItemStack(Minecraft mc, RenderItem itemRender, ItemStack itm, int x, int y, String txt) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);

        boolean rc = true;
        if (!itm.isEmpty() && itm.getItem() != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 32.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableLighting();
            short short1 = 240;
            short short2 = 240;
            net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1, short2);
            try {
                itemRender.renderItemAndEffectIntoGUI(itm, x, y);
                renderItemOverlayIntoGUI(mc.fontRenderer, itm, x, y, txt, txt.length() - 2);
            } catch (Exception e) {
                ThrowableIdentity.registerThrowable(e);
                rc = false; // Report error
            }
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableLighting();
        }

        return rc;
    }

    /**
     * Renders the stack size and/or damage bar for the given ItemStack in the GUI.
     *
     * @param fr         The {@link FontRenderer} instance to be used to render text.
     * @param stack      The {@link ItemStack} for which the overlay is being rendered.
     * @param xPosition  The x-coordinate of the item position in the GUI.
     * @param yPosition  The y-coordinate of the item position in the GUI.
     * @param text       An optional text to display instead of the item count.
     * @param scaled     The scaling factor for rendering the text.
     */
    public static void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text,
                                                int scaled) {
        if (!stack.isEmpty()) {
            if (stack.getCount() != 1 || text != null) {
                String s = text == null ? String.valueOf(stack.getCount()) : text;
                if (text == null && stack.getCount() < 1) {
                    s = TextFormatting.RED + String.valueOf(stack.getCount());
                }

                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableBlend();
                if (scaled >= 2) {
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(.5f, .5f, .5f);
                    fr.drawStringWithShadow(s, ((xPosition + 19 - 2) * 2 - 1 - fr.getStringWidth(s)), yPosition * 2 + 24, 16777215);
                    GlStateManager.popMatrix();
                } else if (scaled == 1) {
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(.75f, .75f, .75f);
                    fr.drawStringWithShadow(s, ((xPosition - 2) * 1.34f + 24 - fr.getStringWidth(s)), yPosition * 1.34f + 14, 16777215);
                    GlStateManager.popMatrix();
                } else {
                    fr.drawStringWithShadow(s, (xPosition + 19 - 2 - fr.getStringWidth(s)), (yPosition + 6 + 3), 16777215);
                }
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                // Fixes opaque cooldown overlay a bit lower
                // TODO: check if enabled blending still screws things up down the line.
                GlStateManager.enableBlend();
            }

            if (stack.getItem().showDurabilityBar(stack)) {
                double health = stack.getItem().getDurabilityForDisplay(stack);
                int j = (int) Math.round(13.0D - health * 13.0D);
                int i = (int) Math.round(255.0D - health * 255.0D);
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder vertexbuffer = tessellator.getBuffer();
                draw(vertexbuffer, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
                draw(vertexbuffer, xPosition + 2, yPosition + 13, 12, 1, (255 - i) / 4, 64, 0, 255);
                draw(vertexbuffer, xPosition + 2, yPosition + 13, j, 1, 255 - i, i, 0, 255);
                GlStateManager.enableBlend();
                GlStateManager.enableAlpha();
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }

            EntityPlayerSP entityplayersp = ClientTools.mc.player;
            float f = entityplayersp == null ? 0.0F : entityplayersp.getCooldownTracker().getCooldown(stack.getItem(), ClientTools.mc.getRenderPartialTicks());

            if (f > 0.0F) {
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                Tessellator tessellator1 = Tessellator.getInstance();
                BufferBuilder vertexbuffer1 = tessellator1.getBuffer();
                draw(vertexbuffer1, xPosition, yPosition + (int)Math.floor(16.0F * (1.0F - f)), 16, (int)Math.ceil(16.0F * f), 255, 255, 255, 127);
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
        }
    }

    /**
     * Draws a colored rectangle using the provided BufferBuilder.
     *
     * @param renderer The {@link BufferBuilder} instance used to draw the rectangle.
     * @param x        The x-coordinate of the top-left corner of the rectangle.
     * @param y        The y-coordinate of the top-left corner of the rectangle.
     * @param width    The width of the rectangle.
     * @param height   The height of the rectangle.
     * @param red      The red component of the rectangle's color (0-255).
     * @param green    The green component of the rectangle's color (0-255).
     * @param blue     The blue component of the rectangle's color (0-255).
     * @param alpha    The alpha (transparency) component of the rectangle's color (0-255).
     */
    private static void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        renderer.pos((x), (y), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((x), (y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((x + width), (y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((x + width), (y), 0.0D).color(red, green, blue, alpha).endVertex();
        Tessellator.getInstance().draw();
    }


    /**
     * Renders a text string on the screen at the specified coordinates.
     *
     * @param mc  The {@link Minecraft} instance.
     * @param x   The x-coordinate where the text will be rendered.
     * @param y   The y-coordinate where the text will be rendered.
     * @param txt The text string to be rendered.
     *
     * @return The width of the rendered text.
     */
    public static int renderText(Minecraft mc, int x, int y, String txt) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableLighting();
        net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        int width = mc.fontRenderer.getStringWidth(txt);
        mc.fontRenderer.drawStringWithShadow(txt, x, y, 16777215);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        // Fixes opaque cooldown overlay a bit lower
        // TODO: check if enabled blending still screws things up down the line.
        GlStateManager.enableBlend();

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();

        return width;
    }

    private static Vec3d Cross(Vec3d a, Vec3d b) {
        double x = a.y * b.z - a.z * b.y;
        double y = a.z * b.x - a.x * b.z;
        double z = a.x * b.y - a.y * b.x;
        return new Vec3d(x, y, z);
    }

    private static Vec3d Sub(Vec3d a, Vec3d b) {
        return a.subtract(b);
    }

    private static Vec3d Add(Vec3d a, Vec3d b) {
        return a.add(b);
    }

    private static Vec3d Mul(Vec3d a, double f) {
        return a.scale(f);
    }


}
