package mcjty.theoneprobe.gui;

import lombok.Getter;
import mcjty.theoneprobe.api.TextStyleClass;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Preset {
    private final String name;
    private final int boxBorderColor;
    private final int boxFillColor;
    private final int boxThickness;
    private final int boxOffset;
    private final Map<TextStyleClass, String> textStyleClasses;

    /**
     * Constructs a Preset with specified parameters.
     *
     * @param name           The name of the preset.
     * @param boxBorderColor The color of the border of the box.
     * @param boxFillColor   The color to fill the box.
     * @param boxThickness   The thickness of the box border.
     * @param boxOffset      The offset of the box.
     * @param styles         Pairs of {@link TextStyleClass} and their associated style names.
     */
    @SafeVarargs
    public Preset(String name, int boxBorderColor, int boxFillColor, int boxThickness, int boxOffset, Pair<TextStyleClass, String>... styles) {
        this.name = name;
        this.boxBorderColor = boxBorderColor;
        this.boxFillColor = boxFillColor;
        this.boxThickness = boxThickness;
        this.boxOffset = boxOffset;
        textStyleClasses = new HashMap<>();
        for (Pair<TextStyleClass, String> style : styles) {
            textStyleClasses.put(style.getLeft(), style.getRight());
        }
    }

    /**
     * Gets all TextStyleClass keys in the preset.
     *
     * @return A collection of TextStyleClass keys.
     */
    public Collection<TextStyleClass> getTextStyleClassKeys() {
        return textStyleClasses.keySet();
    }

    /**
     * Gets the style name associated with a given TextStyleClass.
     *
     * @param styleClass The TextStyleClass to look up.
     * @return The associated style name, or null if not found.
     */
    public String getStyleName(TextStyleClass styleClass) {
        return textStyleClasses.get(styleClass);
    }
}
