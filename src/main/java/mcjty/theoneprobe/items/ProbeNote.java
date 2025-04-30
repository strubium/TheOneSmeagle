package mcjty.theoneprobe.items;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.probe.ProbeBase;
import mcjty.theoneprobe.setup.proxy.GuiProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

public class ProbeNote extends ProbeBase {

    public ProbeNote() {
        super("probenote");
        setMaxStackSize(Config.probeNoteStackSize);
    }

    @Override
    @ParametersAreNonnullByDefault
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) {
            if (player.isSneaking()) {
                player.openGui(TheOneProbe.instance, GuiProxy.GUI_CONFIG, player.getEntityWorld(), (int) player.posX, (int) player.posY, (int) player.posZ);
            } else {
                player.openGui(TheOneProbe.instance, GuiProxy.GUI_NOTE, player.getEntityWorld(), (int) player.posX, (int) player.posY, (int) player.posZ);
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
