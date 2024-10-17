package mcjty.theoneprobe.rendering;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.ProbeHitData;
import mcjty.theoneprobe.apiimpl.ProbeHitEntityData;
import mcjty.theoneprobe.apiimpl.ProbeInfo;
import mcjty.theoneprobe.apiimpl.elements.ElementProgress;
import mcjty.theoneprobe.apiimpl.elements.ElementText;
import mcjty.theoneprobe.apiimpl.providers.DefaultProbeInfoEntityProvider;
import mcjty.theoneprobe.apiimpl.providers.DefaultProbeInfoProvider;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import mcjty.theoneprobe.config.ConfigSetup;
import mcjty.theoneprobe.network.PacketGetEntityInfo;
import mcjty.theoneprobe.network.PacketGetInfo;
import mcjty.theoneprobe.network.PacketHandler;
import mcjty.theoneprobe.network.ThrowableIdentity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static mcjty.theoneprobe.api.TextStyleClass.ERROR;
public class OverlayRenderer {

    private static Map<Pair<Integer, BlockPos>, Pair<Long, ProbeInfo>> cachedInfo = new HashMap<>();
    private static Map<UUID, Pair<Long, ProbeInfo>> cachedEntityInfo = new HashMap<>();
    private static long lastCleanupTime = 0;

    /**For a short while we keep displaying the last pair if we have no new information to prevent flickering*/
    private static Pair<Long, ProbeInfo> lastPair;
    private static long lastPairTime = 0;

    /** When the server delays too long we also show some preliminary information already*/
    private static long lastRenderedTime = -1;

    public static void registerProbeInfo(int dim, BlockPos pos, ProbeInfo probeInfo) {
        if (probeInfo == null) {
            return;
        }
        long time = System.currentTimeMillis();
        cachedInfo.put(Pair.of(dim, pos), Pair.of(time, probeInfo));
    }

    public static void registerProbeInfo(UUID uuid, ProbeInfo probeInfo) {
        if (probeInfo == null) {
            return;
        }
        long time = System.currentTimeMillis();
        cachedEntityInfo.put(uuid, Pair.of(time, probeInfo));
    }

    public static void renderHUD(ProbeMode mode, float partialTicks) {
        float dist = ConfigSetup.probeDistance;

        RayTraceResult mouseOver = Minecraft.getMinecraft().objectMouseOver;
        if (mouseOver != null) {
            if (mouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
                GlStateManager.pushMatrix();

                double scale = ConfigSetup.tooltipScale;

                ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
                double sw = scaledresolution.getScaledWidth_double();
                double sh = scaledresolution.getScaledHeight_double();

                setupOverlayRendering(sw * scale, sh * scale);
                renderHUDEntity(mode, mouseOver, sw * scale, sh * scale);
                setupOverlayRendering(sw, sh);
                GlStateManager.popMatrix();

                checkCleanup();
                return;
            }
        }

        EntityPlayerSP entity = Minecraft.getMinecraft().player;
        Vec3d start  = entity.getPositionEyes(partialTicks);
        Vec3d vec31 = entity.getLook(partialTicks);
        Vec3d end = start.addVector(vec31.x * dist, vec31.y * dist, vec31.z * dist);

        mouseOver = entity.getEntityWorld().rayTraceBlocks(start, end, ConfigSetup.showLiquids);
        if (mouseOver == null) {
            return;
        }

        if (mouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
            GlStateManager.pushMatrix();

            double scale = ConfigSetup.tooltipScale;

            ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
            double sw = scaledresolution.getScaledWidth_double();
            double sh = scaledresolution.getScaledHeight_double();

            setupOverlayRendering(sw * scale, sh * scale);
            renderHUDBlock(mode, mouseOver, sw * scale, sh * scale);
            setupOverlayRendering(sw, sh);

            GlStateManager.popMatrix();
        }

        checkCleanup();
    }

    public static void setupOverlayRendering(double sw, double sh) {
        GlStateManager.clear(256);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, sw, sh, 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
    }

    private static void checkCleanup() {
        long time = System.currentTimeMillis();
        if (time > lastCleanupTime + 5000) {
            cleanupCachedBlocks(time);
            cleanupCachedEntities(time);
            lastCleanupTime = time;
        }
    }

