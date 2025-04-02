package mcjty.theoneprobe.apiimpl.providers;

import mcjty.lib.api.power.IBigPower;
import mcjty.theoneprobe.ClientTools;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.ProbeConfig;
import mcjty.theoneprobe.apiimpl.elements.ElementProgress;
import mcjty.theoneprobe.compat.RedstoneFluxTools;
import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.setup.ModSetup;
import net.minecraft.block.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import java.util.Collections;
import java.util.Objects;

import static mcjty.theoneprobe.api.TextStyleClass.*;

public class DefaultProbeInfoProvider implements IProbeInfoProvider {

    @Override
    public String getID() {
        return TheOneProbe.MODID + ":default";
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        Block block = blockState.getBlock();
        BlockPos pos = data.getPos();

        IProbeConfig config = Config.getRealConfig();

        boolean handled = false;
        for (IBlockDisplayOverride override : TheOneProbe.theOneProbeImp.getBlockOverrides()) {
            if (override.overrideStandardInfo(mode, probeInfo, player, world, blockState, data)) {
                handled = true;
                break;
            }
        }
        if (!handled) {
            showStandardBlockInfo(config, mode, probeInfo, blockState, block, data);
        }

        if (Tools.show(mode, config.getShowCropPercentage())) {
            showGrowthLevel(probeInfo, blockState);
        }

        boolean showHarvestLevel = Tools.show(mode, config.getShowHarvestLevel());
        boolean showHarvested = Tools.show(mode, config.getShowCanBeHarvested());
        if (showHarvested && showHarvestLevel) {
            HarvestInfoTools.showHarvestInfo(probeInfo, world, pos, block, blockState, player);
        } else if (showHarvestLevel) {
            HarvestInfoTools.showHarvestLevel(probeInfo, blockState, block);
        } else if (showHarvested) {
            HarvestInfoTools.showCanBeHarvested(probeInfo, world, pos, block, player);
        }

        if (Tools.show(mode, config.getShowRedstone())) {
            showRedstonePower(probeInfo, world, blockState, data, block, Tools.show(mode, config.getShowLeverSetting()));
        }
        if (Tools.show(mode, config.getShowLeverSetting())) {
            showLeverSetting(probeInfo, blockState, block);
        }

        ChestInfoTools.showChestInfo(mode, probeInfo, world, pos, config);

        if (config.getRFMode() > 0) {
            showRF(probeInfo, world, pos);
        }
        if (Tools.show(mode, config.getShowTankSetting())) {
            if (config.getTankMode() > 0) {
                showTankInfo(probeInfo, world, pos);
            }
        }

        if (Tools.show(mode, config.getShowBrewStandSetting())) {
            showBrewingStandInfo(probeInfo, world, data, block);
        }

        if (Tools.show(mode, config.getShowMobSpawnerSetting())) {
            showMobSpawnerInfo(probeInfo, world, data, block);
        }
        if (blockState.getBlock() instanceof BlockCauldron) {
            for (IProperty<?> property : blockState.getProperties().keySet()) {
                if (!"level".equals(property.getName())) continue;
                if (property.getValueClass() == Integer.class) {
                    //noinspection unchecked
                    IProperty<Integer> integerProperty = (IProperty<Integer>) property;
                    int fill = blockState.getValue(integerProperty);

                    if (fill > 0) {
                        probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                                .text(TextStyleClass.LABEL + ((fill == 1) ? fill + " {*theoneprobe.probe.bottle_indicator*}" : fill + " {*theoneprobe.probe.bottles_indicator*}"));
                    } else {
                        probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                                .text(TextStyleClass.LABEL + "{*theoneprobe.probe.empty_indicator*}");
                    }
                    return;
                }
            }
        }
    }

    private void showBrewingStandInfo(IProbeInfo probeInfo, World world, IProbeHitData data, Block block) {
        if (block instanceof BlockBrewingStand) {
            TileEntity te = world.getTileEntity(data.getPos());
            if (te instanceof TileEntityBrewingStand) {
                int brewtime = ((TileEntityBrewingStand) te).getField(0);
                int fuel = ((TileEntityBrewingStand) te).getField(1);
                probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                        .item(new ItemStack(Items.BLAZE_POWDER), probeInfo.defaultItemStyle().width(16).height(16))
                        .text(LABEL + "{*theoneprobe.probe.fuel_indicator*} " + INFO + fuel);
                if (brewtime > 0) {
                    probeInfo.text(LABEL + "{*theoneprobe.probe.time_indicator*} " + INFO + brewtime + " ticks");
                }

            }
        }
    }

