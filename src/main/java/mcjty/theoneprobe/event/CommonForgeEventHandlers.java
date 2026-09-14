package mcjty.theoneprobe.event;

import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.config.NewConfig;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonForgeEventHandlers {

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        Config.setupStyleConfig(Config.mainConfig);
        NewConfig.client.probeLooks.updateCurrentOverlayStyle();

        if (Config.mainConfig.hasChanged()) {
            Config.mainConfig.save();
        }
    }
}
