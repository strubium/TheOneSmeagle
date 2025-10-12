package mcjty.theoneprobe.gui;

import mcjty.theoneprobe.ClientTools;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.IOverlayStyle;
import mcjty.theoneprobe.apiimpl.ProbeInfo;
import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.rendering.RenderHelper;
import mcjty.theoneprobe.rendering.TextureGenerator;
import mcjty.theoneprobe.setup.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static mcjty.theoneprobe.api.TextStyleClass.*;

/**
 * The GUI uses for the Probe Note's config screen
 *
 * @since 11/10/2016
 * @author McJty, strubium
 */
@SideOnly(Side.CLIENT)
public class GuiConfig extends GuiScreen {
    private static final int WIDTH = 230;
    private static final int HEIGHT = 230;

    private int guiLeft;
    private int guiTop;

    private static final ResourceLocation background = TextureGenerator.generateTexture("scene_background", WIDTH, HEIGHT, TextureGenerator.PatternType.GUI_BACKGROUND, new Color(124, 124, 124), null, 0);
    private static final ResourceLocation scene = new ResourceLocation(TheOneProbe.MODID, "textures/gui/scene.png");

    // Convert ARGB int to Color
    static Color baseButtonColor = new Color(Config.probeButtonColor, true);
    ResourceLocation buttonTexture = TextureGenerator.generateTexture(
            "config_button_preset",
            WIDTH - 50, 11,
            TextureGenerator.PatternType.GUI_BUTTON,
            baseButtonColor,
            baseButtonColor,
            1 // patternSize for bevel effect
    );
    private static final ResourceLocation buttonTextureLower = TextureGenerator.generateTexture(
            "config_button_lower",
            30, 14,
            TextureGenerator.PatternType.GUI_BUTTON,
            baseButtonColor,
            baseButtonColor,
            1 // patternSize used for bevel depth
    );

    private List<HitBox> hitboxes = Collections.emptyList();