    /**
     * Shows information specific to a mob spawner block in the probe info.
     * This method checks if the block is a mob spawner and retrieves relevant information
     * about the spawner's configured mob.
     *
     * @author Artemish
     *
     * @param probeInfo The {@link IProbeInfo} object where information about the mob spawner is added.
     * @param world The {@link World} instance where the mob spawner exists.
     * @param data Additional hit data related to the mob spawner probe.
     * @param block The {@link Block} instance being probed, expected to be a mob spawner.
     */
    private void showMobSpawnerInfo(IProbeInfo probeInfo, World world, IProbeHitData data, Block block) {
        if (block instanceof BlockMobSpawner) {
            TileEntity te = world.getTileEntity(data.getPos());
            if (te instanceof TileEntityMobSpawner) {
                MobSpawnerBaseLogic logic = ((TileEntityMobSpawner) te).getSpawnerBaseLogic();
                String mobName = logic.getCachedEntity().getName();
                probeInfo.horizontal(probeInfo.defaultLayoutStyle()
                    .alignment(ElementAlignment.ALIGN_CENTER))
                    .text(LABEL + "{*theoneprobe.probe.mob_indicator*} " + INFO + mobName);
            }
        }
    }

    private void showRedstonePower(IProbeInfo probeInfo, World world, IBlockState blockState, IProbeHitData data, Block block,
                                   boolean showLever) {
        if (showLever && block instanceof BlockLever) {
            // We are showing the lever setting, so we don't show redstone in that case
            return;
        }
        int redstonePower;
        if (block instanceof BlockRedstoneWire) {
            redstonePower = blockState.getValue(BlockRedstoneWire.POWER);
        } else {
            redstonePower = world.getRedstonePower(data.getPos(), data.getSideHit().getOpposite());
        }
        if (redstonePower > 0) {
            probeInfo.horizontal()
                    .item(new ItemStack(Items.REDSTONE), probeInfo.defaultItemStyle().width(14).height(14))
                    .text(LABEL + "{*theoneprobe.probe.power_indicator*} " + INFO + redstonePower);
        }
    }

    private void showLeverSetting(IProbeInfo probeInfo, IBlockState blockState, Block block) {
        if (block instanceof BlockLever) {
            probeInfo.horizontal().item(new ItemStack(Items.REDSTONE), probeInfo.defaultItemStyle().width(14).height(14))
                    .text(LABEL + "State: " + INFO + (blockState.getValue(BlockLever.POWERED) ? "On" : "Off"));
        } else if (block instanceof BlockRedstoneComparator) {
            BlockRedstoneComparator.Mode mode = blockState.getValue(BlockRedstoneComparator.MODE);
            probeInfo.text(LABEL + "Mode: " + INFO + mode.getName());
        } else if (block instanceof BlockRedstoneRepeater) {
            Boolean locked = blockState.getValue(BlockRedstoneRepeater.LOCKED);
            Integer delay = blockState.getValue(BlockRedstoneRepeater.DELAY);
            probeInfo.text(LABEL + "Delay: " + INFO + delay + " ticks");
            if (locked) {
                probeInfo.text(INFO + "Locked");
            }
        }
    }

