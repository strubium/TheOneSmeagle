package mcjty.theoneprobe.compat.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.impl.ConfigHandler;

import java.util.HashMap;
import java.util.Set;

public class WailaConfigHandler implements IWailaConfigHandler {

    public static final WailaConfigHandler INSTANCE =
            new WailaConfigHandler();

    private WailaConfigHandler() {
    }

    @Override
    public Set<String> getModuleNames() {
        return ConfigHandler.instance().getModuleNames();
    }

    @Override
    public HashMap<String, String> getConfigKeys(String modName) {
        return ConfigHandler.instance().getConfigKeys(modName);
    }

    @Override
    public boolean getConfig(String key, boolean defaultValue) {
        return ConfigHandler.instance().getConfig(key, defaultValue);
    }

    @Override
    public boolean getConfig(String key) {
        return ConfigHandler.instance().getConfig(key);
    }
}
