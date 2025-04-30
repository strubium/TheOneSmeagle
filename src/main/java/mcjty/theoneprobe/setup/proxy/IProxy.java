package mcjty.theoneprobe.setup.proxy;

import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.concurrent.Callable;

/**
 * Handle client and server side operations.
 */
public interface IProxy {

    /**
     * Called during the pre-initialization phase of the mod loading.
     *
     * @param e The event that contains information about the pre-initialization phase.
     */
    void preInit(FMLPreInitializationEvent e);

    /**
     * Called during the initialization phase of the mod loading.
     *
     * @param e The event that contains information about the initialization phase.
     */
    void init(FMLInitializationEvent e);

    /**
     * Called during the post-initialization phase of the mod loading.
     *
     * @param e The event that contains information about the post-initialization phase.
     */
    void postInit(FMLPostInitializationEvent e);

    /**
     * Gets the client world instance. This method is used to obtain the client-side world.
     *
     * @return The client world instance, or null if called on the server side.
     */
    World getClientWorld();

    /**
     * Gets the client player instance. This method is used to obtain the player on the client side.
     *
     * @return The client player instance, or null if called on the server side.
     */
    EntityPlayer getClientPlayer();

    /**
     * Schedules a task to be executed on the client thread.
     *
     * @param <V> The result type of the task.
     * @param callableToSchedule The task to be scheduled.
     * @return A ListenableFuture representing pending completion of the task.
     */
    <V> ListenableFuture<V> addScheduledTaskClient(Callable<V> callableToSchedule);

    /**
     * Schedules a task to be executed on the client thread.
     *
     * @param runnableToSchedule The task to be scheduled.
     * @return A ListenableFuture representing pending completion of the task.
     */
    ListenableFuture<Object> addScheduledTaskClient(Runnable runnableToSchedule);
}