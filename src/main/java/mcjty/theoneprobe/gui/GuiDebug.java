package mcjty.theoneprobe.gui;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.IOverlayStyle;
import mcjty.theoneprobe.api.TextStyleClass;
import mcjty.theoneprobe.apiimpl.ProbeInfo;
import mcjty.theoneprobe.config.ConfigSetup;
import mcjty.theoneprobe.rendering.RenderHelper;
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
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static mcjty.theoneprobe.api.TextStyleClass.*;

/**
 * The GUI uses for the Probe Note's config screen
 *
 * @since 11/10/2016
 * @author McJty, strubium
 */
@SideOnly(Side.CLIENT)
public class GuiDebug extends GuiScreen {
    private static final int WIDTH = 230;
    private static final int HEIGHT = 230;

    private int guiLeft;
    private int guiTop;

    private static final ResourceLocation background = new ResourceLocation(TheOneProbe.MODID, "textures/gui/config.png");

    private List<HitBox> hitboxes = Collections.emptyList();

    @Override
    public boolean doesGuiPauseGame() {
        return true;
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
        drawTexturedModalRect(guiLeft + WIDTH, guiTop, 0, 0, WIDTH, HEIGHT);

        int x = WIDTH + guiLeft + 10;
        int y = guiTop + 10;
        hitboxes = new ArrayList<>();

        RenderHelper.renderText(Minecraft.getMinecraft(), x, y, TextFormatting.GOLD + I18n.format("gui.theoneprobe.gui_note_config.title.scale"));
        y += 12;
        RenderHelper.renderText(Minecraft.getMinecraft(), x+10, y, I18n.format("gui.theoneprobe.gui_note_config.body.3"));
        y += 12;
        addButton(x+10, y, "--", () -> ConfigSetup.setScale(ConfigSetup.getScale() + 0.2F)); x += 36;
        addButton(x+10, y, "-", () -> ConfigSetup.setScale(ConfigSetup.getScale() + 0.1F)); x += 36;
        addButton(x+10, y, "!", () -> ConfigSetup.setScale(1f)); x += 36;
        addButton(x+10, y, "+", () -> ConfigSetup.setScale(ConfigSetup.getScale() - 0.1F)); x += 36;
        addButton(x+10, y, "++", () -> ConfigSetup.setScale(ConfigSetup.getScale() - 0.2F));
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

    private void addButton(int x, int y, String text, Runnable runnable) {
        drawRect(x, y, x + 30 -1, y + 14 -1, 0xff000000);
        RenderHelper.renderText(Minecraft.getMinecraft(), x + 3, y + 3, text);
        hitboxes.add(new HitBox(x - guiLeft, y - guiTop, x + 30 -1 - guiLeft, y + 14 -1 - guiTop, runnable));
    }
}
