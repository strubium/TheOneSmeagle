package mcjty.theoneprobe.compat.waila;

import mcp.mobius.waila.api.IWailaDataAccessor;
import mcjty.theoneprobe.api.IProbeHitData;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents the Waila data we have
 */
public class WailaDataAccessor implements IWailaDataAccessor {

    private final World world;
    private final EntityPlayer player;
    private final IBlockState blockState;
    private final TileEntity tileEntity;
    private final RayTraceResult mop;
    private final Vec3d renderingPosition;
    private final NBTTagCompound nbtData;
    private final ItemStack stack;

    public WailaDataAccessor(
            World world,
            EntityPlayer player,
            IBlockState blockState,
            IProbeHitData hitData,
            NBTTagCompound nbtData,
            ItemStack stack) {

        this.world = world;
        this.player = player;
        this.blockState = blockState;
        this.tileEntity = world.getTileEntity(hitData.getPos());

        this.mop = new RayTraceResult(
                RayTraceResult.Type.BLOCK,
                hitData.getHitVec(),
                hitData.getSideHit(),
                hitData.getPos()
        );

        this.renderingPosition = hitData.getHitVec();
        this.nbtData = nbtData;
        this.stack = stack;
    }

    @Nonnull
    @Override
    public World getWorld() {
        return world;
    }

    @Nonnull
    @Override
    public EntityPlayer getPlayer() {
        return player;
    }

    @Nonnull
    @Override
    public Block getBlock() {
        return blockState.getBlock();
    }

    @Override
    public int getMetadata() {
        return getBlock().getMetaFromState(blockState);
    }

    @Nonnull
    @Override
    public IBlockState getBlockState() {
        return blockState;
    }

    @Nullable
    @Override
    public TileEntity getTileEntity() {
        return tileEntity;
    }

    @Nonnull
    @Override
    public RayTraceResult getMOP() {
        return mop;
    }

    @Nonnull
    @Override
    public BlockPos getPosition() {
        return mop.getBlockPos();
    }

    @Nullable
    @Override
    public Vec3d getRenderingPosition() {
        return renderingPosition;
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData() {
        return nbtData;
    }

    @Override
    public int getNBTInteger(NBTTagCompound tag, String key) {
        return tag == null ? 0 : tag.getInteger(key);
    }

    @Override
    public double getPartialFrame() {
        return 0.0;
    }

    @Nonnull
    @Override
    public EnumFacing getSide() {
        return mop.sideHit;
    }

    @Nonnull
    @Override
    public ItemStack getStack() {
        return stack;
    }
}