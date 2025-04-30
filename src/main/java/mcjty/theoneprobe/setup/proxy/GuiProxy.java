package mcjty.theoneprobe.setup.proxy;

import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.gui.GuiConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import mcjty.theoneprobe.gui.GuiNote;

public class GuiProxy implements IGuiHandler {

    public static int GUI_NOTE = 1;
    public static int GUI_CONFIG = 2;

    @Override
    public Object getServerGuiElement(int guiid, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        return null;
    }

    /**
     * Returns the client-side GUI element based on the provided GUI ID.
     *
     * @param guiid The ID of the GUI to be displayed.
     * @param entityPlayer The player entity.
     * @param world The world in which the GUI is being requested.
     * @param x The x-coordinate for the GUI (if applicable).
     * @param y The y-coordinate for the GUI (if applicable).
     * @param z The z-coordinate for the GUI (if applicable).
     *
     * @return The client-side GUI element, or null if the GUI ID does not match any known GUIs.
     */
    @Override
    public Object getClientGuiElement(int guiid, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        if (guiid == GUI_NOTE) {
            if(Config.getShowProbeNoteGUI()){
                return new GuiNote();
            }
        } else if (guiid == GUI_CONFIG) {
            if(Config.getShowProbeConfigGUI()){
                return new GuiConfig();
            }
        } else {
            return null;
        }
        return null;
    }
}
