package mcjty.theoneprobe.config;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.IOverlayStyle;
import mcjty.theoneprobe.api.ProbeRequirement;
import mcjty.theoneprobe.apiimpl.styles.DefaultOverlayStyle;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;

@Config(modid = TheOneProbe.MODID, name = "theonesmeagle")
public class NewConfig {

    @Config.Comment("Server sided configs. The server has authority")
    public static Server server = new Server();

    @Config.Comment("Client sided configs. The server DOES NOT has authority")
    public static Client client = new Client();

    @Config.Comment("Networking related configs. The server authority will be marked per config")
    public static Networking networking = new Networking();

    public static class Server {

        public Debug debug = new Debug();

        public static class Debug {

            @Config.Comment("If true, show debug info with creative probe")
            public boolean showDebugInfo = true;

            @Config.Comment("If true, show a entities UUID in the debug probe menu")
            public boolean showDebugUUID = false;
        }

        @Config.Comment("Is the probe needed to show the tooltip? 0 = no, 1 = yes, 2 = yes and clients cannot override, 3 = probe needed for extended info only")
        @Config.RangeInt(min = 0, max = 3)
        public int needsProbe = ProbeRequirement.PROBE_NEEDEDFOREXTENDED.configNumber;


        @Config.Comment("If true, equal stacks will be compacted in the chest contents overlay")
        public boolean compactEqualStacks = true;

        @Config.Comment("If true, show clients the color color of tamed wolves")
        public boolean showCollarColor = true;

        @Config.Comment("List of mod IDs whose helmets should be ignored")
        public String[] probeHelmetBlacklist = new String[]{"mwc"};

        @Config.Comment("Max amount of potions to show on a entity")
        @Config.RangeInt(min = 1, max = 256)
        public int potionMaxNumber = 5;

        @Config.Comment("If true, first-time players will be given a read-me note")
        public boolean spawnNote = true;

        @Config.Comment("The max displaying width of a block name, 0.0 is no limit, otherwise represents the percentage with respect to the whole screen")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public float blockNameMaxWidth = 0.0f;

        @Config.Comment("If true, there will be a bauble version of the probe if baubles is present")
        public boolean supportBaubles = true;

        @Config.Comment("If true, probes will be registered. Useful if needsProbe is 0")
        public boolean regProbes = true;

        @Config.Comment("If true, probe helmets will be registered. Useful if needsProbe is 0")
        public boolean regProbeHelmets = false;

        @Config.Comment("Stack size of the Readme note")
        @Config.RangeInt(min = 1, max = 64)
        public int probeNoteStackSize = 1;

        public void setCompactEqualStacks(boolean compact) {
            NewConfig.server.compactEqualStacks = compact;
            ConfigManager.sync(TheOneProbe.MODID, Config.Type.INSTANCE);
        }
        public void setProbeNeeded(ProbeRequirement probeNeeded) {
            NewConfig.server.needsProbe = probeNeeded.configNumber;
            ConfigManager.sync(TheOneProbe.MODID, Config.Type.INSTANCE);
        }

    }

    public static class Client {

        public ProbeLooks probeLooks = new ProbeLooks();

        public static class ProbeLooks {

            /// The current style we use for rendering
            @Config.Ignore
            public IOverlayStyle currentOverlayStyle;


            @Config.Comment("The left offset for the probe")
            @Config.RangeInt(min = -1, max = 10000)
            private int leftX = 0;

            @Config.Comment("The right offset for the probe")
            @Config.RangeInt(min = -1, max = 10000)
            private int topY = 0;

            @Config.Comment("The top offset for the probe")
            @Config.RangeInt(min = -1, max = 10000)
            private int rightX = -1;

            @Config.Comment("The bottom offset for the probe")
            @Config.RangeInt(min = -1, max = 10000)
            private int bottomY = -1;

            public void setPos(int leftx, int topy, int rightx, int bottomy) {
                NewConfig.client.probeLooks.leftX = leftx;
                NewConfig.client.probeLooks.topY = topy;
                NewConfig.client.probeLooks.rightX = rightx;
                NewConfig.client.probeLooks.bottomY = bottomy;
                ConfigManager.sync(TheOneProbe.MODID, Config.Type.INSTANCE);

                updateCurrentOverlayStyle();
            }

            public void setBoxStyle(int thickness, int borderColor, int fillcolor) {
                boxThickness = thickness;
                boxBorderColor = borderColor;
                boxFillColor = fillcolor;
                ConfigManager.sync(TheOneProbe.MODID, Config.Type.INSTANCE);


                updateCurrentOverlayStyle();
            }

