package mcjty.theoneprobe.items;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.compat.BaubleTools;
import mcjty.theoneprobe.probe.ProbeArmor;
import mcjty.theoneprobe.setup.ModSetup;
import mcjty.theoneprobe.setup.Registration;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static CreativeProbe creativeProbe;
    public static Probe probe;
    public static Item diamondHelmetProbe;
    public static Item goldHelmetProbe;
    public static Item ironHelmetProbe;
    public static Item probeGoggles;
    public static ProbeNote probeNote;

    private static final List<Item> helmetModels = new ArrayList<>();


    public static final String PROBETAG = TheOneProbe.MODID;

    public static void init() {
        int stepCount = ModSetup.baubles ? 5 : 4;
        final ProgressManager.ProgressBar bar = ProgressManager.push("Loading Mod Items", stepCount);

        bar.step("Initializing Probe");
        probe = new Probe();

        bar.step("Initializing Creative Probe");
        creativeProbe = new CreativeProbe();

        bar.step("Creating Armor Probes");
        diamondHelmetProbe = makeHelmet(Items.DIAMOND_HELMET,"diamond_helmet_probe");
        goldHelmetProbe = makeHelmet(Items.GOLDEN_HELMET, "gold_helmet_probe");
        ironHelmetProbe = makeHelmet(Items.IRON_HELMET, "iron_helmet_probe");

        bar.step("Initializing Probe Note");
        probeNote = new ProbeNote();

        if (ModSetup.baubles) {
            bar.step("Initializing Probe Goggles");
            probeGoggles = BaubleTools.initProbeGoggle();
        }

        ProgressManager.pop(bar);
    }

    public static Item makeHelmet(Item baseItem, String name) {
        ItemArmor.ArmorMaterial material = ((ItemArmor) baseItem).getArmorMaterial();
        int renderIndex = ((ItemArmor) baseItem).renderIndex;

        Item item = new ProbeArmor(material, renderIndex, EntityEquipmentSlot.HEAD, getBaseTexture(baseItem)) {
            @Override
            public boolean getHasSubtypes() {
                return true;
            }
        };

        item.setUnlocalizedName(TheOneProbe.MODID + "." + name);
        item.setRegistryName(name);
        item.setCreativeTab(TheOneProbe.tabProbe);

        // Register the item
        Registration.addItem(item);
        helmetModels.add(item);

        return item;
    }

    private static String getBaseTexture(Item baseItem) {
        String registryNamespace = baseItem.getRegistryName().getResourceDomain();

        // Determine the material (e.g. "gold") for armor textures
        String armorMaterial = ((ItemArmor) baseItem).getArmorMaterial().getName().toLowerCase();

        // Return the path to the armor texture (layer 1 in this case)
        return registryNamespace + ":textures/models/armor/" + armorMaterial + "_layer_1.png";
    }


    @SideOnly(Side.CLIENT)
    public static void initClient() {
        initModel(probe);
        initModel(creativeProbe);
        initModel(probeNote);

        // Initialize all helmet models and resolve their textures
        for (Item helmet : helmetModels) {
            initModel(helmet);
        }

        if (ModSetup.baubles) {
            initModel(probeGoggles);
        }
    }

    @SideOnly(Side.CLIENT)
    private static void initModel(Item helmet) {
        ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation(helmet.getRegistryName(), "inventory"));
    }

    @Deprecated //Old Hook, dont use
    public static boolean isProbeInHand(ItemStack stack) {
        return isProbe(stack);
    }

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

    public static boolean hasAProbeSomewhere(EntityPlayer player) {
        return isProbe(player.getHeldItem(EnumHand.MAIN_HAND))
                || isProbe(player.getHeldItem(EnumHand.OFF_HAND))
                || isProbeHelmet(player.inventory.getStackInSlot(36 + 3))
                || (ModSetup.baubles && BaubleTools.hasProbeGoggle(player));
    }
}
