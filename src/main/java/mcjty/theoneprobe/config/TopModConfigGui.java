package mcjty.theoneprobe.config;

import mcjty.theoneprobe.TheOneProbe;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Config GUI for The One Probe
 *
 * @author McJty, strubium
 */
@SideOnly(Side.CLIENT)
public class TopModConfigGui extends GuiConfig {

    public TopModConfigGui(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(), TheOneProbe.MODID, false, false, I18n.format("config.theoneprobe.config.title"));
    }

    /**
     * Creates a list of config elements, each corresponding to a category.
     * Each element will be shown as a separate button in the GUI.
     *
     * @return a list of config elements for the GUI
     */
    private static List<IConfigElement> getConfigElements() {
        // Define the categories for the configuration
        final String[] categories = {
                Config.CATEGORY_CLIENT,
                Config.CATEGORY_THEONEPROBE,
                Config.CATEGORY_PROVIDERS
        };

        // Create configuration elements for the main categories
        return Arrays.stream(categories)
                .map(category -> new ConfigElement(Config.mainConfig.getCategory(category)))
                .collect(Collectors.toList());
    }
}