    int[][] hitboxPositionMap = {
            {2, 2, -1, -1},   // Top-left
            {-1, 2, -1, -1},  // Top-center
            {-1, 2, 2, -1},   // Top-right
            {2, -1, -1, -1},  // Middle-left
            {-1, -1, -1, -1}, // Center
            {-1, -1, 5, -1},  // Middle-right
            {2, -1, -1, 5},   // Bottom-left
            {-1, -1, -1, 15}, // Bottom-center
            {-1, -1, 5, 2}    // Bottom-right
    };

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        super.initGui();
        guiLeft = (this.width - WIDTH - WIDTH) / 2;
        guiTop = (this.height - HEIGHT) / 2;
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        mc.getTextureManager().bindTexture(background);
        drawScaledCustomSizeModalRect(guiLeft + WIDTH, guiTop,
                0, 0, WIDTH, HEIGHT,
                WIDTH, HEIGHT,
                WIDTH, HEIGHT);
        mc.getTextureManager().bindTexture(scene);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);

        renderProbe(Config.getProbeNoteBlock());

        int x = WIDTH + guiLeft + 10;
        int y = guiTop + 10;
        RenderHelper.renderText(ClientTools.mc, x, y, TextFormatting.GOLD + I18n.format("gui.theoneprobe.gui_note_config.title.placement"));
        y += 12;
        RenderHelper.renderText(ClientTools.mc, x+10, y, I18n.format("gui.theoneprobe.gui_note_config.body.1"));
        y += 10;
        RenderHelper.renderText(ClientTools.mc, x+10, y, I18n.format("gui.theoneprobe.gui_note_config.body.2"));
        y += 20;

        hitboxes = new ArrayList<>();
        RenderHelper.renderText(ClientTools.mc, x, y, TextFormatting.GOLD + I18n.format("gui.theoneprobe.gui_note_config.title.presets") + TextFormatting.RESET);
        y += 12;
        for (Preset preset : PresetBuilder.getPresets()) {
            y = addPreset(x, y, preset);
        }

        y += 5;

        RenderHelper.renderText(ClientTools.mc, x, y, TextFormatting.GOLD + I18n.format("gui.theoneprobe.gui_note_config.title.scale"));
        y += 12;
        RenderHelper.renderText(ClientTools.mc, x+10, y, I18n.format("gui.theoneprobe.gui_note_config.body.3"));
        y += 12;
        addButton(x+10, y, "--", () -> Config.setScale(Config.getTooltipScale() + 0.2F)); x += 36;
        addButton(x+10, y, "-", () -> Config.setScale(Config.getTooltipScale() + 0.1F)); x += 36;
        addButton(x+10, y, "0", () -> Config.setScale(1f)); x += 36;
        addButton(x+10, y, "+", () -> Config.setScale(Config.getTooltipScale() - 0.1F)); x += 36;
        addButton(x+10, y, "++", () -> Config.setScale(Config.getTooltipScale() - 0.2F));

        int margin = 90;

        int[] xPositions = {0, margin, WIDTH - margin};
        int[] yPositions = {0, margin, HEIGHT - margin};

        // Create the hitboxes
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int x1 = xPositions[i];
                int y1 = yPositions[j];
                int x2 = (i == 2) ? WIDTH : xPositions[i + 1];
                int y2 = (j == 2) ? HEIGHT : yPositions[j + 1];

                int[] pos = hitboxPositionMap[j * 3 + i];

                hitboxes.add(new HitBox(x1, y1, x2, y2, () -> Config.setPos(pos[0], pos[1], pos[2], pos[3])));
            }
        }

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            for (HitBox box : hitboxes) {
                if (box.isHit(mouseX-guiLeft, mouseY-guiTop)) {
                    box.call();
                }
            }
        }
    }


    private int addPreset(int x, int y, Preset preset) {
        int width = WIDTH - 50;
        int height = 11;

        // Bind and draw the texture
        Minecraft.getMinecraft().getTextureManager().bindTexture(buttonTexture);
        drawModalRectWithCustomSizedTexture(x + 10, y - 1, 0, 0, width, height, width, height);

        // Prepare the localized name or fallback
        String presetNameKey = preset.getName().toLowerCase().replace(" ", "");
        String displayName = I18n.hasKey("theoneprobe.preset." + presetNameKey + ".config")
                ? I18n.format("theoneprobe.preset." + presetNameKey + ".config")
                : preset.getName();

        // Render text on top of the button
        RenderHelper.renderText(ClientTools.mc, x + 20, y, displayName);

        // Register the interactive area
        hitboxes.add(new HitBox(
                x + 10 - guiLeft, y - 1 - guiTop,
                x + 10 + width - guiLeft, y + height - guiTop,
                () -> PresetBuilder.applyPreset(preset)
        ));

        return y + 14;
    }

    private void addButton(int x, int y, String text, Runnable runnable) {
        int width = 30;
        int height = 14;

        // Bind and draw the texture
        Minecraft.getMinecraft().getTextureManager().bindTexture(buttonTextureLower);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);

        // Render the button text
        RenderHelper.renderText(ClientTools.mc, x + 3, y + 3, text);

        // Add hitbox
        hitboxes.add(new HitBox(
                x - guiLeft, y - guiTop,
                x + width - 1 - guiLeft, y + height - 1 - guiTop,
                runnable
        ));
    }


    /**
     * Renders the fake TOP overlay in the GUI
     *
     * @param blockName The {@link Block} (as a string) to use for the example
     *
     * @author McJty
     */
    private void renderProbe(String blockName) {
        // Try to resolve the block from the string blockName
        Block block = Block.getBlockFromName(blockName);

        // If the block isn't found, default to air or handle error
        if (block == Blocks.AIR || block == null) {
            CommonProxy.getLogger().error("Block not found: {}! Defaulting to Log", blockName);
            block = Blocks.LOG;
        }

        String modid = Tools.getModName(block);
        ProbeInfo probeInfo = TheOneProbe.theOneProbeImp.create();
        ItemStack pickBlock = new ItemStack(block);
        probeInfo.horizontal()
                .item(pickBlock)
                .vertical()
                .text(NAME + pickBlock.getDisplayName())
                .text(MODNAME + modid);
        probeInfo.text(LABEL + "{*theoneprobe.probe.fuel_indicator*} " + INFO + "5 volts");
        probeInfo.text(LABEL + "{*theoneprobe.probe.error_indicator*} " + ERROR + "Oups!");

        renderElements(probeInfo, Config.getDefaultOverlayStyle());
    }

    /**
     * Renders elements of the probe information overlay with the given style.
     *
     * @param probeInfo The {@link ProbeInfo} object containing the information to be rendered.
     * @param style     The {@link IOverlayStyle} object defining the style of the overlay.
     */
    private void renderElements(ProbeInfo probeInfo, IOverlayStyle style) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / Config.tooltipScale, 1 / Config.tooltipScale, 1 / Config.tooltipScale);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();

        int w = probeInfo.getWidth();
        int h = probeInfo.getHeight();

        int offset = style.getBorderOffset();
        int thick = style.getBorderThickness();
        int margin = (thick > 0) ? offset + thick + 3 : 0;

        if (thick > 0) {
            w += (margin * 2);
            h += (margin * 2);
        }

        int x = calculateXPosition(style, w);
        int y = calculateYPosition(style, h);

        x += guiLeft;
        y += guiTop;

        double factor = (Config.tooltipScale - 1) * 1.4 + 1;
        x *= (int) factor;
        y *= (int) factor;

        if (thick > 0) {
            int x2 = x + w - 1;
            int y2 = y + h - 1;
            if (offset > 0) {
                RenderHelper.drawThickBeveledBox(x, y, x2, y2, thick, style.getBoxColor(), style.getBoxColor(), style.getBoxColor());
            }
            RenderHelper.drawThickBeveledBox(x + offset, y + offset, x2 - offset, y2 - offset, thick, style.getBorderColor(), style.getBorderColor(), style.getBoxColor());
        }

        if (!ClientTools.mc.isGamePaused()) {
            RenderHelper.rot += .5f;
        }

        probeInfo.render(x + margin, y + margin);

        GlStateManager.popMatrix();
    }

    /**
     * Calculates the x position for the overlay based on the given style and width.
     *
     * @param style The {@link IOverlayStyle} object defining the style of the overlay.
     * @param width The width of the overlay.
     * @return The calculated x position.
     */
    private int calculateXPosition(IOverlayStyle style, int width) {
        if (style.getLeftX() != -1) {
            return style.getLeftX();
        } else if (style.getRightX() != -1) {
            return WIDTH - width - style.getRightX();
        } else {
            return (WIDTH - width) / 2;
        }
    }

    /**
     * Calculates the y position for the overlay based on the given style and height.
     *
     * @param style  The {@link IOverlayStyle} object defining the style of the overlay.
     * @param height The height of the overlay.
     * @return The calculated y position.
     */
    private int calculateYPosition(IOverlayStyle style, int height) {
        if (style.getTopY() != -1) {
            return style.getTopY();
        } else if (style.getBottomY() != -1) {
            return HEIGHT - height - style.getBottomY();
        } else {
            return (HEIGHT - height) / 2;
        }
    }
}
