package mcjty.theoneprobe.config;


import lombok.Getter;
import lombok.Setter;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.IOverlayStyle;
import mcjty.theoneprobe.api.IProbeConfig;
import mcjty.theoneprobe.api.NumberFormat;
import mcjty.theoneprobe.api.TextStyleClass;
import mcjty.theoneprobe.apiimpl.ProbeConfig;
import mcjty.theoneprobe.apiimpl.styles.DefaultOverlayStyle;
import mcjty.theoneprobe.setup.proxy.CommonProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.util.*;

import static mcjty.theoneprobe.api.TextStyleClass.*;

/**
 * @since 5/14/2016
 * @author McJty
 */
public class Config {

    public static Configuration mainConfig;

    public static String CATEGORY_THEONEPROBE = "theoneprobe";
    public static String CATEGORY_PROVIDERS = "providers";
    public static String CATEGORY_CLIENT = "client";
    public static String SUBCATEGORY_OFFSETS = "client_offsets";
    public static String SUBCATEGORY_TEXT = "client_text";
    public static String SUBCATEGORY_SHOW = "theoneprobe_show";


    public static final int PROBE_NOTNEEDED = 0;
    public static final int PROBE_NEEDED = 1;
    public static final int PROBE_NEEDEDHARD = 2;
    public static final int PROBE_NEEDEDFOREXTENDED = 3;
    public static int needsProbe = PROBE_NEEDEDFOREXTENDED;

    public static boolean extendedInMain = false;
    public static NumberFormat rfFormat = NumberFormat.COMPACT;
    public static NumberFormat tankFormat = NumberFormat.COMPACT;

    // Chest related settings
    public static int showSmallChestContentsWithoutSneaking = 0;
    public static int showItemDetailThresshold = 4;
    public static String[] showContentsWithoutSneaking = { "storagedrawers:basicDrawers", "storagedrawersextra:extra_drawers" };
    public static String[] dontShowContentsUnlessSneaking = {};
    public static String[] dontSendNBT = { };

    private static Set<ResourceLocation> inventoriesToShow = null;
    private static Set<ResourceLocation> inventoriesToNotShow = null;
    private static Set<ResourceLocation> dontSendNBTSet = null;


    private static int leftX = 0;
    private static int topY = 0;
    private static int rightX = -1;
    private static int bottomY = -1;

    public static int showBreakProgress = 1;    // 0 == off, 1 == bar, 2 == text
    public static boolean harvestStyleVanilla = true;

    public static int chestContentsBorderColor = 0xff006699;
    public static int probeButtonColor = 0xFF404040;
    public static int probeProgressColor = 0xff990000;
    public static int probeProgressAltColor = 0xff550000;
    public static int probeProgressBorderColor = 0;
    public static int probeProgressBackgroundColor = 0xff000000;
    public static boolean probeProgressGradient = false;

    private static int boxBorderColor = 0xff999999;
    private static int boxFillColor = 0x55006699;
    private static int boxThickness = 2;


    public static int rfbarFilledColor = 0xffdd0000;
    public static int rfbarAlternateFilledColor = 0xff430000;
    public static int rfbarBorderColor = 0xff555555;

    public static int tankbarFilledColor = 0xff0000dd;
    public static int tankbarAlternateFilledColor = 0xff000043;
    public static int tankbarBorderColor = 0xff555555;
    public static int probeNoteStackSize = 1;

    public static Map<TextStyleClass, String> defaultTextStyleClasses = new HashMap<>();
    public static Map<TextStyleClass, String> textStyleClasses;

    static {
        defaultTextStyleClasses.put(NAME, "white");
        defaultTextStyleClasses.put(MODNAME, "blue,italic");
        defaultTextStyleClasses.put(ERROR, "red,bold");
        defaultTextStyleClasses.put(WARNING, "yellow");
        defaultTextStyleClasses.put(OK, "green");
        defaultTextStyleClasses.put(INFO, "white");
        defaultTextStyleClasses.put(INFOIMP, "blue");
        defaultTextStyleClasses.put(OBSOLETE, "gray,strikethrough");
        defaultTextStyleClasses.put(LABEL, "gray");
        defaultTextStyleClasses.put(PROGRESS, "white");
        textStyleClasses = new HashMap<>(defaultTextStyleClasses);
    }



