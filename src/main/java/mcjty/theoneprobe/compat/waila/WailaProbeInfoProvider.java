package mcjty.theoneprobe.compat.waila;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WailaProbeInfoProvider implements IProbeInfoProvider {

    private final ModuleRegistrar registrar = ModuleRegistrar.instance();

    @Override
    public String getID() {
        return TheOneProbe.MODID + ":waila";
    }

    @Override
    public void addProbeInfo(
            ProbeMode mode,
            IProbeInfo probeInfo,
            EntityPlayer player,
            World world,
            IBlockState blockState,
            IProbeHitData data) {

        Object block = blockState.getBlock();

        /*
         * Only do WAILA work when this block has at least one
         * registered WAILA provider. (Most should)
         */
        if (!registrar.hasStackProviders(block)
                && !registrar.hasHeadProviders(block)
                && !registrar.hasBodyProviders(block)
                && !registrar.hasTailProviders(block)
                && !registrar.hasNBTProviders(block)) {
            return;
        }


        TileEntity tileEntity = world.getTileEntity(data.getPos());

        // Get stack data
        ItemStack stack = data.getPickBlock();

        if (stack == null) {
            stack = ItemStack.EMPTY;
        }

        Map<Integer, List<IWailaDataProvider>> stackProviders =
                registrar.getStackProviders(block);

        for (List<IWailaDataProvider> providers : stackProviders.values()) {
            for (IWailaDataProvider provider : providers) {

                WailaDataAccessor accessor = new WailaDataAccessor(
                        world,
                        player,
                        blockState,
                        data,
                        new NBTTagCompound(),
                        stack
                );

                ItemStack result = provider.getWailaStack(
                        accessor,
                        WailaConfigHandler.INSTANCE
                );

                /*
                 * WAILA providers such as LittleTiles may return
                 * ItemStack.EMPTY to mean "leave the current stack alone".
                 */
                if (result != null && !result.isEmpty()) {
                    stack = result;
                }
            }
        }

        // Get NBT data
        NBTTagCompound nbtData = new NBTTagCompound();

        EntityPlayerMP playerMP = null;

        if (player instanceof EntityPlayerMP) {
            playerMP = (EntityPlayerMP) player;
        }

        Map<Integer, List<IWailaDataProvider>> nbtProviders =
                registrar.getNBTProviders(block);

        for (List<IWailaDataProvider> providers : nbtProviders.values()) {
            for (IWailaDataProvider provider : providers) {

                if (playerMP != null) {
                    provider.getNBTData(
                            playerMP,
                            tileEntity,
                            nbtData,
                            world,
                            data.getPos()
                    );
                }
            }
        }

        /*
         * -------------------------------------------------------------
         * FINAL ACCESSOR
         * -------------------------------------------------------------
         */

        WailaDataAccessor accessor = new WailaDataAccessor(
                world,
                player,
                blockState,
                data,
                nbtData,
                stack
        );


        //HEAD
        addProviders(
                probeInfo,
                registrar.getHeadProviders(block),
                accessor,
                WailaConfigHandler.INSTANCE,
                ProviderType.HEAD
        );


        //BODY
        addProviders(
                probeInfo,
                registrar.getBodyProviders(block),
                accessor,
                WailaConfigHandler.INSTANCE,
                ProviderType.BODY
        );


        //TAIL
        addProviders(
                probeInfo,
                registrar.getTailProviders(block),
                accessor,
                WailaConfigHandler.INSTANCE,
                ProviderType.TAIL
        );
    }

    private void addProviders(
            IProbeInfo probeInfo,
            Map<Integer, List<IWailaDataProvider>> providerMap,
            WailaDataAccessor accessor,
            WailaConfigHandler config,
            ProviderType type) {


        // WAILA passes the current tooltip list to the next provider.
        List<String> currentTip = new ArrayList<>();

        for (List<IWailaDataProvider> providers : providerMap.values()) {
            for (IWailaDataProvider provider : providers) {

                List<String> result;

                switch (type) {
                    case HEAD:
                        result = provider.getWailaHead(
                                accessor.getStack(),
                                currentTip,
                                accessor,
                                config
                        );
                        break;

                    case BODY:
                        result = provider.getWailaBody(
                                accessor.getStack(),
                                currentTip,
                                accessor,
                                config
                        );
                        break;

                    case TAIL:
                        result = provider.getWailaTail(
                                accessor.getStack(),
                                currentTip,
                                accessor,
                                config
                        );
                        break;

                    default:
                        continue;
                }

                /*
                 * A null result means the provider did not replace
                 * the current tooltip.
                 */
                if (result != null) {
                    currentTip = result;
                }
            }
        }


        // Display the WAILA tooltip
        for (String line : currentTip) {
            if (line != null && !line.isEmpty()) {
                probeInfo.text(line);
            }
        }
    }

    private enum ProviderType {
        HEAD,
        BODY,
        TAIL
    }
}