    private void showTankInfo(IProbeInfo probeInfo, World world, BlockPos pos) {
        ProbeConfig config = Config.getDefaultConfig();
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
            net.minecraftforge.fluids.capability.IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            if (handler != null) {
                IFluidTankProperties[] properties = handler.getTankProperties();
                if (properties != null) {
                    for (IFluidTankProperties property : properties) {
                        if (property != null) {
                            FluidStack fluidStack = property.getContents();
                            int maxContents = property.getCapacity();
                            addFluidInfo(probeInfo, config, fluidStack, maxContents);
                        }
                    }
                }
            }
        }
    }

    private void addFluidInfo(IProbeInfo probeInfo, ProbeConfig config, FluidStack fluidStack, int maxContents) {
        int contents = fluidStack == null ? 0 : fluidStack.amount;
        if (fluidStack != null) {
            probeInfo.text(NAME + "Liquid: " + " {*" + fluidStack.getUnlocalizedName() +"*}"); //TOPFIX: Fluid container info doesn't show fluid local name when player on server.
        }
        if (config.getTankMode() == 1) {
            probeInfo.progress(contents, maxContents,
                    probeInfo.defaultProgressStyle()
                            .suffix("mB")
                            .filledColor(Config.tankbarFilledColor)
                            .alternateFilledColor(Config.tankbarAlternateFilledColor)
                            .borderColor(Config.tankbarBorderColor)
                            .numberFormat(Config.tankFormat));
        } else {
            probeInfo.text(PROGRESS + ElementProgress.format(contents, Config.tankFormat, "mB"));
        }
    }

    private void showRF(IProbeInfo probeInfo, World world, BlockPos pos) {
        ProbeConfig config = Config.getDefaultConfig();
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof IBigPower) {
            long energy = ((IBigPower) te).getStoredPower();
            long maxEnergy = ((IBigPower) te).getCapacity();
            addRFInfo(probeInfo, config, energy, maxEnergy);
        } else if (ModSetup.redstoneflux && RedstoneFluxTools.isEnergyHandler(te)) {
            int energy = RedstoneFluxTools.getEnergy(te);
            int maxEnergy = RedstoneFluxTools.getMaxEnergy(te);
            addRFInfo(probeInfo, config, energy, maxEnergy);
        } else if (te != null && te.hasCapability(CapabilityEnergy.ENERGY, null)) {
            net.minecraftforge.energy.IEnergyStorage handler = te.getCapability(CapabilityEnergy.ENERGY, null);
            if (handler != null) {
                addRFInfo(probeInfo, config, handler.getEnergyStored(), handler.getMaxEnergyStored());
            }
        }
    }

    /**
     * Adds information about Redstone Flux energy based on the given configuration.
     * If the RF mode in the configuration is set to 1, a progress bar is displayed with specific styling.
     * Otherwise, a text representation of the RF energy is shown.
     *
     * @author McJty
     *
     * @param probeInfo The {@link IProbeInfo} object to which the RF information will be added.
     * @param config The {@link ProbeConfig} containing settings for displaying RF information.
     * @param energy The current amount of RF energy.
     * @param maxEnergy The maximum capacity of RF energy.
     */
    private void addRFInfo(IProbeInfo probeInfo, ProbeConfig config, long energy, long maxEnergy) {
        if (config.getRFMode() == 1) {
            probeInfo.progress(energy, maxEnergy,
                    probeInfo.defaultProgressStyle()
                            .suffix("RF")
                            .filledColor(Config.rfbarFilledColor)
                            .alternateFilledColor(Config.rfbarAlternateFilledColor)
                            .borderColor(Config.rfbarBorderColor)
                            .numberFormat(Config.rfFormat));
        } else {
            probeInfo.text(PROGRESS + "RF: " + ElementProgress.format(energy, Config.rfFormat, "RF"));
        }
    }

    /**
     * Adds information about crop growth based on the given configuration.
     *
     * @author McJty
     * @author Joseph C Sible
     *
     * @param probeInfo The {@link IProbeInfo} object to which the growth information will be added.
     * @param blockState The {@link IBlockState} containing the block info.
     */
    private void showGrowthLevel(IProbeInfo probeInfo, IBlockState blockState) {
        for (IProperty<?> property : blockState.getProperties().keySet()) {
            if(!"age".equals(property.getName())) continue;
            if(property.getValueClass() == Integer.class) {
                IProperty<Integer> integerProperty = (IProperty<Integer>)property;
                int age = blockState.getValue(integerProperty);
                int maxAge = Collections.max(integerProperty.getAllowedValues());
                if (age == maxAge) {
                    probeInfo.text(OK + "{*theoneprobe.probe.fully_grown_indicator*}");
                } else {
                    probeInfo.text(LABEL + "{*theoneprobe.probe.growth_indicator*} " + WARNING + (age * 100) / maxAge + "%");
                }
            }
            return;
        }
    }

    private static String cachedBlockName;
    private static String cachedTruncatedBlockName;

    /**
     * Shows standard information about a block based on the probe configuration and mode.
     * This method handles different types of blocks and their display in the probe info.
     *
     * @author McJty, Joseph C. Sible
     *
     * @param config The {@link IProbeConfig}.
     * @param mode The {@link ProbeMode} determining the level of detail to show.
     * @param probeInfo The {@link IProbeInfo} object where block information is added.
     * @param blockState The {@link IBlockState} of the block being probed.
     * @param block The {@link Block} being probed.
     * @param data Additional hit data related to the block probe.
     */
    public static void showStandardBlockInfo(IProbeConfig config, ProbeMode mode, IProbeInfo probeInfo, IBlockState blockState, Block block,
                                             IProbeHitData data) {
        String modid = Tools.getModName(block);
        ItemStack pickBlock = data.getPickBlock();

        if (block instanceof BlockSilverfish && mode != ProbeMode.DEBUG && !Tools.show(mode, config.getShowSilverfish())) {
            BlockSilverfish.EnumType type = blockState.getValue(BlockSilverfish.VARIANT);
            blockState = type.getModelBlock();
            block = blockState.getBlock();
            pickBlock = new ItemStack(block, 1, block.getMetaFromState(blockState));
        }

        if (block instanceof BlockFluidBase || block instanceof BlockLiquid) {
            Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
            if (fluid != null) {
                FluidStack fluidStack = new FluidStack(fluid, Fluid.BUCKET_VOLUME);
                ItemStack bucketStack = FluidUtil.getFilledBucket(fluidStack);

                IProbeInfo horizontal = probeInfo.horizontal();
                if (fluidStack.isFluidEqual(FluidUtil.getFluidContained(bucketStack))) {
                    horizontal.item(bucketStack);
                } else {
                    horizontal.icon(fluid.getStill(), -1, -1, 16, 16, probeInfo.defaultIconStyle().width(20));
                }

                horizontal.vertical()
                        .text(NAME + fluidStack.getLocalizedName())
                        .text(MODNAME + modid);
                return;
            }
        }

        if (!Objects.requireNonNull(pickBlock).isEmpty()) {
            if (Tools.show(mode, config.getShowModName())) {
                String blockDisplayName = pickBlock.getDisplayName();

                if (Config.getBlockNameMaxWidth() != 0) {
                    // Calculate available width for text
                    FontRenderer fontRenderer = ClientTools.mc.fontRenderer;
                    int screenWidth = Tools.getScreenWidth();
                    int availableWidth = (int) (screenWidth * Config.getBlockNameMaxWidth());

                    // String truncation
                    if (blockDisplayName.equals(cachedBlockName))
                        blockDisplayName = cachedTruncatedBlockName;
                    else if (fontRenderer.getStringWidth(blockDisplayName) > availableWidth) {
                        int charWidth = fontRenderer.getCharWidth(blockDisplayName.charAt(0));
                        // Estimate
                        int index = availableWidth / charWidth - 1;
                        index = Math.max(index, 0);
                        String truncated = null;
                        boolean quit = false;
                        // This loop usually runs 2-4 times
                        while (!quit) {
                            truncated = blockDisplayName.substring(0, index);
                            int width = fontRenderer.getStringWidth(truncated);
                            int nextWidth = fontRenderer.getStringWidth(blockDisplayName.substring(0, index + 1));

                            if ((width <= availableWidth && nextWidth > availableWidth) || width == availableWidth)
                                quit = true;
                            else if (width > availableWidth)
                                index--;
                            else
                                index++;
                        }
                        cachedBlockName = blockDisplayName;
                        blockDisplayName = truncated + "...";
                        cachedTruncatedBlockName = blockDisplayName;
                    } else {
                        cachedBlockName = blockDisplayName;
                        cachedTruncatedBlockName = blockDisplayName;
                    }
                }

                probeInfo.horizontal()
                        .item(pickBlock)
                        .vertical()
                        .text(NAME + blockDisplayName)
                        .text(MODNAME + modid);
            } else {
                probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                        .item(pickBlock)
                        .itemLabel(pickBlock);
            }
        } else {
            if (Tools.show(mode, config.getShowModName())) {
                probeInfo.vertical()
                        .text(MODNAME + modid);
            }
        }
    }
}