    private static void requestEntityInfo(ProbeMode mode, RayTraceResult mouseOver, Entity entity, EntityPlayerSP player) {
        PacketHandler.INSTANCE.sendToServer(new PacketGetEntityInfo(player.getEntityWorld().provider.getDimension(), mode, mouseOver, entity));
    }

    private static boolean handleCacheAndRender(Pair cacheEntry, long time, double sw, double sh, IElement extraElement, UUID entityUUID, BlockPos blockPos, ProbeMode mode, RayTraceResult mouseOver, EntityPlayerSP player, int dimension) {
        if (cacheEntry == null || cacheEntry.getValue() == null) {
            // To make sure we don't ask it too many times before the server got a chance to send the answer
            if (cacheEntry == null || time >= ((Pair<Long, ProbeInfo>) cacheEntry).getLeft()) {
                if (entityUUID != null) {
                    cachedEntityInfo.put(entityUUID, Pair.of(time + 500, null));
                    requestEntityInfo(mode, mouseOver, (Entity) mouseOver.entityHit, player);
                } else if (blockPos != null) {
                    cachedInfo.put(Pair.of(dimension, blockPos), Pair.of(time + 500, null));
                    requestBlockInfo(mode, mouseOver, blockPos, player);
                }
            }

            if (lastPair != null && time < lastPairTime + ConfigSetup.timeout) {
                ResourceLocation backgroundTexture = new ResourceLocation(TheOneProbe.MODID, "textures/gui/probebackground.png");
                renderElements(lastPair.getRight(), ConfigSetup.getDefaultOverlayStyle(), sw, sh, extraElement, backgroundTexture);
                lastRenderedTime = time;
            } else if (ConfigSetup.waitingForServerTimeout > 0 && lastRenderedTime != -1 && time > lastRenderedTime + ConfigSetup.waitingForServerTimeout) {
                ProbeInfo info;
                if (entityUUID != null) {
                    info = getWaitingEntityInfo(mode, mouseOver, (Entity) mouseOver.entityHit, player);
                    registerProbeInfo(entityUUID, info);
                } else {
                    info = getWaitingInfo(mode, mouseOver, blockPos, player);
                    registerProbeInfo(dimension, blockPos, info);
                }
                lastPair = Pair.of(time, info);
                lastPairTime = time;
                ResourceLocation backgroundTexture = new ResourceLocation(TheOneProbe.MODID, "textures/gui/probebackground.png");
                renderElements(info, ConfigSetup.getDefaultOverlayStyle(), sw, sh, extraElement, backgroundTexture);
                lastRenderedTime = time;
            }
            return false;
        } else {
            // Cached info is valid or needs refreshing
            if (time > ((Pair<Long, ProbeInfo>) cacheEntry).getLeft() + ConfigSetup.timeout) {
                if (entityUUID != null) {
                    cachedInfo.put(Pair.of(dimension, blockPos), Pair.of(time + 500, (ProbeInfo) cacheEntry.getRight()));
                    requestEntityInfo(mode, mouseOver, (Entity) mouseOver.entityHit, player);
                } else {
                    cachedInfo.put(Pair.of(dimension, blockPos), Pair.of(time + 500, (ProbeInfo) cacheEntry.getRight()));
                    requestBlockInfo(mode, mouseOver, blockPos, player);
                }
            }
            ResourceLocation backgroundTexture = new ResourceLocation(TheOneProbe.MODID, "textures/gui/probebackground.png");
            renderElements(((Pair<Long, ProbeInfo>) cacheEntry).getRight(), ConfigSetup.getDefaultOverlayStyle(), sw, sh, extraElement, backgroundTexture);
            lastRenderedTime = time;
            lastPair = cacheEntry;
            lastPairTime = time;
            return true;
        }
    }

    private static void renderHUDEntity(ProbeMode mode, RayTraceResult mouseOver, double sw, double sh) {
        Entity entity = mouseOver.entityHit;
        if (entity == null) {
            return;
        }

        UUID uuid = entity.getPersistentID();
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        long time = System.currentTimeMillis();

        Pair<Long, ProbeInfo> cacheEntry = cachedEntityInfo.get(uuid);

        // Delegate common cache handling logic
        handleCacheAndRender(cacheEntry, time, sw, sh, null, uuid, null, mode, mouseOver, player, -1);
    }

