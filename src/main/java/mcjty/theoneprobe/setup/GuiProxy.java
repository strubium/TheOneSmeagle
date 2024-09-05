package mcjty.theoneprobe.setup;

import mcjty.theoneprobe.gui.GuiConfig;
import mcjty.theoneprobe.gui.GuiDebug;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import mcjty.theoneprobe.gui.GuiNote;

public class GuiProxy implements IGuiHandler {

    public static int GUI_NOTE = 1;
    public static int GUI_CONFIG = 2;
    public static int GUI_DEBUG = 3;

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
            return new GuiNote();
        } else if (guiid == GUI_CONFIG) {
            return new GuiConfig();
        }else if (guiid == GUI_DEBUG)  {
            return new GuiDebug();
        }
        else {
            return null;
        }
    }
}