    private static IOverlayStyle defaultOverlayStyle;
    @Getter private static final ProbeConfig defaultConfig = new ProbeConfig();
    @Getter @Setter private static IProbeConfig realConfig;

    public static void init(Configuration cfg) {
        needsProbe = cfg.getInt("needsProbe", CATEGORY_THEONEPROBE, needsProbe, 0, 3, "Is the probe needed to show the tooltip? 0 = no, 1 = yes, 2 = yes and clients cannot override, 3 = probe needed for extended info only");
        extendedInMain = cfg.getBoolean("extendedInMain", CATEGORY_THEONEPROBE, extendedInMain, "If true the probe will automatically show extended information if it is in your main hand (so not required to sneak)");
        defaultConfig.setRFMode(cfg.getInt("showRF", CATEGORY_THEONEPROBE + "." + SUBCATEGORY_SHOW, defaultConfig.getRFMode(), 0, 2, "How to display RF: 0 = do not show, 1 = show in a bar, 2 = show as text"));
        defaultConfig.setTankMode(cfg.getInt("showTank", CATEGORY_THEONEPROBE + "." + SUBCATEGORY_SHOW, defaultConfig.getTankMode(), 0, 2, "How to display tank contents: 0 = do not show, 1 = show in a bar, 2 = show as text"));
        int fmt = cfg.getInt("rfFormat", CATEGORY_THEONEPROBE, rfFormat.ordinal(), 0, 2, "Format for displaying RF: 0 = full, 1 = compact, 2 = comma separated");
        rfFormat = NumberFormat.values()[fmt];
        fmt = cfg.getInt("tankFormat", CATEGORY_THEONEPROBE, tankFormat.ordinal(), 0, 2, "Format for displaying tank contents: 0 = full, 1 = compact, 2 = comma separated");
        tankFormat = NumberFormat.values()[fmt];
        initDefaultConfig(cfg);

        rfbarFilledColor = parseColor(cfg.getString("rfbarFilledColor", CATEGORY_THEONEPROBE, Integer.toHexString(rfbarFilledColor), "Color for the RF bar"));
        rfbarAlternateFilledColor = parseColor(cfg.getString("rfbarAlternateFilledColor", CATEGORY_THEONEPROBE, Integer.toHexString(rfbarAlternateFilledColor), "Alternate color for the RF bar"));
        rfbarBorderColor = parseColor(cfg.getString("rfbarBorderColor", CATEGORY_THEONEPROBE, Integer.toHexString(rfbarBorderColor), "Color for the RF bar border"));
        tankbarFilledColor = parseColor(cfg.getString("tankbarFilledColor", CATEGORY_THEONEPROBE, Integer.toHexString(tankbarFilledColor), "Color for the tank bar"));
        tankbarAlternateFilledColor = parseColor(cfg.getString("tankbarAlternateFilledColor", CATEGORY_THEONEPROBE, Integer.toHexString(tankbarAlternateFilledColor), "Alternate color for the tank bar"));
        tankbarBorderColor = parseColor(cfg.getString("tankbarBorderColor", CATEGORY_THEONEPROBE, Integer.toHexString(tankbarBorderColor), "Color for the tank bar border"));
        probeNoteStackSize = cfg.getInt("probeNoteStackSize", CATEGORY_THEONEPROBE, tankFormat.ordinal(), 1, 64, "Stack size of the Readme note");
        showItemDetailThresshold = cfg.getInt("showItemDetailThresshold", CATEGORY_THEONEPROBE + "." + SUBCATEGORY_SHOW, showItemDetailThresshold, 0, 20, "If the number of items in an inventory is lower or equal then this number then more info is shown");
        showSmallChestContentsWithoutSneaking = cfg.getInt("showSmallChestContentsWithoutSneaking", CATEGORY_THEONEPROBE + "." + SUBCATEGORY_SHOW, showSmallChestContentsWithoutSneaking, 0, 1000, "The maximum amount of slots (empty or not) to show without sneaking");
        showContentsWithoutSneaking = cfg.getStringList("showContentsWithoutSneaking", CATEGORY_THEONEPROBE + "." + SUBCATEGORY_SHOW, showContentsWithoutSneaking, "A list of blocks for which we automatically show chest contents even if not sneaking");
        dontShowContentsUnlessSneaking = cfg.getStringList("dontShowContentsUnlessSneaking", CATEGORY_THEONEPROBE, dontShowContentsUnlessSneaking, "A list of blocks for which we don't show chest contents automatically except if sneaking");
        dontSendNBT = cfg.getStringList("dontSendNBT", CATEGORY_THEONEPROBE, dontSendNBT, "A list of blocks not to send NBT over the network. This is useful for blocks that have HUGE NBT in their pickblock (itemstack)");

        setupStyleConfig(cfg);
    }

