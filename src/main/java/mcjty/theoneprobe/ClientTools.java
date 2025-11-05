package mcjty.theoneprobe;

import mcjty.theoneprobe.api.TextStyleClass;
import mcjty.theoneprobe.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mcjty.theoneprobe.api.IProbeInfo.ENDLOC;
import static mcjty.theoneprobe.api.IProbeInfo.STARTLOC;

@SideOnly(Side.CLIENT)
public class ClientTools {

    /**
     * Constant for {@link Minecraft#getMinecraft()}
     */
    public static final Minecraft MC = Minecraft.getMinecraft();

    private static final Pattern TRANSLATION_PATTERN = Pattern.compile(
            Pattern.quote(STARTLOC) + "(.*?)" + Pattern.quote(ENDLOC)
    );

    private static final int MAX_CACHE_SIZE = 512;
    private static final Map<String, String> STYLIFY_CACHE = new LinkedHashMap<String, String>(MAX_CACHE_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };

    /**
     * Applies translation markers and text styles to a string.
     *
     * @param input Original string containing markers and style tokens.
     * @return Formatted string ready for rendering.
     */
    public static String stylifyString(String input) {
        if (StringUtils.isEmpty(input)) {
            return "";
        }

        // Check cache
        String cached = STYLIFY_CACHE.get(input);
        if (cached != null) {
            return cached;
        }

        StringBuilder sb = new StringBuilder(input);

        // 1. Handle translation markers like ${STARTLOC}...${ENDLOC}
        sb.setLength(0);
        sb.append(applyTranslations(input));

        // 2. Handle text style markers like {=gold}
        applyTextStyles(sb);

        String result = sb.toString();

        // ✅ Save to cache
        STYLIFY_CACHE.put(input, result);

        return result;
    }

    private static String applyTranslations(String text) {
        Matcher matcher = TRANSLATION_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            String translated = I18n.format(key).trim();
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(translated));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static void applyTextStyles(StringBuilder sb) {
        String text = sb.toString();
        if (!text.contains("{=")) {
            return;
        }

        Set<TextStyleClass> needsContext = EnumSet.noneOf(TextStyleClass.class);
        TextStyleClass context = null;

        for (TextStyleClass styleClass : Config.textStyleClasses.keySet()) {
            String styleToken = styleClass.toString();
            if (text.contains(styleToken)) {
                String replacement = Config.getTextStyle(styleClass);
                if ("context".equals(replacement)) {
                    needsContext.add(styleClass);
                } else {
                    if (context == null) {
                        context = styleClass;
                    }
                    text = StringUtils.replace(text, styleToken, replacement);
                }
            }
        }

        if (context != null && !needsContext.isEmpty()) {
            String contextReplacement = Config.getTextStyle(context);
            for (TextStyleClass styleClass : needsContext) {
                text = StringUtils.replace(text, styleClass.toString(), contextReplacement);
            }
        }

        sb.setLength(0);
        sb.append(text);
    }
}
