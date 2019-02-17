package mcjty.theoneprobe.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.ProbeHitData;
import mcjty.theoneprobe.apiimpl.ProbeInfo;
import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.items.ModItems;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.List;

import static mcjty.theoneprobe.api.TextStyleClass.ERROR;
import static mcjty.theoneprobe.api.TextStyleClass.LABEL;
import static mcjty.theoneprobe.config.Config.PROBE_NEEDEDFOREXTENDED;
import static mcjty.theoneprobe.config.Config.PROBE_NEEDEDHARD;

public class PacketGetInfo implements IPacket {

    public static final Identifier GET_INFO = new Identifier(TheOneProbe.MODID, "get_info");

    private int dim;
    private BlockPos pos;
    private ProbeMode mode;
    private Direction sideHit;
    private Vec3d hitVec;
    private ItemStack pickBlock;

    @Override
    public Identifier getId() {
        return GET_INFO;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dim = buf.readInt();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        mode = ProbeMode.values()[buf.readByte()];
        byte sideByte = buf.readByte();
        if (sideByte == 127) {
            sideHit = null;
        } else {
            sideHit = Direction.values()[sideByte];
        }
        if (buf.readBoolean()) {
            hitVec = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        }
        pickBlock = NetworkTools.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dim);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeByte(mode.ordinal());
        buf.writeByte(sideHit == null ? 127 : sideHit.ordinal());
        if (hitVec == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeDouble(hitVec.x);
            buf.writeDouble(hitVec.y);
            buf.writeDouble(hitVec.z);
        }

        ByteBuf buffer = Unpooled.buffer();
        NetworkTools.writeItemStack(buffer, pickBlock);
        if (buffer.writerIndex() <= Config.maxPacketToServer) {
            buf.writeBytes(buffer);
        } else {
            ItemStack copy = new ItemStack(pickBlock.getItem(), pickBlock.getAmount());
            NetworkTools.writeItemStack(buf, copy);
        }
    }

    public PacketGetInfo() {
    }

    public PacketGetInfo(int dim, BlockPos pos, ProbeMode mode, HitResult mouseOver, ItemStack pickBlock) {
        this.dim = dim;
        this.pos = pos;
        this.mode = mode;
        this.sideHit = mouseOver.getType() == HitResult.Type.BLOCK ? ((BlockHitResult)mouseOver).getSide() : null;
        this.hitVec = mouseOver.getPos();
        this.pickBlock = pickBlock;
    }

    public static class Handler extends MessageHandler<PacketGetInfo> {

        @Override
        protected PacketGetInfo createPacket() {
            return new PacketGetInfo();
        }

        @Override
        void handle(PacketContext context, PacketGetInfo message) {
            ServerWorld world = context.getPlayer().getEntityWorld().getServer().getWorld(DimensionType.byRawId(message.dim));
            if (world != null) {
                ProbeInfo probeInfo = getProbeInfo(context.getPlayer(),
                        message.mode, world, message.pos, message.sideHit, message.hitVec, message.pickBlock);
                NetworkInit.sendToClient(new PacketReturnInfo(message.dim, message.pos, probeInfo), (ServerPlayerEntity) context.getPlayer());
            }
        }
    }

    private static ProbeInfo getProbeInfo(PlayerEntity player, ProbeMode mode, World world, BlockPos blockPos, Direction sideHit, Vec3d hitVec, ItemStack pickBlock) {
        if (Config.needsProbe == PROBE_NEEDEDFOREXTENDED) {
            // We need a probe only for extended information
            if (!ModItems.hasAProbeSomewhere(player)) {
                // No probe anywhere, switch EXTENDED to NORMAL
                if (mode == ProbeMode.EXTENDED) {
                    mode = ProbeMode.NORMAL;
                }
            }
        } else if (Config.needsProbe == PROBE_NEEDEDHARD && !ModItems.hasAProbeSomewhere(player)) {
            // The server says we need a probe but we don't have one in our hands
            return null;
        }

        BlockState state = world.getBlockState(blockPos);
        ProbeInfo probeInfo = TheOneProbe.theOneProbeImp.create();
        IProbeHitData data = new ProbeHitData(blockPos, hitVec, sideHit, pickBlock);

        IProbeConfig probeConfig = TheOneProbe.theOneProbeImp.createProbeConfig();
        List<IProbeConfigProvider> configProviders = TheOneProbe.theOneProbeImp.getConfigProviders();
        for (IProbeConfigProvider configProvider : configProviders) {
            configProvider.getProbeConfig(probeConfig, player, world, state, data);
        }
        Config.setRealConfig(probeConfig);

        List<IProbeInfoProvider> providers = TheOneProbe.theOneProbeImp.getProviders();
        for (IProbeInfoProvider provider : providers) {
            try {
                provider.addProbeInfo(mode, probeInfo, player, world, state, data);
            } catch (Throwable e) {
                ThrowableIdentity.registerThrowable(e);
                probeInfo.text(LABEL + "Error: " + ERROR + provider.getID());
            }
        }
        return probeInfo;
    }

}
