package mcjty.theoneprobe.config;

import mcjty.theoneprobe.TheOneProbe;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;

@Config(modid = TheOneProbe.MODID, name = TheOneProbe.MODID + "_new")
public class NewConfig {

    @Config.Comment("Server sided configs. The server has authority")
    public static Server server = new Server();

    @Config.Comment("Client sided configs. The server DOES NOT has authority")
    public static Client client = new Client();

    @Config.Comment("Networking related configs. The server authority will be marked per config")
    public static Networking networking = new Networking();

    public static Show show = new Show();


    @Config.Comment("Distance at which the probe works")
    @Config.RangeDouble(min = 0.1, max = 200)
    public static float probeDistance = 6;


    public static class Show {

        @Config.Comment("If true show liquid information when the probe hits liquid first")
        public boolean showLiquids = false;

        @Config.Comment("Show a entities UUID in the debug probe menu")
        public boolean showDebugUUID = false;


        public void setLiquids(boolean liquids) {
            NewConfig.show.showLiquids = liquids;
            ConfigManager.sync(TheOneProbe.MODID, Config.Type.INSTANCE);
        }
    }

    public static class Server {
        @Config.Comment("If true, equal stacks will be compacted in the chest contents overlay")
        public boolean compactEqualStacks = true;

        @Config.Comment("If true, show clients the color color of tamed wolves")
        public boolean showCollarColor = true;

        @Config.Comment("List of mod IDs whose helmets should be ignored")
        public String[] probeHelmetBlacklist = new String[]{"mwc"};


        public void setCompactEqualStacks(boolean compact) {
            NewConfig.server.compactEqualStacks = compact;
            ConfigManager.sync(TheOneProbe.MODID, Config.Type.INSTANCE);
        }
    }

    public static class Client {

        @Config.Comment("The scale of the tooltips, 1 is default, 2 is smaller")
        @Config.RangeDouble(min = 0.4, max = 5)
        public float tooltipScale = 1.0f;

        @Config.Comment("Is the probe visible? (client can override)")
        public boolean isVisible = true;

        @Config.Comment("If true, the probe hotkey must be held down to show the tooltip")
        public boolean holdKeyToMakeVisible = false;

        @Config.Comment("If true, the probe note can show its config GUI on right-click")
        public boolean unlockProbeConfigGUI = true;

        @Config.Comment("If true, the probe note can show its note GUI on right-click")
        public boolean unlockProbeNoteGUI = true;

        @Config.Comment("The language translation keys to use when showing harvest levels")
        public String[] harvestLevelTranslationKeys = new String[]{
                "theoneprobe.harvestlevel.stone",
                "theoneprobe.harvestlevel.iron",
                "theoneprobe.harvestlevel.diamond",
                "theoneprobe.harvestlevel.obsidian",
                "theoneprobe.harvestlevel.cobalt",
                "theoneprobe.harvestlevel.duranite",
                "theoneprobe.harvestlevel.valyrium",
                "theoneprobe.harvestlevel.vibranium"
        };

        @Config.Comment("The block used inside the probe note example")
        public String probeNoteBlock = "minecraft:log";


        public void setTooltipScale(float scale) {
            NewConfig.client.tooltipScale = scale;
            ConfigManager.sync(TheOneProbe.MODID, Config.Type.INSTANCE);
        }

        public void setVisible(boolean visible) {
            NewConfig.client.isVisible = visible;
            ConfigManager.sync(TheOneProbe.MODID, Config.Type.INSTANCE);
        }
    }


    public static class Networking {

        @Config.Comment("The amount of milliseconds to wait before requesting block information from the server (this is a client-side config)")
        public int blockTimeout = 300;

        @Config.Comment("The amount of milliseconds to wait before requesting entity information from the server (this is a client-side config)")
        public int entityTimeout = 400;
    }


}