    private static void renderHUDBlock(ProbeMode mode, RayTraceResult mouseOver, double sw, double sh) {
        BlockPos blockPos = mouseOver.getBlockPos();
        if (blockPos == null) {
            return;
        }

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player.getEntityWorld().isAirBlock(blockPos)) {
            return;
        }

        long time = System.currentTimeMillis();

        IElement damageElement = null;
        if (ConfigSetup.showBreakProgress > 0) {
            float damage = Minecraft.getMinecraft().playerController.curBlockDamageMP;
            if (damage > 0) {
                damageElement = ConfigSetup.showBreakProgress == 2
                        ? new ElementText("" + TextFormatting.RED + I18n.format("theoneprobe.probe.progress_indicator") + " " + (int) (damage * 100) + "%")
                        : new ElementProgress((long) (damage * 100), 100, new ProgressStyle()
                        .prefix(I18n.format("theoneprobe.probe.progress_indicator") + " ")
                        .suffix("%")
                        .width(85)
                        .borderColor(0)
                        .filledColor(0xff990000)
                        .alternateFilledColor(0xff550000));
            }
        }

        int dimension = player.getEntityWorld().provider.getDimension();
        Pair<Integer, BlockPos> key = Pair.of(dimension, blockPos);
        Pair<Long, ProbeInfo> cacheEntry = cachedInfo.get(key);

