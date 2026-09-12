package mcjty.theoneprobe.compat.waila;

public class WailaTools {

    private static final WailaProbeInfoProvider wailaProbeInfoProvider =
            new WailaProbeInfoProvider();

    public static WailaProbeInfoProvider getWailaProbeInfoProvider() {
        return wailaProbeInfoProvider;
    }
}
