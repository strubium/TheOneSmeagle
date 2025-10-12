package mcjty.theoneprobe.items;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.compat.BaubleTools;
import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.probe.ProbeArmor;
import mcjty.theoneprobe.setup.proxy.CommonProxy;
import mcjty.theoneprobe.setup.Registration;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModItems {
    public static CreativeProbe creativeProbe;
    public static Probe probe;
    public static Item probeGoggles;
    public static ProbeNote probeNote;

    private static final List<Item> helmetModels = new ArrayList<>();


    public static final String PROBETAG = TheOneProbe.MODID;
    public static CreativeTabs tabProbe;

    public static void init() {
        if(Config.regProbes){

             tabProbe = new CreativeTabs("Probe") {
                @Override
                public ItemStack createIcon() {
                    return new ItemStack(ModItems.probe);
                }
            };


            int stepCount = CommonProxy.baubles ? 5 : 4;
            final ProgressManager.ProgressBar bar = ProgressManager.push("Loading Mod Items", stepCount);
            bar.step("Initializing Probe");
            probe = new Probe();

            bar.step("Initializing Creative Probe");
            creativeProbe = new CreativeProbe();

            bar.step("Creating Armor Probes");
            if (Config.regProbeHelmets) {
                int totalItems = ForgeRegistries.ITEMS.getValuesCollection().size();
                ProgressManager.ProgressBar progressBar = ProgressManager.push("Processing Helmets", totalItems);

                for (Item item : ForgeRegistries.ITEMS.getValuesCollection()) {
                    progressBar.step(item.getRegistryName() != null ? item.getRegistryName().toString() : "Unknown Item");

                    if (item instanceof ItemArmor && ((ItemArmor) item).armorType == EntityEquipmentSlot.HEAD) {
                        ResourceLocation registryName = item.getRegistryName();
                        if (registryName != null && !Config.probeHelmetBlacklist.contains(registryName.getNamespace())) {
                            if (((ItemArmor) item).getArmorMaterial().equals(ItemArmor.ArmorMaterial.LEATHER)) {
                                continue; //HACK HACK Skip leather helmets because of their die (dye) rendering
                            }

                            String probeHelmetName = registryName.getPath() + "_probe";
                            Item madeHelmet = makeHelmet(item, probeHelmetName);
                            CommonProxy.getLogger().info("Made Helmet: {}", madeHelmet.getRegistryName());
                        } else {
                            CommonProxy.getLogger().debug("Not making helmet from: {}, matches: {}", registryName, registryName.getNamespace());
                        }
                    }
                }

                ProgressManager.pop(progressBar);
            }

            bar.step("Initializing Probe Note");
            probeNote = new ProbeNote();

            if (CommonProxy.baubles) {
                bar.step("Initializing Probe Goggles");
                probeGoggles = BaubleTools.initProbeGoggle();
            }

            ProgressManager.pop(bar);
        }
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

        item.setTranslationKey(TheOneProbe.MODID + "." + name);
        item.setRegistryName(name);
        item.setCreativeTab(ModItems.tabProbe);

        // Register the item
        Registration.addItem(item);
        helmetModels.add(item);

        GameRegistry.addShapelessRecipe(
                new ResourceLocation(TheOneProbe.MODID, name + "_recipe"), // Recipe ID
                null, // Recipe Group
                new ItemStack(item), // Output
                Ingredient.fromItems(baseItem), // Input 1
                Ingredient.fromItems(probe) // Input 2
        );

        return item;
    }

    private static String getBaseTexture(Item baseItem) {
        String registryNamespace = baseItem.getRegistryName().getNamespace();

        String registryPath = baseItem.getRegistryName().getPath();

        String[] parts = registryPath.split("_");

        if(Objects.equals(parts[0], "golden") & registryNamespace.equals("minecraft")){
            parts[0] = "gold"; //HACK HACK Golden helmets use "golden" for their id, but the model uses "gold"
        }

        // Determine the material (e.g. "gold") for armor textures
        String armorMaterial = parts[0];

        // Return the path to the armor texture (layer 1 in this case)
        return registryNamespace + ":textures/models/armor/" + armorMaterial + "_layer_1.png";
    }


    @SideOnly(Side.CLIENT)
    public static void initClient() {
        if(Config.regProbes){
            initModel(probe);
            initModel(creativeProbe);
            initModel(probeNote);

            // Initialize all helmet models and resolve their textures
            for (Item helmet : helmetModels) {
                initModel(helmet);
            }

            if (CommonProxy.baubles) {
                initModel(probeGoggles);
            }
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
        if (stack.getTagCompound() == null) {
            return false;
        }
        return stack.getTagCompound().hasKey(PROBETAG);
    }

    public static boolean hasAProbeSomewhere(EntityPlayer player) {
        return isProbe(player.getHeldItem(EnumHand.MAIN_HAND))
                || isProbe(player.getHeldItem(EnumHand.OFF_HAND))
                || isProbeHelmet(player.inventory.getStackInSlot(36 + 3))
                || (CommonProxy.baubles && BaubleTools.hasProbeGoggle(player));
    }
}
