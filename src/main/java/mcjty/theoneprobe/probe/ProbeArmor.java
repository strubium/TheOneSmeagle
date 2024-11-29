package mcjty.theoneprobe.probe;

import mcjty.theoneprobe.TheOneProbe;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

import javax.annotation.ParametersAreNonnullByDefault;

import static mcjty.theoneprobe.items.ModItems.PROBETAG;


/**
 * A custom form of {@link ItemArmor} with an overlay for the probe
 *
 * @since 10/8/2024
 * @author strubium
 */
public class ProbeArmor extends ItemArmor {

    private final String baseTexture;

    public ProbeArmor(ArmorMaterial material, int renderIndex, EntityEquipmentSlot equipmentSlot, String baseTexture) {
        super(material, renderIndex, equipmentSlot);
        this.baseTexture = baseTexture;
    }

    @Override
    public boolean hasOverlay(ItemStack stack) {
        return true; // Enable the overlay for this armor
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        if ("overlay".equals(type)) {
            // Return the custom overlay texture (layer1)
            return TheOneProbe.MODID + ":textures/models/armor/probe_armor_overlay.png";
        }

        if (slot == EntityEquipmentSlot.HEAD) {
            return this.baseTexture;
        }

        // Call the parent method for layer0
        return super.getArmorTexture(stack, entity, slot, type);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (this.isInCreativeTab(tab)) {
            ItemStack stack = new ItemStack(this);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger(PROBETAG, 1);
            stack.setTagCompound(tag);
            subItems.add(stack);
        }
    }
}
