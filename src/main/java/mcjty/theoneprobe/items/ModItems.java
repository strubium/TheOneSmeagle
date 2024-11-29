package mcjty.theoneprobe.items;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.compat.BaubleTools;
import mcjty.theoneprobe.probe.ProbeArmor;
import mcjty.theoneprobe.setup.ModSetup;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

public class ModItems {
    public static CreativeProbe creativeProbe;
    public static Probe probe;
    public static Item diamondHelmetProbe;
    public static Item goldHelmetProbe;
    public static Item ironHelmetProbe;
    public static Item probeGoggles;
    public static ProbeNote probeNote;

    public static final String PROBETAG = TheOneProbe.MODID;

    public static void init() {
        int stepCount;
        if (ModSetup.baubles) {
             stepCount = 6;
        }
        else {
             stepCount = 5;
        }
        final ProgressManager.ProgressBar bar = ProgressManager.push("Loading Mod Items", stepCount);

        bar.step("Initializing Probe");
        probe = new Probe();

        bar.step("Initializing Creative Probe");
        creativeProbe = new CreativeProbe();

        bar.step("Creating Armor Materials");
        ItemArmor.ArmorMaterial materialDiamondHelmet = EnumHelper.addArmorMaterial("diamond_helmet_probe", TheOneProbe.MODID + ":probe_diamond",
                33, new int[]{3, 6, 8, 3}, 10, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F);
        ItemArmor.ArmorMaterial materialGoldHelmet = EnumHelper.addArmorMaterial("gold_helmet_probe", TheOneProbe.MODID + ":probe_gold",
                7, new int[]{1, 3, 5, 2}, 25, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 0.0F);
        ItemArmor.ArmorMaterial materialIronHelmet = EnumHelper.addArmorMaterial("iron_helmet_probe", TheOneProbe.MODID + ":probe_iron",
                15, new int[]{2, 5, 6, 2}, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F);

        bar.step("Creating Armor Probes");
        diamondHelmetProbe = makeHelmet(materialDiamondHelmet, 3, "diamond_helmet_probe", "minecraft:textures/models/armor/diamond_layer_1.png");
        goldHelmetProbe = makeHelmet(materialGoldHelmet, 4, "gold_helmet_probe", "minecraft:textures/models/armor/gold_layer_1.png");
        ironHelmetProbe = makeHelmet(materialIronHelmet, 2, "iron_helmet_probe", "minecraft:textures/models/armor/iron_layer_1.png");

        bar.step("Initializing Probe Note");
        probeNote = new ProbeNote();

        if (ModSetup.baubles) {
            bar.step("Initializing Probe Goggles");
            probeGoggles = BaubleTools.initProbeGoggle();
        }

        ProgressManager.pop(bar);
    }

    private static Item makeHelmet(ItemArmor.ArmorMaterial material, int renderIndex, String name, String baseTexture) {
        Item item = new ProbeArmor(material, renderIndex, EntityEquipmentSlot.HEAD, baseTexture) {
            @Override
            public boolean getHasSubtypes() {
                return true;
            }

        };
        item.setUnlocalizedName(TheOneProbe.MODID + "." + name);
        item.setRegistryName(name);
        item.setCreativeTab(TheOneProbe.tabProbe);
        return item;
    }

    @SideOnly(Side.CLIENT)
    public static void initClient() {
        probe.initModel();
        creativeProbe.initModel();

        ModelLoader.setCustomModelResourceLocation(diamondHelmetProbe, 0, new ModelResourceLocation(diamondHelmetProbe.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(goldHelmetProbe, 0, new ModelResourceLocation(goldHelmetProbe.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ironHelmetProbe, 0, new ModelResourceLocation(ironHelmetProbe.getRegistryName(), "inventory"));

        probeNote.initModel();

        if (ModSetup.baubles) {
            BaubleTools.initProbeModel(probeGoggles);
        }
    }

    @SideOnly(Side.CLIENT)
    @Deprecated //Old Hook, dont use
    public static boolean isProbeInHand(ItemStack stack) {
        return isProbe(stack);
    }

    @SideOnly(Side.CLIENT)
    public static boolean isProbe(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.getItem() == probe || stack.getItem() == creativeProbe) {
            return true;
        }
        if (stack.getTagCompound() == null) {
            return false;
        }
        return stack.getTagCompound().hasKey(PROBETAG);
    }

    @SideOnly(Side.CLIENT)
    private static boolean isProbeHelmet(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.getItem() == diamondHelmetProbe || stack.getItem() == goldHelmetProbe || stack.getItem() == ironHelmetProbe) {
            return true;
        }
        if (stack.getTagCompound() == null) {
            return false;
        }
        return stack.getTagCompound().hasKey(PROBETAG);
    }

    @SideOnly(Side.CLIENT)
    public static boolean hasAProbeSomewhere(EntityPlayer player) {
        return isProbe(player.getHeldItem(EnumHand.MAIN_HAND))
                || isProbe(player.getHeldItem(EnumHand.OFF_HAND))
                || isProbeHelmet(player.inventory.getStackInSlot(36 + 3))
                || (ModSetup.baubles && BaubleTools.hasProbeGoggle(player));
    }
}
