package mcjty.theoneprobe;

import mcjty.theoneprobe.api.IProbeConfig;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.apache.commons.lang3.text.WordUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static mcjty.theoneprobe.api.IProbeConfig.ConfigMode.EXTENDED;
import static mcjty.theoneprobe.api.IProbeConfig.ConfigMode.NORMAL;

public class Tools {

    private final static Map<String, String> modNamesForIds = new HashMap<>();

    private static void init() {
        Map<String, ModContainer> modMap = Loader.instance().getIndexedModList();
        for (Map.Entry<String, ModContainer> modEntry : modMap.entrySet()) {
            String lowercaseId = modEntry.getKey().toLowerCase(Locale.ENGLISH);
            String modName = modEntry.getValue().getName();
            modNamesForIds.put(lowercaseId, modName);
        }
    }

    /**
     * Retrieves the mod name for a given block.
     *
     * @param block the block for which to get the mod name
     * @return the mod name associated with the block
     */
    public static String getModName(Block block) {
        if (modNamesForIds.isEmpty()) {
            init();
        }
        ResourceLocation itemResourceLocation = block.getRegistryName();
        String modId = Objects.requireNonNull(itemResourceLocation).getNamespace();
        String lowercaseModId = modId.toLowerCase(Locale.ENGLISH);
        String modName = modNamesForIds.get(lowercaseModId);
        if (modName == null) {
            modName = WordUtils.capitalize(modId);
            modNamesForIds.put(lowercaseModId, modName);
        }
        return modName;
    }

    /**
     * Retrieves the mod name for a given entity.
     *
     * @param entity the entity for which to get the mod name
     * @return the mod name associated with the entity or "Minecraft" if not found
     */
    public static String getModName(Entity entity) {
        if (modNamesForIds.isEmpty()) {
            init();
        }
        EntityRegistry.EntityRegistration modSpawn = EntityRegistry.instance().lookupModSpawn(entity.getClass(), true);
        if (modSpawn == null) {
            return "Minecraft";
        }
        ModContainer container = modSpawn.getContainer();
        if (container == null) {
            return "Minecraft";
        }
        String modId = container.getModId();
        String lowercaseModId = modId.toLowerCase(Locale.ENGLISH);
        return modNamesForIds.computeIfAbsent(lowercaseModId, key -> WordUtils.capitalize(modId));
    }

    /**
     * Determines if information should be displayed based on the probe mode and configuration mode.
     *
     * @param mode the current probe mode
     * @param cfg  the IProbeConfig.ConfigMode
     * @return true if information should be shown, false otherwise
     */
    public static boolean show(ProbeMode mode, IProbeConfig.ConfigMode cfg) {
        return cfg == NORMAL || (cfg == EXTENDED && mode == ProbeMode.EXTENDED);
    }

    /**
     * Retrieves the screen width in pixels.
     *
     * @return The screen width in pixels.
     */
    public static int getScreenWidth() {
        ScaledResolution resolution = new ScaledResolution(ClientTools.mc);
        return resolution.getScaledWidth();
    }

    /**
     * Capitalizes a string
     *
     * @return The Capitalized string
     */
    public static String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

}
