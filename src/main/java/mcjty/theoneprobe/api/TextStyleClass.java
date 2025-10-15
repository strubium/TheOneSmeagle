package mcjty.theoneprobe.api;

import lombok.Getter;

/**
 * Represent a style for text. This style is configurable by the user and used server-side.
 * Use it like you would use a TextFormatting in your strings. i.e.:
 * probeInfo.text(TextStyleClass.ERROR + "Error! World will explode in 5 seconds!");
 */
@Getter
public enum TextStyleClass {
    /**Name of the mod*/
    MODNAME("m", "ModName"),
    /**Name of the block or entity*/
    NAME("n", "Name"),
    /**General info, neutral*/
    INFO("i", "Info"),
    /**General info, important*/
    INFOIMP("I", "InfoImportant"),
    /**Warning, something is not ready (not mature), or missing stuff*/
    WARNING("w", "Warning"),
    /**Error, bad situation, out of power, things like that*/
    ERROR("e", "Error"),
    /**Obsolete, deprecated, old information*/
    OBSOLETE("O", "Obsolete"),
    /**A label, use the 'context' code to set the same as the style that follows*/
    LABEL("l", "Label"),
    /**Status ok*/
    OK("o", "Ok"),
    /** Progress rendering in case the bar is not used*/
    PROGRESS("p", "Progress");

    private final String code;
    private final String readableName;

    TextStyleClass(String code, String readableName) {
        this.code = code;
        this.readableName = readableName;
    }

    /**
     * Returns a string representation of the enum constant,
     * formatted as "{=code=}".
     *
     * @return String representation of the enum constant.
     */
    @Override
    public String toString() {
        return "{=" + code + "=}";
    }
}
