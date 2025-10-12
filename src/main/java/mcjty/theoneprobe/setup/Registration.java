package mcjty.theoneprobe.setup;

import mcjty.theoneprobe.items.ModItems;
import mcjty.theoneprobe.setup.proxy.CommonProxy;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class Registration {

    private static final List<Item> itemReg = new ArrayList<>();

    public static void addItem(Item item) {
        if (item != null) {
            itemReg.add(item);
        } else {
            throw new IllegalArgumentException("Cannot add null to itemReg");
        }
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        final ProgressManager.ProgressBar bar = ProgressManager.push("Registering Items", 1);
        bar.step("Registering Items");
        ModItems.init();

        if (!CommonProxy.baubles) { //Googles are automatically added to registration, so we need to remove it if baubles isn't loaded
            itemReg.remove(ModItems.probeGoggles);
        }

        for (Item item : itemReg) {
            event.getRegistry().register(item);
        }


        ProgressManager.pop(bar);
    }

}
