package mcjty.theoneprobe.keys;

import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.config.NewConfig;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyInputHandler {

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeyBindings.toggleLiquids.isPressed()) {
            NewConfig.client.setLiquids(!NewConfig.client.showLiquids);
        } else if (KeyBindings.toggleVisible.isPressed()) {
            if (!NewConfig.client.holdKeyToMakeVisible) {
                NewConfig.client.setVisible(!NewConfig.client.isVisible);
            }
        }
    }
}
