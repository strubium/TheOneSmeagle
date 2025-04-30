package mcjty.theoneprobe.event;

import mcjty.theoneprobe.config.Config;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonForgeEventHandlers {

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        Config.setupStyleConfig(Config.mainConfig);
        Config.updateDefaultOverlayStyle();

        if (Config.mainConfig.hasChanged()) {
            Config.mainConfig.save();
        }
    }
}