    private static void initDefaultConfig(Configuration cfg) {
        defaultConfig.showModName(IProbeConfig.ConfigMode.values()[cfg.getInt("showModName", CATEGORY_THEONEPROBE + "." + SUBCATEGORY_SHOW, defaultConfig.getShowModName().ordinal(), 0, 2, "Show mod name (0 = not, 1 = always, 2 = sneak)")]);
        defaultConfig.showHarvestLevel(IProbeConfig.ConfigMode.values()[cfg.getInt("showHarvestLevel", CATEGORY_THEONEPROBE + "." + SUBCATEGORY_SHOW, defaultConfig.getShowHarvestLevel().ordinal(), 0, 2, "Show harvest level (0 = not, 1 = always, 2 = sneak)")]);
        defaultConfig.showCanBeHarvested(IProbeConfig.ConfigMode.values()[cfg.getInt("showCanBeHarvested", CATEGORY_THEONEPROBE + "." + SUBCATEGORY_SHOW, defaultConfig.getShowHarvestLevel().ordinal(), 0, 2, "Show if the block can be harvested (0 = not, 1 = always, 2 = sneak)")]);
        defaultConfig.showCropPercentage(IProbeConfig.ConfigMode.values()[cfg.getInt("showCropPercentage", CATEGORY_THEONEPROBE + "." + SUBCATEGORY_SHOW, defaultConfig.getShowCropPercentage().ordinal(), 0, 2, "Show the growth level of crops (0 = not, 1 = always, 2 = sneak)")]);
        defaultConfig.showChestContents(IProbeConfig.ConfigMode.values()[cfg.getInt("showChestContents", CATEGORY_THEONEPROBE + "." + SUBCATEGORY_SHOW, defaultConfig.getShowChestContents().ordinal(), 0, 2, "Show chest contents (0 = not, 1 = always, 2 = sneak)")]);
        defaultConfig.showChestContentsDetailed(IProbeConfig.ConfigMode.values()[cfg.getInt("showChestContentsDetailed", CATEGORY_THEONEPROBE + "." + SUBCATEGORY_SHOW, defaultConfig.getShowChestContentsDetailed().ordinal(), 0, 2, "Show chest contents in detail (0 = not, 1 = always, 2 = sneak), used only if number of items is below 'showItemDetailThresshold'")]);
        defaultConfig.showRedstone(IProbeConfig.ConfigMode.values()[cfg.getInt("showRedstone", CATEGORY_THEONEPROBE + "." + SUBCATEGORY_SHOW, defaultConfig.getShowRedstone().ordinal(), 0, 2, "Show redstone (0 = not, 1 = always, 2 = sneak)")]);
        defaultConfig.showMobHealth(IProbeConfig.ConfigMode.values()[cfg.getInt("showMobHealth", CATEGORY_THEONEPROBE + "." + SUBCATEGORY_SHOW, defaultConfig.getShowMobHealth().ordinal(), 0, 2, "Show mob health (0 = not, 1 = always, 2 = sneak)")]);
        defaultConfig.showMobGrowth(IProbeConfig.ConfigMode.values()[cfg.getInt("showMobGrowth", CATEGORY_THEONEPROBE + "." + SUBCATEGORY_SHOW, defaultConfig.getShowMobGrowth().ordinal(), 0, 2, "Show time to adulthood for baby mobs (0 = not, 1 = always, 2 = sneak)")]);
        defaultConfig.showMobPotionEffects(IProbeConfig.ConfigMode.values()[cfg.getInt("showMobPotionEffects", CATEGORY_THEONEPROBE + "." + SUBCATEGORY_SHOW, defaultConfig.getShowMobPotionEffects().ordinal(), 0, 2, "Show mob potion effects (0 = not, 1 = always, 2 = sneak)")]);
        defaultConfig.showLeverSetting(IProbeConfig.ConfigMode.values()[cfg.getInt("showLeverSetting", CATEGORY_THEONEPROBE + "." + SUBCATEGORY_SHOW, defaultConfig.getShowLeverSetting().ordinal(), 0, 2, "Show lever/comparator/repeater settings (0 = not, 1 = always, 2 = sneak)")]);
        defaultConfig.showTankSetting(IProbeConfig.ConfigMode.values()[cfg.getInt("showTankSetting", CATEGORY_THEONEPROBE + "." + SUBCATEGORY_SHOW, defaultConfig.getShowTankSetting().ordinal(), 0, 2, "Show tank setting (0 = not, 1 = always, 2 = sneak)")]);
        defaultConfig.showBrewStandSetting(IProbeConfig.ConfigMode.values()[cfg.getInt("showBrewStandSetting", CATEGORY_THEONEPROBE + "." + SUBCATEGORY_SHOW, defaultConfig.getShowBrewStandSetting().ordinal(), 0, 2, "Show brewing stand setting (0 = not, 1 = always, 2 = sneak)")]);
        defaultConfig.showMobSpawnerSetting(IProbeConfig.ConfigMode.values()[cfg.getInt("showMobSpawnerSetting", CATEGORY_THEONEPROBE + "." + SUBCATEGORY_SHOW, defaultConfig.getShowMobSpawnerSetting().ordinal(), 0, 2, "Show mob spawner setting (0 = not, 1 = always, 2 = sneak)")]);
        defaultConfig.showAnimalOwnerSetting(IProbeConfig.ConfigMode.values()[cfg.getInt("showAnimalOwnerSetting", CATEGORY_THEONEPROBE + "." + SUBCATEGORY_SHOW, defaultConfig.getAnimalOwnerSetting().ordinal(), 0, 2, "Show animal owner setting (0 = not, 1 = always, 2 = sneak)")]);
        defaultConfig.showHorseStatSetting(IProbeConfig.ConfigMode.values()[cfg.getInt("showHorseStatSetting", CATEGORY_THEONEPROBE + "." + SUBCATEGORY_SHOW, defaultConfig.getHorseStatSetting().ordinal(), 0, 2, "Show horse stats setting (0 = not, 1 = always, 2 = sneak)")]);
        defaultConfig.showSilverfish(IProbeConfig.ConfigMode.values()[cfg.getInt("showSilverfish",CATEGORY_THEONEPROBE + "." + SUBCATEGORY_SHOW,defaultConfig.getShowSilverfish().ordinal(),0,2,"Reveal monster eggs (0 = not, 1 = always, 2 = sneak)")]);

    }

