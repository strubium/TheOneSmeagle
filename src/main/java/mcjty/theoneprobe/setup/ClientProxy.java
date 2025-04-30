package mcjty.theoneprobe.setup;

import com.google.common.util.concurrent.ListenableFuture;
import mcjty.theoneprobe.event.ClientForgeEventHandlers;
import mcjty.theoneprobe.ClientTools;
import mcjty.theoneprobe.commands.CommandTopCfg;
import mcjty.theoneprobe.commands.CommandTopNeed;
import mcjty.theoneprobe.keys.KeyBindings;
import mcjty.theoneprobe.keys.KeyInputHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.concurrent.Callable;

@SideOnly(Side.CLIENT)
public class ClientProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(new ClientForgeEventHandlers());
    }

    @Override
    public void init(FMLInitializationEvent e) {
        ClientCommandHandler.instance.registerCommand(new CommandTopCfg());
        ClientCommandHandler.instance.registerCommand(new CommandTopNeed());
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler()); //Load the keybindings
        KeyBindings.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
    }

    @Override
    public World getClientWorld() {
        return ClientTools.mc.world;
    }

    @Override
    public EntityPlayer getClientPlayer() {
        return ClientTools.mc.player;
    }

    @Override
    public <V> ListenableFuture<V> addScheduledTaskClient(Callable<V> callableToSchedule) {
        return ClientTools.mc.addScheduledTask(callableToSchedule);
    }

    @Override
    public ListenableFuture<Object> addScheduledTaskClient(Runnable runnableToSchedule) {
        return ClientTools.mc.addScheduledTask(runnableToSchedule);
    }
}
