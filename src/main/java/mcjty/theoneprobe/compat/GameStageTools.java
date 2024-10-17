package mcjty.theoneprobe.compat;

import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.player.EntityPlayer;

public class GameStageTools {
    public static final String PROBE_NORMAL = "probe_normal";
    public static final String PROBE_EXTENDED = "probe_normal";
    public static final String PROBE_DEBUG = "probe_normal";


    public boolean playerHasStage(EntityPlayer player, String stage) {
        return GameStageHelper.hasStage(player, stage);
    }
}