    public static void setProbeNeeded(int probeNeeded) {
        Configuration cfg = mainConfig;
        Config.needsProbe = probeNeeded;
        cfg.get(CATEGORY_THEONEPROBE, "needsProbe", probeNeeded).set(probeNeeded);
        cfg.save();
    }


    public static void setupStyleConfig(Configuration cfg) {
        leftX = cfg.getInt("boxLeftXOffset", CATEGORY_CLIENT + "." + SUBCATEGORY_OFFSETS, leftX, -1, 10000, "The left offset for the probe");
        rightX = cfg.getInt("boxRightXOffset", CATEGORY_CLIENT + "." + SUBCATEGORY_OFFSETS, rightX, -1, 10000, "The right offset for the probe");
        topY = cfg.getInt("boxTopYOffset", CATEGORY_CLIENT + "." + SUBCATEGORY_OFFSETS, topY, -1, 10000, "The top offset for the probe");
        bottomY = cfg.getInt("boxBottomYOffset", CATEGORY_CLIENT + "." + SUBCATEGORY_OFFSETS, bottomY, -1, 10000, "The bottom offset for the probe");
        boxBorderColor = parseColor(cfg.getString("boxBorderColor", CATEGORY_CLIENT, Integer.toHexString(boxBorderColor), "Color of the border of the box (0 to disable)"));
        boxFillColor = parseColor(cfg.getString("boxFillColor", CATEGORY_CLIENT, Integer.toHexString(boxFillColor), "Color of the box (0 to disable)"));
        boxThickness = cfg.getInt("boxThickness", CATEGORY_CLIENT, boxThickness, 0, 20, "Thickness of the border of the box (0 to disable)");
        probeButtonColor = parseColor(cfg.getString("probeButtonColor", CATEGORY_CLIENT, Integer.toHexString(probeButtonColor), "Color of the buttons in the probe note (0 to disable)"));
        probeProgressColor = parseColor(cfg.getString("probeProgressColor", CATEGORY_CLIENT, Integer.toHexString(probeProgressColor), "Color of the progress bar (0 to disable)"));
        probeProgressAltColor = parseColor(cfg.getString("probeProgressAltColor", CATEGORY_CLIENT, Integer.toHexString(probeProgressAltColor), "Alt color of the progress bar (0 to disable)"));
        probeProgressBorderColor = parseColor(cfg.getString("probeProgressBorderColor", CATEGORY_CLIENT, Integer.toHexString(probeProgressBorderColor), "Color of the border of the progress bar (0 to disable)"));
        probeProgressBackgroundColor = parseColor(cfg.getString("probeProgressBackgroundColor", CATEGORY_CLIENT, Integer.toHexString(probeProgressBackgroundColor), "Color of the background of the progress bar (0 to disable)"));
        probeProgressGradient = cfg.getBoolean("probeProgressGradient", CATEGORY_CLIENT, probeProgressGradient, "Use a gradient instead of alternating colors in solid blocks");
        chestContentsBorderColor = parseColor(cfg.getString("chestContentsBorderColor", CATEGORY_CLIENT, Integer.toHexString(chestContentsBorderColor), "Color of the border of the chest contents box (0 to disable)"));
        showBreakProgress = cfg.getInt("showBreakProgress", CATEGORY_CLIENT, showBreakProgress, 0, 2, "0 means don't show break progress, 1 is show as bar, 2 is show as text");
        harvestStyleVanilla = cfg.getBoolean("harvestStyleVanilla", CATEGORY_CLIENT, harvestStyleVanilla, "true means shows harvestability with vanilla style icons");

        Map<TextStyleClass, String> newformat = new HashMap<>();
        for (TextStyleClass styleClass : textStyleClasses.keySet()) {
            String style = cfg.getString("textStyle" + styleClass.getReadableName(),
                    CATEGORY_CLIENT + "." + SUBCATEGORY_TEXT, textStyleClasses.get(styleClass),
                    "Text style. Use a comma delimited list with colors like: 'red', 'green', 'blue', ... or style codes like 'underline', 'bold', 'italic', 'strikethrough', ...");
            newformat.put(styleClass, style);
        }
        textStyleClasses = newformat;

        extendedInMain = cfg.getBoolean("extendedInMain", CATEGORY_CLIENT, extendedInMain, "If true the probe will automatically show extended information if it is in your main hand (so not required to sneak)");
    }

