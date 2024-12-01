package mcjty.theoneprobe;

import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.config.ConfigSetup;
import mcjty.theoneprobe.gui.GuiConfig;
import mcjty.theoneprobe.gui.GuiNote;
import mcjty.theoneprobe.items.ModItems;
import mcjty.theoneprobe.keys.KeyBindings;
import mcjty.theoneprobe.rendering.OverlayRenderer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static mcjty.theoneprobe.config.ConfigSetup.*;
@SideOnly(Side.CLIENT)
public class ClientForgeEventHandlers {

    public static boolean ignoreNextGuiClose = false;

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (ignoreNextGuiClose) {
            GuiScreen current = Tools.mc.currentScreen;
            if (event.getGui() == null && (current instanceof GuiConfig || current instanceof GuiNote)) {
                ignoreNextGuiClose = false;
                // We don't want our gui to be closed for a new 'null' gui
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void renderGameOverlayEvent(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) {
            return;
        }

        if (ConfigSetup.holdKeyToMakeVisible) {
            if (!KeyBindings.toggleVisible.isKeyDown()) {
                return;
            }
        } else {
            if (!ConfigSetup.isVisible) {
                return;
            }
        }

        if (hasItemInEitherHand(ModItems.creativeProbe)) {
            OverlayRenderer.renderHUD(ProbeMode.DEBUG, event.getPartialTicks());
        } else {
            switch (ConfigSetup.needsProbe) {
                case PROBE_NOTNEEDED:
                case PROBE_NEEDEDFOREXTENDED:
                    OverlayRenderer.renderHUD(getModeForPlayer(), event.getPartialTicks());
                    break;
                case PROBE_NEEDED:
                case PROBE_NEEDEDHARD:
                    if (ModItems.hasAProbeSomewhere(Tools.mc.player)) {
                        OverlayRenderer.renderHUD(getModeForPlayer(), event.getPartialTicks());
                    }
                    break;
            }
        }
    }

    private ProbeMode getModeForPlayer() {
        EntityPlayerSP player = Tools.mc.player;
        if (ConfigSetup.extendedInMain) {
            if (hasItemInMainHand(ModItems.probe)) {
                return ProbeMode.EXTENDED;
            }
        }
        return player.isSneaking() ? ProbeMode.EXTENDED : ProbeMode.NORMAL;
    }

    private boolean hasItemInEitherHand(Item item) {
        ItemStack mainHeldItem = Tools.mc.player.getHeldItem(EnumHand.MAIN_HAND);
        ItemStack offHeldItem = Tools.mc.player.getHeldItem(EnumHand.OFF_HAND);
        return (mainHeldItem != null && mainHeldItem.getItem() == item) ||
                (offHeldItem != null && offHeldItem.getItem() == item);
    }


    private boolean hasItemInMainHand(Item item) {
        ItemStack mainHeldItem = Tools.mc.player.getHeldItem(EnumHand.MAIN_HAND);
        return mainHeldItem != null && mainHeldItem.getItem() == item;
    }
}