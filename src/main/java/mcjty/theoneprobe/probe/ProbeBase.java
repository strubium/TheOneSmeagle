package mcjty.theoneprobe.probe;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.setup.GuiProxy;
import mcjty.theoneprobe.setup.Registration;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The base of a probe, to simplify probe creation
 *
 * @since 10/11/2024
 * @author strubium
 */
public abstract class ProbeBase extends Item {

    public ProbeBase(String registryName, String unlocalizedName) {
        setUnlocalizedName(TheOneProbe.MODID + "." + unlocalizedName);
        setRegistryName(registryName);
        setMaxStackSize(1);
        setCreativeTab(TheOneProbe.tabProbe);
        Registration.addItem(this);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        // Check if we're on the client
        if (world.isRemote) {
            // This is the client side, we open the GUI here
            handleRightClickClient(world, player, hand, stack);
        } else {
            // Server-side logic goes here
            handleRightClickServer(world, player, hand, stack);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    /**
     * Client-side logic for right-click action. Override this method to open GUI on the client.
     */
    @SideOnly(Side.CLIENT)
    protected void handleRightClickClient(World world, EntityPlayer player, EnumHand hand, ItemStack stack) {
        // Open the GUI (client-side)
        player.openGui(TheOneProbe.instance, GuiProxy.GUI_CONFIG, player.getEntityWorld(),
                (int) player.posX, (int) player.posY, (int) player.posZ);
    }

    /**
     * Server-side logic for right-click action. Can be overridden by subclasses to add server logic.
     */
    protected void handleRightClickServer(World world, EntityPlayer player, EnumHand hand, ItemStack stack) {
        // Add server-specific logic here if needed (e.g., changing item states, etc.)
    }
}
