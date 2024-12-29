package mcjty.theoneprobe.apiimpl.client;

import mcjty.theoneprobe.ClientTools;
import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.IItemStyle;
import mcjty.theoneprobe.rendering.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ElementItemStackRender {

    public static void render(ItemStack itemStack, IItemStyle style, int x, int y) {
        RenderItem itemRender = ClientTools.mc.getRenderItem();
        if (!itemStack.isEmpty()) {
            int size = itemStack.getCount();
            String amount;
            if (size <= 1) {
                amount = "";
            } else if (size < 100000) {
                amount = String.valueOf(size);
            } else if (size < 1000000) {
                amount = size / 1000 + "k";
            } else if (size < 1000000000) {
                amount = size / 1000000 + "m";
            } else {
                amount = size / 1000000000 + "g";
            }

            if (!RenderHelper.renderItemStack(ClientTools.mc, itemRender, itemStack, x + (style.getWidth() - 18) / 2, y + (style.getHeight() - 18) / 2, amount)) {
                // There was a crash rendering this item
                RenderHelper.renderText(ClientTools.mc, x, y, TextFormatting.RED + "{*theoneprobe.probe.error_indicator*} " + itemStack.getDisplayName());
            }
        }
    }

}