    public static void setTextStyle(TextStyleClass styleClass, String style) {
        Configuration cfg = mainConfig;
        Config.textStyleClasses.put(styleClass, style);
        cfg.get(CATEGORY_CLIENT + "." + SUBCATEGORY_TEXT, "textStyle" + styleClass.getReadableName(), style).set(style);
        cfg.save();
    }

    public static void setExtendedInMain(boolean extendedInMain) {
        Configuration cfg = mainConfig;
        Config.extendedInMain = extendedInMain;
        cfg.get(CATEGORY_CLIENT, "extendedInMain", extendedInMain).set(extendedInMain);
        cfg.save();
    }

    public static void setPos(int leftx, int topy, int rightx, int bottomy) {
        Configuration cfg = mainConfig;
        Config.leftX = leftx;
        Config.topY = topy;
        Config.rightX = rightx;
        Config.bottomY = bottomy;
        cfg.get(CATEGORY_CLIENT + "." + SUBCATEGORY_OFFSETS, "boxLeftXOffset", leftx).set(leftx);
        cfg.get(CATEGORY_CLIENT + "." + SUBCATEGORY_OFFSETS, "boxRightXOffset", rightx).set(rightx);
        cfg.get(CATEGORY_CLIENT + "." + SUBCATEGORY_OFFSETS, "boxTopYOffset", topy).set(topy);
        cfg.get(CATEGORY_CLIENT + "." + SUBCATEGORY_OFFSETS, "boxBottomYOffset", bottomy).set(bottomy);
        cfg.save();
        updateDefaultOverlayStyle();
    }

