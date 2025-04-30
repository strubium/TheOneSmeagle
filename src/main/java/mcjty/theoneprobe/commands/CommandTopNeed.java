package mcjty.theoneprobe.commands;

import mcjty.theoneprobe.event.ClientForgeEventHandlers;
import mcjty.theoneprobe.ClientTools;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.setup.proxy.GuiProxy;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandTopNeed extends CommandBase {


    @Override
    public String getName() {
        return "topneed";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "topneed";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        ClientForgeEventHandlers.ignoreNextGuiClose = true;
        EntityPlayerSP player = ClientTools.mc.player;
        player.openGui(TheOneProbe.instance, GuiProxy.GUI_NOTE, player.getEntityWorld(), (int) player.posX, (int) player.posY, (int) player.posZ);
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public int compareTo(ICommand o) {
        return getName().compareTo(o.getName());
    }
}
