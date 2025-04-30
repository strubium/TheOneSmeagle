package mcjty.theoneprobe.probe;

import com.google.gson.JsonObject;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.items.ModItems;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import lombok.NonNull;

public class ProbeHelmetRecipeFactory implements IRecipeFactory {

    @Override
    public IRecipe parse(JsonContext context, JsonObject json) {
        ShapelessOreRecipe recipe = ShapelessOreRecipe.factory(context, json);

        return new HelmetRecipe(new ResourceLocation(TheOneProbe.MODID, "probe_helmet"), recipe.getRecipeOutput(), recipe.getIngredients());
    }

    public static class HelmetRecipe extends ShapelessOreRecipe {
        public HelmetRecipe(ResourceLocation group, ItemStack result, NonNullList<Ingredient> ingredients) {
            super(group, ingredients, result);
        }

        @NonNull
        @Override
        public ItemStack getCraftingResult(@NonNull InventoryCrafting inventory) {
            ItemStack result = super.getCraftingResult(inventory);
            NBTTagCompound tc = new NBTTagCompound();
            tc.setInteger(ModItems.PROBETAG, 1);
            result.setTagCompound(tc);
            return result;
        }
    }
}