        // Delegate common cache handling logic
        handleCacheAndRender(cacheEntry, time, sw, sh, damageElement, null, blockPos, mode, mouseOver, player, dimension);
    }

    // Information for when the server is laggy
    private static ProbeInfo getWaitingInfo(ProbeMode mode, RayTraceResult mouseOver, BlockPos blockPos, EntityPlayerSP player) {
        ProbeInfo probeInfo = TheOneProbe.theOneProbeImp.create();

        World world = player.getEntityWorld();
        IBlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        ItemStack pickBlock = block.getPickBlock(blockState, mouseOver, world, blockPos, player);
        IProbeHitData data = new ProbeHitData(blockPos, mouseOver.hitVec, mouseOver.sideHit, pickBlock);

        IProbeConfig probeConfig = TheOneProbe.theOneProbeImp.createProbeConfig();
        try {
            DefaultProbeInfoProvider.showStandardBlockInfo(probeConfig, mode, probeInfo, blockState, block, data);
        } catch (Exception e) {
            ThrowableIdentity.registerThrowable(e);
            probeInfo.text(ERROR + "{*theoneprobe.probe.error_log_indicator*}");
        }

        probeInfo.text(ERROR + "{*theoneprobe.probe.waiting_server_indicator*}");
        return probeInfo;
    }

    private static ProbeInfo getWaitingEntityInfo(ProbeMode mode, RayTraceResult mouseOver, Entity entity, EntityPlayerSP player) {
        ProbeInfo probeInfo = TheOneProbe.theOneProbeImp.create();
        IProbeHitEntityData data = new ProbeHitEntityData(mouseOver.hitVec);

        IProbeConfig probeConfig = TheOneProbe.theOneProbeImp.createProbeConfig();
        try {
            DefaultProbeInfoEntityProvider.showStandardInfo(mode, probeInfo, entity, probeConfig);
        } catch (Exception e) {
            ThrowableIdentity.registerThrowable(e);
            probeInfo.text(ERROR + "{*theoneprobe.probe.error_log_indicator*}");
        }

        probeInfo.text(ERROR + "{*theoneprobe.probe.waiting_server_indicator*}");
        return probeInfo;
    }

    private static void requestBlockInfo(ProbeMode mode, RayTraceResult mouseOver, BlockPos blockPos, EntityPlayerSP player) {
        World world = player.getEntityWorld();
        IBlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        ItemStack pickBlock = block.getPickBlock(blockState, mouseOver, world, blockPos, player);
        if (pickBlock == null || (!pickBlock.isEmpty() && pickBlock.getItem() == null)) {
            // Protection for some invalid items.
            pickBlock = ItemStack.EMPTY;
        }
        if (pickBlock != null && (!pickBlock.isEmpty()) && ConfigSetup.getDontSendNBTSet().contains(pickBlock.getItem().getRegistryName())) {
            pickBlock = pickBlock.copy();
            pickBlock.setTagCompound(null);
        }
        PacketHandler.INSTANCE.sendToServer(new PacketGetInfo(world.provider.getDimension(), blockPos, mode, mouseOver, pickBlock));
    }

    public static void renderOverlay(IOverlayStyle style, IProbeInfo probeInfo) {
        GlStateManager.pushMatrix();

        double scale = ConfigSetup.getScale();

        Minecraft minecraft = Minecraft.getMinecraft();
        ScaledResolution scaledresolution = new ScaledResolution(minecraft);
        double sw = scaledresolution.getScaledWidth_double();
        double sh = scaledresolution.getScaledHeight_double();

        setupOverlayRendering(sw * scale, sh * scale);
        ResourceLocation backgroundTexture = new ResourceLocation(TheOneProbe.MODID, "textures/gui/probebackground.png");
        renderElements((ProbeInfo) probeInfo, style, sw * scale, sh * scale, null, backgroundTexture);
        setupOverlayRendering(sw, sh);
        GlStateManager.popMatrix();
    }

    private static void cleanupCachedBlocks(long time) {
        // It has been a while. Time to clean up unused cached pairs.
        Map<Pair<Integer,BlockPos>, Pair<Long, ProbeInfo>> newCachedInfo = new HashMap<>();
        for (Map.Entry<Pair<Integer, BlockPos>, Pair<Long, ProbeInfo>> entry : cachedInfo.entrySet()) {
            long t = entry.getValue().getLeft();
            if (time < t + ConfigSetup.timeout + 1000) {
                newCachedInfo.put(entry.getKey(), entry.getValue());
            }
        }
        cachedInfo = newCachedInfo;
    }

    private static void cleanupCachedEntities(long time) {
        // It has been a while. Time to clean up unused cached pairs.
        Map<UUID, Pair<Long, ProbeInfo>> newCachedInfo = new HashMap<>();
        for (Map.Entry<UUID, Pair<Long, ProbeInfo>> entry : cachedEntityInfo.entrySet()) {
            long t = entry.getValue().getLeft();
            if (time < t + ConfigSetup.timeout + 1000) {
                newCachedInfo.put(entry.getKey(), entry.getValue());
            }
        }
        cachedEntityInfo = newCachedInfo;
    }

    public static void renderElements(ProbeInfo probeInfo, IOverlayStyle style, double sw, double sh, @Nullable IElement extra, @Nullable ResourceLocation backgroundTexture) {
        if (extra != null) {
            probeInfo.element(extra);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();

        int scaledWidth = (int) sw;
        int scaledHeight = (int) sh;

        int w = probeInfo.getWidth();
        int h = probeInfo.getHeight();

        int offset = style.getBorderOffset();
        int thick = style.getBorderThickness();
        int margin = 0;
        if (thick > 0) {
            w += (offset + thick + 3) * 2;
            h += (offset + thick + 3) * 2;
            margin = offset + thick + 3;
        }

        int x, y;

        // Horizontal positioning (X-axis)
        if (style.getLeftX() != -1) {
            x = (int) (scaledWidth * (style.getLeftX() / 100.0));
        } else if (style.getRightX() != -1) {
            x = (int) (scaledWidth - w - (scaledWidth * (style.getRightX() / 100.0)));
        } else {
            x = (scaledWidth - w) / 2;
        }

        // Vertical positioning (Y-axis)
        if (style.getTopY() != -1) {
            y = (int) (scaledHeight * (style.getTopY() / 100.0));
        } else if (style.getBottomY() != -1) {
            y = (int) (scaledHeight - h - (scaledHeight * (style.getBottomY() / 100.0)));
        } else {
            y = (scaledHeight - h) / 2;
        }

        // Draw the background image if provided
        if (backgroundTexture != null) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(backgroundTexture);
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, w, h, w, h);
        }

        // Draw borders and the box
        if (thick > 0) {
            if (offset > 0) {
                RenderHelper.drawThickBeveledBox(x, y, x + w - 1, y + h - 1, thick, style.getBoxColor(), style.getBoxColor(), style.getBoxColor());
            }
            RenderHelper.drawThickBeveledBox(x + offset, y + offset, x + w - 1 - offset, y + h - 1 - offset, thick, style.getBorderColor(), style.getBorderColor(), style.getBoxColor());
        }

        if (!Minecraft.getMinecraft().isGamePaused()) {
            RenderHelper.rot += .5f;
        }

        // Render the ProbeInfo elements
        probeInfo.render(x + margin, y + margin);

        if (extra != null) {
            probeInfo.removeElement(extra);
        }
    }
}
