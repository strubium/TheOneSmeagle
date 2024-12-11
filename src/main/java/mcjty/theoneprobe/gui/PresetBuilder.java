package mcjty.theoneprobe.gui;

import mcjty.theoneprobe.api.TextStyleClass;
import mcjty.theoneprobe.config.Config;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Builder to make a new preset, basically for GroovyScript compact
 *
 * @since 10/15/2024
 * @author strubium
 */
@SuppressWarnings("unused") //This is suppressed because most of these are added for groovy compact
public class PresetBuilder {

    private static final List<Preset> presets = new ArrayList<>();

    private String name;
    private int boxBorderColor = 0xFFFFFF; // default value
    private int boxFillColor = 0x000000;   // default value
    private int boxThickness = 1;           // default value
    private int boxOffset = 0;              // default value
    private final Map<TextStyleClass, String> textStyleClasses = new HashMap<>();

    public PresetBuilder() {}

    /**
     * Set the Name of the preset, can be a translation key
     */
    public PresetBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public PresetBuilder setBoxBorderColor(int color) {
        this.boxBorderColor = color;
        return this;
    }

    public PresetBuilder setBoxFillColor(int color) {
        this.boxFillColor = color;
        return this;
    }

    public PresetBuilder setBoxThickness(int thickness) {
        this.boxThickness = thickness;
        return this;
    }

    public PresetBuilder setBoxOffset(int offset) {
        this.boxOffset = offset;
        return this;
    }

    public PresetBuilder addTextStyleClass(TextStyleClass styleClass, String styleName) {
        this.textStyleClasses.put(styleClass, styleName);
        return this;
    }

    // Build method to construct the Preset object and add it to the static presets list
    public Preset build() {
        // Create the new Preset object
        Preset preset = new Preset(
                name,
                boxBorderColor,
                boxFillColor,
                boxThickness,
                boxOffset,
                textStyleClasses.entrySet()
                        .stream()
                        .map(entry -> Pair.of(entry.getKey(), entry.getValue())).toArray(Pair[]::new)
        );

        // Add the newly created preset to the static list
        presets.add(preset);

        return preset;
    }

    /**
     * Applies the given preset configuration.
     *
     * @param preset The {@link Preset} object containing the configuration to apply.
     * @throws IllegalArgumentException if the preset is null.
     */
    public static void applyPreset(Preset preset) {
        if (preset == null) {
            throw new IllegalArgumentException("Preset cannot be null.");
        }

        // Apply text styles from the preset
        for (Map.Entry<TextStyleClass, String> entry : Config.defaultTextStyleClasses.entrySet()) {
            Config.setTextStyle(entry.getKey(), entry.getValue());
        }

        // Apply box styles from the preset
        Config.setBoxStyle(
                preset.getBoxThickness(),
                preset.getBoxBorderColor(),
                preset.getBoxFillColor()
        );

        // Apply text styles from the preset
        for (Map.Entry<TextStyleClass, String> entry : preset.getTextStyleClasses().entrySet()) {
            Config.setTextStyle(entry.getKey(), entry.getValue());
        }
    }


    /**
     * Get the name of the preset.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the box border color of the preset.
     */
    public int getBoxBorderColor() {
        return boxBorderColor;
    }

    /**
     * Get the box fill color of the preset.
     */
    public int getBoxFillColor() {
        return boxFillColor;
    }

    /**
     * Get the box thickness of the preset.
     */
    public int getBoxThickness() {
        return boxThickness;
    }

    /**
     * Get the box offset of the preset.
     */
    public int getBoxOffset() {
        return boxOffset;
    }

    /**
     * Get the text style classes of the preset.
     */
    public Map<TextStyleClass, String> getTextStyleClasses() {
        return new HashMap<>(textStyleClasses); // Return a copy to avoid external modifications
    }

    /**
     * Get the ArrayList containing all the presets
     */
    public static List<Preset> getPresets() {
        return presets;
    }

    /**
     * Clear the ArrayList containing all the presets
     */
    public static void removeAll() {
        presets.clear();
    }

    /**
     * Remove a preset by name.
     *
     * @param name The name of the preset to remove.
     * @return true if the preset was removed, false if not found.
     */
    public static boolean removePresetByName(String name) {
        return presets.removeIf(preset -> preset.getName().equals(name));
    }

    public static void addDefaultPresets() {
        presets.add(new Preset("Default", 0xff999999, 0x55006699, 2, 0));
        presets.add(new Preset("WAILA", 0xff4503d0, 0xff000000, 1, 1));
        presets.add(new Preset("Jade", 0xff323331, 0xff20261a, 1, 1));
        presets.add(new Preset("Fully transparent", 0x00000000, 0x00000000, 0, 0));
        presets.add(new Preset("Black & White", 0xffffffff, 0xff000000, 2, 0,
                Pair.of(TextStyleClass.MODNAME, "white,italic"),
                Pair.of(TextStyleClass.NAME, "white,bold"),
                Pair.of(TextStyleClass.INFO, "white"),
                Pair.of(TextStyleClass.INFOIMP, "white,bold"),
                Pair.of(TextStyleClass.WARNING, "white"),
                Pair.of(TextStyleClass.ERROR, "white,underline"),
                Pair.of(TextStyleClass.OBSOLETE, "white,strikethrough"),
                Pair.of(TextStyleClass.LABEL, "white,bold"),
                Pair.of(TextStyleClass.OK, "white"),
                Pair.of(TextStyleClass.PROGRESS, "white")
        ));
        presets.add(new Preset("Crazy!", 0xff00ff00, 0x55ff0000, 2, 0,
                Pair.of(TextStyleClass.MODNAME, "green"),
                Pair.of(TextStyleClass.NAME, "yellow,bold"),
                Pair.of(TextStyleClass.INFO, "cyan,bold"),
                Pair.of(TextStyleClass.INFOIMP, "magenta,bold"),
                Pair.of(TextStyleClass.WARNING, "orange,bold"),
                Pair.of(TextStyleClass.ERROR, "red,bold"),
                Pair.of(TextStyleClass.OBSOLETE, "gray,bold"),
                Pair.of(TextStyleClass.LABEL, "blue,bold"),
                Pair.of(TextStyleClass.OK, "green,bold"),
                Pair.of(TextStyleClass.PROGRESS, "white,bold")
        ));
        presets.add(new Preset("Soft Pastels", 0xffe0bbff, 0x00000000, 1, 1,
                Pair.of(TextStyleClass.MODNAME, "pink,bold,italic")
        ));
        presets.add(new Preset("Ocean Blue", 0xff003366, 0x556699cc, 2, 0,
                Pair.of(TextStyleClass.MODNAME, "cyan"),
                Pair.of(TextStyleClass.NAME, "light_blue,bold"),
                Pair.of(TextStyleClass.INFO, "white"),
                Pair.of(TextStyleClass.INFOIMP, "white,bold"),
                Pair.of(TextStyleClass.WARNING, "yellow,bold"),
                Pair.of(TextStyleClass.ERROR, "red,bold"),
                Pair.of(TextStyleClass.OBSOLETE, "gray,bold,italic"),
                Pair.of(TextStyleClass.LABEL, "aqua,bold"),
                Pair.of(TextStyleClass.OK, "green,bold"),
                Pair.of(TextStyleClass.PROGRESS, "white,bold")
        ));
    }
}