            public void updateCurrentOverlayStyle() {
                this.currentOverlayStyle = new DefaultOverlayStyle()
                        .borderThickness(this.boxThickness)
                        .borderColor(this.boxBorderColor)
                        .boxColor(this.boxFillColor)
                        .location(this.leftX, this.rightX, this.topY, this.bottomY);

                ConfigManager.sync(TheOneProbe.MODID, Config.Type.INSTANCE);
            }

            public IOverlayStyle getCurrentOverlayStyle() {
                if (currentOverlayStyle == null) {
                    updateCurrentOverlayStyle();
                }
                return currentOverlayStyle;
            }


            @Config.Comment("Color of the buttons in the probe note (0 to disable)")
            public int probeButtonColor = 0xFF404040;

            @Config.Comment("Color of the progress bar (0 to disable)")
            public int probeProgressColor = 0xff990000;

            @Config.Comment("Alt color of the progress bar (0 to disable)")
            public int probeProgressAltColor = 0xff550000;

            @Config.Comment("Color of the border of the progress bar (0 to disable)")
            public int probeProgressBorderColor = 0;

            @Config.Comment("Color of the background of the progress bar (0 to disable)")
            public int probeProgressBackgroundColor = 0xff000000;

            @Config.Comment("Color of the border of the chest contents box (0 to disable)")
            public int chestContentsBorderColor = 0xff006699;

            @Config.Comment("Use a gradient instead of alternating colors in solid blocks")
            public boolean probeProgressGradient = false;

            @Config.Comment("Color of the border of the box (0 to disable)")
            private int boxBorderColor = 0xff999999;

            @Config.Comment("Color of the box (0 to disable)")
            private int boxFillColor = 0x55006699;

            @Config.Comment("Thickness of the border of the box (0 to disable)")
            @Config.RangeInt(min = 0, max = 20)
            private int boxThickness = 2;


            @Config.Comment("Color for the RF bar")
            public int rfbarFilledColor = 0xffdd0000;

            @Config.Comment("Alternate color for the RF bar")
            public int rfbarAlternateFilledColor = 0xff430000;

            @Config.Comment("Color for the RF bar border")
            public int rfbarBorderColor = 0xff555555;

            @Config.Comment("Color for the tank bar")
            public int tankbarFilledColor = 0xff0000dd;

            @Config.Comment("Alternate color for the tank bar")
            public int tankbarAlternateFilledColor = 0xff000043;

            @Config.Comment("Color for the tank bar border")
            public int tankbarBorderColor = 0xff555555;
        }

        @Config.Comment("Distance at which the probe works")
        @Config.RangeDouble(min = 0.1, max = 200)
        public float probeDistance = 6;

        @Config.Comment("If true, show the text in the progress bar")
        public boolean showBreakProgressText = true;

        @Config.Comment("0 means don't show break progress, 1 is show as bar, 2 is show as text")
        @Config.RangeInt(min = 0, max = 2)
        public int showBreakProgress = 1;    // 0 == off, 1 == bar, 2 == text

        @Config.Comment("The scale of the tooltips, 1 is default, 2 is smaller")
        @Config.RangeDouble(min = 0.4, max = 5)
        public float tooltipScale = 1.0f;

        @Config.Comment("Is the probe visible?")
        public boolean isVisible = true;

        @Config.Comment("If true, show harvestability with vanilla style icons")
        public boolean harvestStyleVanilla = true;

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

        @Config.Comment("If true show liquid information when the probe hits liquid first")
        public boolean showLiquids = false;

        public void setLiquids(boolean liquids) {
            NewConfig.client.showLiquids = liquids;
            ConfigManager.sync(TheOneProbe.MODID, Config.Type.INSTANCE);
        }

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
        public int blockTimeout = 400;

        @Config.Comment("The amount of milliseconds to wait before requesting entity information from the server (this is a client-side config)")
        public int entityTimeout = 500;

        @Config.Comment("The amount of milliseconds to wait before showing a error on the client if the server is slow to respond (-1 to disable this feature) (this is a client-side config)")
        @Config.RangeInt(min = -1, max = 100000)
        public int waitingForServerTimeout = 2000;


        @Config.Comment("How much time (in ms) to wait before reporting an exception again (this is a server and client-side config)")
        @Config.RangeInt(min = 1, max = 10000000)
        public int loggingThrowableTimeout = 20000;
    }


}