    public static boolean getHarvestStyleVanilla(){
        return harvestStyleVanilla;
    }
    public static void setBoxStyle(int thickness, int borderColor, int fillcolor) {
        Configuration cfg = mainConfig;
        boxThickness = thickness;
        boxBorderColor = borderColor;
        boxFillColor = fillcolor;
        cfg.get(CATEGORY_CLIENT, "boxThickness", thickness).set(thickness);
        cfg.get(CATEGORY_CLIENT, "boxBorderColor", Integer.toHexString(borderColor)).set(Integer.toHexString(borderColor));
        cfg.get(CATEGORY_CLIENT, "boxFillColor", Integer.toHexString(fillcolor)).set(Integer.toHexString(fillcolor));
        cfg.save();
        updateDefaultOverlayStyle();
    }

    private static String configToTextFormat(String input) {
        if ("context".equals(input)) {
            return "context";
        }
        StringBuilder builder = new StringBuilder();
        String[] splitted = StringUtils.split(input, ',');
        for (String s : splitted) {
            TextFormatting format = TextFormatting.getValueByName(s);
            if (format != null) {
                builder.append(format);
            }
        }
        return builder.toString();
    }

    public static String getTextStyle(TextStyleClass styleClass) {
        if (textStyleClasses.containsKey(styleClass)) {
            return configToTextFormat(textStyleClasses.get(styleClass));
        }
        return "";
    }

    private static int parseColor(String col) {
        try {
            return (int) Long.parseLong(col, 16);
        } catch (NumberFormatException e) {
            System.out.println("Config.parseColor");
            return 0;
        }
    }

    public static void updateDefaultOverlayStyle() {
        defaultOverlayStyle = new DefaultOverlayStyle()
                .borderThickness(boxThickness)
                .borderColor(boxBorderColor)
                .boxColor(boxFillColor)
                .location(leftX, rightX, topY, bottomY);
    }

    public static IOverlayStyle getDefaultOverlayStyle() {
        if (defaultOverlayStyle == null) {
            updateDefaultOverlayStyle();
        }
        return defaultOverlayStyle;
    }

    public static Set<ResourceLocation> getInventoriesToShow() {
        if (inventoriesToShow == null) {
            inventoriesToShow = new HashSet<>();
            for (String s : showContentsWithoutSneaking) {
                inventoriesToShow.add(new ResourceLocation(s));
            }
        }
        return inventoriesToShow;
    }

    public static Set<ResourceLocation> getInventoriesToNotShow() {
        if (inventoriesToNotShow == null) {
            inventoriesToNotShow = new HashSet<>();
            for (String s : dontShowContentsUnlessSneaking) {
                inventoriesToNotShow.add(new ResourceLocation(s));
            }
        }
        return inventoriesToNotShow;
    }

    public static Set<ResourceLocation> getDontSendNBTSet() {
        if (dontSendNBTSet == null) {
            dontSendNBTSet = new HashSet<>();
            for (String s : dontSendNBT) {
                dontSendNBTSet.add(new ResourceLocation(s));
            }
        }
        return dontSendNBTSet;
    }

    public static void init() {
        mainConfig = new Configuration(new File(CommonProxy.modConfigDir.getPath(), TheOneProbe.MODID + ".cfg"));
        Configuration cfg = mainConfig;
        try {
            cfg.load();
            cfg.addCustomCategoryComment(CATEGORY_THEONEPROBE, "The One Probe Configuration");
            cfg.addCustomCategoryComment(CATEGORY_PROVIDERS, "Provider Configuration");
            cfg.addCustomCategoryComment(CATEGORY_CLIENT, "Clientside Settings");
            init(cfg);
        } catch (Exception e1) {
            CommonProxy.getLogger().log(Level.ERROR, "Problem loading config file!", e1);
        }
    }
}
