package mcjty.theoneprobe.rendering;

import lombok.NonNull;
import mcjty.theoneprobe.ClientTools;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.ProbeHitData;
import mcjty.theoneprobe.apiimpl.ProbeInfo;
import mcjty.theoneprobe.apiimpl.elements.ElementProgress;
import mcjty.theoneprobe.apiimpl.elements.ElementText;
import mcjty.theoneprobe.apiimpl.providers.DefaultProbeInfoEntityProvider;
import mcjty.theoneprobe.apiimpl.providers.DefaultProbeInfoProvider;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.network.PacketGetEntityInfo;
import mcjty.theoneprobe.network.PacketGetInfo;
import mcjty.theoneprobe.network.PacketHandler;
import mcjty.theoneprobe.network.ThrowableIdentity;
import mcjty.theoneprobe.setup.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static mcjty.theoneprobe.api.TextStyleClass.ERROR;

public class OverlayRenderer {

    // Use concurrent maps to reduce synchronization costs and avoid frequent reallocation
    private static final Map<Pair<Integer, BlockPos>, Pair<Long, ProbeInfo>> cachedInfo = new ConcurrentHashMap<>();
    private static final Map<UUID, Pair<Long, ProbeInfo>> cachedEntityInfo = new ConcurrentHashMap<>();

    // housekeeping
    private static volatile long lastCleanupTime = 0L;

    /** For a short while we keep displaying the last pair if we have no new information to prevent flickering */
    private static volatile Pair<Long, ProbeInfo> lastPair = null;
    private static volatile long lastPairTime = 0L;

    /** When the server delays too long we also show some preliminary information already */
    private static volatile long lastRenderedTime = -1L;

    // ------------------------------
    // registration called from network handlers
    // ------------------------------

    /**
     * Registers a block's probe information in the cache.
     *
     * @param dim       The dimension ID where the block is located.
     * @param pos       The position of the block.
     * @param probeInfo The information about the block to cache.
     */
    public static void registerProbeInfo(int dim, BlockPos pos, ProbeInfo probeInfo) {
        if (probeInfo == null || pos == null) return;
        long time = currentTimeMillis();
        putBlockCache(Pair.of(dim, pos), time, probeInfo);
    }

    /**
     * Registers an entity's probe information in the cache.
     *
     * @param uuid      The UUID of the entity.
     * @param probeInfo The information about the entity to cache.
     */
    public static void registerProbeInfo(UUID uuid, ProbeInfo probeInfo) {
        if (probeInfo == null || uuid == null) return;
        long time = currentTimeMillis();
        putEntityCache(uuid, time, probeInfo);
    }

    // ------------------------------
    // main HUD render entry
    // ------------------------------

    /**
     * Renders the HUD overlay showing probe information for blocks or entities.
     *
     * @param mode         The probe mode to use (e.g., NORMAL, EXTENDED).
     * @param partialTicks Partial tick time for smooth entity position interpolation.
     */
    public static void renderHUD(ProbeMode mode, float partialTicks) {
        if (ClientTools.MC.gameSettings.showDebugInfo) {
            return;
        }

        // Cache values we will read often
        final double tooltipScale = Config.tooltipScale;
        final double maxDistance = Config.probeDistance;
        ScaledResolution scaledresolution = new ScaledResolution(ClientTools.MC);
        final double screenW = scaledresolution.getScaledWidth_double();
        final double screenH = scaledresolution.getScaledHeight_double();

        RayTraceResult mouseOver = ClientTools.MC.objectMouseOver;
        if (mouseOver != null && mouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
            GlStateManager.pushMatrix();
            setupOverlayRenderingScaled(screenW, screenH, tooltipScale);
            renderHUDEntity(mode, mouseOver, screenW * tooltipScale, screenH * tooltipScale);
            setupOverlayRendering(screenW, screenH);
            GlStateManager.popMatrix();
            checkCleanup();
            return;
        }

        // Raytrace for blocks from the player's eyes (so we find blocks at a configurable distance)
        EntityPlayerSP player = ClientTools.MC.player;
        if (player == null) {
            checkCleanup();
            return;
        }

        Vec3d start = player.getPositionEyes(partialTicks);
        Vec3d look = player.getLook(partialTicks);
        Vec3d end = start.add(look.x * maxDistance, look.y * maxDistance, look.z * maxDistance);
        mouseOver = player.getEntityWorld().rayTraceBlocks(start, end, Config.showLiquids);

        if (mouseOver == null) {
            checkCleanup();
            return;
        }

        if (mouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
            GlStateManager.pushMatrix();
            setupOverlayRenderingScaled(screenW, screenH, tooltipScale);
            renderHUDBlock(mode, mouseOver, screenW * tooltipScale, screenH * tooltipScale);
            setupOverlayRendering(screenW, screenH);
            GlStateManager.popMatrix();
        }

        checkCleanup();
    }

    // ------------------------------
    // low-level GL helpers
    // ------------------------------

    /**
     * Sets up OpenGL state for 2D overlay rendering.
     *
     * @param sw Screen width.
     * @param sh Screen height.
     */
    public static void setupOverlayRendering(double sw, double sh) {
        GlStateManager.clear(256);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, sw, sh, 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
    }


    /**
     * Sets up OpenGL state for 2D overlay rendering with a scaling factor.
     *
     * @param sw    Screen width.
     * @param sh    Screen height.
     * @param scale Scale factor for the overlay.
     */
    private static void setupOverlayRenderingScaled(double sw, double sh, double scale) {
        setupOverlayRendering(sw * scale, sh * scale);
    }

    // ------------------------------
    // cleanup expired cache entries (in-place to avoid allocations)
    // ------------------------------

    /** Cleans up expired cache entries for blocks and entities. */
    private static void checkCleanup() {
        long now = currentTimeMillis();
        if (now > lastCleanupTime + 5_000L) {
            cleanupCachedBlocks(now);
            cleanupCachedEntities(now);
            lastCleanupTime = now;
        }
    }

    /**
     * Removes block cache entries that have expired.
     *
     * @param now Current time in milliseconds.
     */
    private static void cleanupCachedBlocks(long now) {
        long expiryWindow = Config.blockTimeout + 1_000L;
        Iterator<Map.Entry<Pair<Integer, BlockPos>, Pair<Long, ProbeInfo>>> it = cachedInfo.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Pair<Integer, BlockPos>, Pair<Long, ProbeInfo>> e = it.next();
            long t = e.getValue().getLeft();
            if (now >= t + expiryWindow) {
                it.remove();
            }
        }
    }

    /**
     * Removes entity cache entries that have expired.
     *
     * @param now Current time in milliseconds.
     */
    private static void cleanupCachedEntities(long now) {
        long expiryWindow = Config.entityTimeout + 1_000L;
        Iterator<Map.Entry<UUID, Pair<Long, ProbeInfo>>> it = cachedEntityInfo.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Pair<Long, ProbeInfo>> e = it.next();
            long t = e.getValue().getLeft();
            if (now >= t + expiryWindow) {
                it.remove();
            }
        }
    }

    // ------------------------------
    // entity request / block request helpers
    // ------------------------------

    /**
     * Sends a request to the server to get probe info for an entity.
     *
     * @param mode      Probe mode.
     * @param mouseOver Ray trace result of the entity.
     * @param entity    The entity being probed.
     * @param player    The local player sending the request.
     */
    private static void requestEntityInfo(ProbeMode mode, RayTraceResult mouseOver, Entity entity, EntityPlayerSP player) {
        PacketHandler.INSTANCE.sendToServer(new PacketGetEntityInfo(player.getEntityWorld().provider.getDimension(), mode, mouseOver, entity));
    }

    /**
     * Sends a request to the server to get probe info for a block.
     *
     * @param mode      Probe mode.
     * @param mouseOver Ray trace result of the block.
     * @param blockPos  Position of the block.
     * @param player    The local player sending the request.
     */
    private static void requestBlockInfo(@NonNull ProbeMode mode, @NonNull RayTraceResult mouseOver, @NonNull BlockPos blockPos, @NonNull EntityPlayerSP player) {
        World world = player.getEntityWorld();
        IBlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        ItemStack pickBlock = block.getPickBlock(blockState, mouseOver, world, blockPos, player);

        // Protect against invalid items and remove NBT if configured
        if (pickBlock == null || (!pickBlock.isEmpty() && pickBlock.getItem() == null)) {
            pickBlock = ItemStack.EMPTY;
        }
        if (!pickBlock.isEmpty() && Config.getDontSendNBTSet().contains(pickBlock.getItem().getRegistryName())) {
            pickBlock = pickBlock.copy();
            pickBlock.setTagCompound(null);
        }

        PacketHandler.INSTANCE.sendToServer(new PacketGetInfo(world.provider.getDimension(), blockPos, mode, mouseOver, pickBlock));
    }

    // ------------------------------
    // render for entities
    // ------------------------------

    /**
     * Renders HUD overlay for an entity under the crosshair.
     *
     * @param mode  Probe mode.
     * @param mouseOver Ray trace result of the entity.
     * @param sw    Screen width scaled for overlay.
     * @param sh    Screen height scaled for overlay.
     */
    private static void renderHUDEntity(ProbeMode mode, RayTraceResult mouseOver, double sw, double sh) {
        Entity entity = mouseOver.entityHit;
        if (entity == null) return;

        if (entity instanceof MultiPartEntityPart) {
            MultiPartEntityPart part = (MultiPartEntityPart) entity;
            if (part.parent instanceof Entity) {
                entity = (Entity) part.parent;
            }
        }

        UUID uuid = entity.getPersistentID();
        EntityPlayerSP player = ClientTools.MC.player;
        if (player == null) return;

        long now = currentTimeMillis();
        Pair<Long, ProbeInfo> cacheEntry = cachedEntityInfo.get(uuid);

        handleEntityCacheAndRender(cacheEntry, now, sw, sh, uuid, mode, mouseOver, player, entity);
    }

    /**
     * Handles cached entity info and renders it. If no info is cached, sends a request to the server.
     *
     * @param cacheEntry Cached entity info or null.
     * @param now        Current time in milliseconds.
     * @param sw         Screen width scaled for overlay.
     * @param sh         Screen height scaled for overlay.
     * @param uuid       Entity UUID.
     * @param mode       Probe mode.
     * @param mouseOver  Ray trace result.
     * @param player     Local player.
     * @param entity     Entity being rendered.
     */
    private static void handleEntityCacheAndRender(@Nullable Pair<Long, ProbeInfo> cacheEntry,
                                                   long now, double sw, double sh,
                                                   @NonNull UUID uuid,
                                                   ProbeMode mode,
                                                   RayTraceResult mouseOver,
                                                   EntityPlayerSP player,
                                                   Entity entity) {
        // If no cache or waiting marker present: enqueue a request and possibly show placeholder
        if (cacheEntry == null || cacheEntry.getValue() == null) {
            // ensure we don't spam the server: write a "waiting" marker into cache with a short delay
            Pair<Long, ProbeInfo> marker = cachedEntityInfo.get(uuid);
            if (marker == null || now >= marker.getLeft()) {
                putEntityCache(uuid, now + 500L, null);
                requestEntityInfo(mode, mouseOver, entity, player);
            }

            // If we have a recent lastPair we can show it to avoid flicker
            if (lastPair != null && now < lastPairTime + Config.entityTimeout) {
                renderElements(lastPair.getRight(), Config.getDefaultOverlayStyle(), sw, sh, null);
                lastRenderedTime = now;
            } else if (Config.waitingForServerTimeout > 0 && lastRenderedTime != -1 && now > lastRenderedTime + Config.waitingForServerTimeout) {
                ProbeInfo info = getWaitingEntityInfo(mode, mouseOver, entity, player);
                registerProbeInfo(uuid, info);
                lastPair = Pair.of(now, info);
                lastPairTime = now;
                renderElements(info, Config.getDefaultOverlayStyle(), sw, sh, null);
                lastRenderedTime = now;
            }
            return;
        }

        // Cache exists and has a ProbeInfo
        long cachedAt = cacheEntry.getLeft();
        ProbeInfo info = cacheEntry.getRight();

        // If cached info is older than timeout, schedule a refresh (leave the info in cache so UI doesn't flicker)
        if (!Minecraft.getMinecraft().isGamePaused() && now > cachedAt + Config.entityTimeout) {
            putEntityCache(uuid, now + 500L, info);
            requestEntityInfo(mode, mouseOver, entity, player);
        }

        renderElements(info, Config.getDefaultOverlayStyle(), sw, sh, null);
        lastRenderedTime = now;
        lastPair = Pair.of(now, info);
        lastPairTime = now;
    }

    // ------------------------------
    // render for blocks
    // ------------------------------

    /**
     * Renders HUD overlay for a block under the crosshair.
     *
     * @param mode  Probe mode.
     * @param mouseOver Ray trace result of the block.
     * @param sw    Screen width scaled for overlay.
     * @param sh    Screen height scaled for overlay.
     */
    private static void renderHUDBlock(ProbeMode mode, RayTraceResult mouseOver, double sw, double sh) {
        BlockPos blockPos = mouseOver.getBlockPos();
        if (blockPos == null) return;

        EntityPlayerSP player = ClientTools.MC.player;
        if (player == null) return;

        if (player.getEntityWorld().isAirBlock(blockPos)) return;

        long now = currentTimeMillis();

        // Build optional break-progress element
        IElement damageElement = null;
        if (Config.showBreakProgress > 0) {
            float damage = ClientTools.MC.playerController.curBlockDamageMP;
            if (damage > 0.0f) {
                if (Config.showBreakProgress == 2) {
                    damageElement = new ElementText(TextFormatting.RED + I18n.format("theoneprobe.probe.progress_indicator") + " " + (int) (damage * 100.0f) + "%");
                } else {
                    damageElement = new ElementProgress((long) (damage * 100.0f), 100, new ProgressStyle()
                            .prefix(I18n.format("theoneprobe.probe.progress_indicator") + " ")
                            .suffix("%")
                            .width(85)
                            .showText(Config.showBreakProgressText)
                            .backgroundColor(Config.probeProgressBackgroundColor)
                            .borderColor(Config.probeProgressBorderColor)
                            .filledColor(Config.probeProgressColor)
                            .alternateFilledColor(Config.probeProgressAltColor));
                }
            }
        }

        int dimension = player.getEntityWorld().provider.getDimension();
        Pair<Integer, BlockPos> key = Pair.of(dimension, blockPos);
        Pair<Long, ProbeInfo> cacheEntry = cachedInfo.get(key);

        handleBlockCacheAndRender(cacheEntry, now, sw, sh, damageElement, key, mode, mouseOver, player);
    }

    /**
     * Handles cached block info and renders it. If no info is cached, sends a request to the server.
     *
     * @param cacheEntry  Cached block info or null.
     * @param now         Current time in milliseconds.
     * @param sw          Screen width scaled for overlay.
     * @param sh          Screen height scaled for overlay.
     * @param extraElement Optional extra element to render (e.g., block damage).
     * @param key         Cache key for block (dimension + position).
     * @param mode        Probe mode.
     * @param mouseOver   Ray trace result.
     * @param player      Local player.
     */
    private static void handleBlockCacheAndRender(@Nullable Pair<Long, ProbeInfo> cacheEntry,
                                                  long now, double sw, double sh,
                                                  @Nullable IElement extraElement,
                                                  @NonNull Pair<Integer, BlockPos> key,
                                                  ProbeMode mode,
                                                  RayTraceResult mouseOver,
                                                  EntityPlayerSP player) {
        BlockPos blockPos = key.getRight();
        int dimension = key.getLeft();

        if (cacheEntry == null || cacheEntry.getValue() == null) {
            Pair<Long, ProbeInfo> marker = cachedInfo.get(key);
            if (marker == null || now >= marker.getLeft()) {
                putBlockCache(key, now + 500L, null);
                requestBlockInfo(mode, mouseOver, blockPos, player);
            }

            if (lastPair != null && now < lastPairTime + Config.blockTimeout) {
                renderElements(lastPair.getRight(), Config.getDefaultOverlayStyle(), sw, sh, extraElement);
                lastRenderedTime = now;
            } else if (Config.waitingForServerTimeout > 0 && lastRenderedTime != -1 && now > lastRenderedTime + Config.waitingForServerTimeout) {
                ProbeInfo info = getWaitingInfo(mode, mouseOver, blockPos, player);
                registerProbeInfo(dimension, blockPos, info);
                lastPair = Pair.of(now, info);
                lastPairTime = now;
                renderElements(info, Config.getDefaultOverlayStyle(), sw, sh, extraElement);
                lastRenderedTime = now;
            }
            return;
        }

        // Cache entry is present
        long cachedAt = cacheEntry.getLeft();
        ProbeInfo info = cacheEntry.getRight();

        if (!Minecraft.getMinecraft().isGamePaused() && now > cachedAt + Config.blockTimeout) {
            // refresh in background, keep showing current info
            putBlockCache(key, now + 500L, info);
            requestBlockInfo(mode, mouseOver, blockPos, player);
        }

        renderElements(info, Config.getDefaultOverlayStyle(), sw, sh, extraElement);
        lastRenderedTime = now;
        lastPair = Pair.of(now, info);
        lastPairTime = now;
    }

    // ------------------------------
    // "waiting" info helpers used when the server takes too long
    // ------------------------------

    /**
     * Generates placeholder probe info for a block when the server takes too long to respond.
     *
     * @param mode     Probe mode.
     * @param mouseOver Ray trace result.
     * @param blockPos Block position.
     * @param player   Local player.
     * @return ProbeInfo placeholder.
     */
    private static ProbeInfo getWaitingInfo(ProbeMode mode, RayTraceResult mouseOver, BlockPos blockPos, EntityPlayerSP player) {
        ProbeInfo probeInfo = TheOneProbe.theOneProbeImp.create();

        World world = player.getEntityWorld();
        IBlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        ItemStack pickBlock = block.getPickBlock(blockState, mouseOver, world, blockPos, player);
        IProbeHitData data = new ProbeHitData(blockPos, mouseOver.hitVec, mouseOver.sideHit, pickBlock);

        IProbeConfig probeConfig = TheOneProbe.theOneProbeImp.createProbeConfig();
        try {
            DefaultProbeInfoProvider.showStandardBlockInfo(probeConfig, mode, probeInfo, blockState, block, data, world);
        } catch (Exception e) {
            ThrowableIdentity.registerThrowable(e);
            probeInfo.text(ERROR + "{*theoneprobe.probe.error_log_indicator*}");
        }

        probeInfo.text(ERROR + "{*theoneprobe.probe.waiting_server_indicator*}");
        return probeInfo;
    }

    /**
     * Generates placeholder probe info for an entity when the server takes too long to respond.
     *
     * @param mode     Probe mode.
     * @param mouseOver Ray trace result.
     * @param entity   Entity being probed.
     * @param player   Local player.
     * @return ProbeInfo placeholder.
     */
    private static ProbeInfo getWaitingEntityInfo(ProbeMode mode, RayTraceResult mouseOver, Entity entity, EntityPlayerSP player) {
        ProbeInfo probeInfo = TheOneProbe.theOneProbeImp.create();

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

    // ------------------------------
    // public overlay render API
    // ------------------------------

    /**
     * Renders a ProbeInfo overlay on the screen with the given style.
     *
     * @param style     Style for the overlay.
     * @param probeInfo Probe information to render.
     */
    public static void renderOverlay(IOverlayStyle style, IProbeInfo probeInfo) {
        GlStateManager.pushMatrix();
        double scale = Config.getTooltipScale();

        ScaledResolution scaledresolution = new ScaledResolution(ClientTools.MC);
        double sw = scaledresolution.getScaledWidth_double();
        double sh = scaledresolution.getScaledHeight_double();

        setupOverlayRendering(sw * scale, sh * scale);
        renderElements((ProbeInfo) probeInfo, style, sw * scale, sh * scale, null);
        setupOverlayRendering(sw, sh);
        GlStateManager.popMatrix();
    }

    // ------------------------------
    // final rendering of elements (unchanged structurally)
    // ------------------------------

    /**
     * Renders the elements inside a ProbeInfo object.
     *
     * @param probeInfo Probe info object containing elements.
     * @param style     Overlay style.
     * @param sw        Screen width scaled for overlay.
     * @param sh        Screen height scaled for overlay.
     * @param extra     Optional extra element to render.
     */
    public static void renderElements(ProbeInfo probeInfo, IOverlayStyle style, double sw, double sh, @Nullable IElement extra) {
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

        int x;
        int y;

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

        // Draw borders and the box
        if (thick > 0) {
            if (offset > 0) {
                RenderHelper.drawThickBeveledBox(x, y, x + w - 1, y + h - 1, thick, style.getBoxColor(), style.getBoxColor(), style.getBoxColor());
            }
            RenderHelper.drawThickBeveledBox(x + offset, y + offset, x + w - 1 - offset, y + h - 1 - offset, thick, style.getBorderColor(), style.getBorderColor(), style.getBoxColor());
        }

        if (!ClientTools.MC.isGamePaused()) {
            RenderHelper.rot += .5f;
        }

        // Render the ProbeInfo elements
        probeInfo.render(x + margin, y + margin);

        if (extra != null) {
            probeInfo.removeElement(extra);
        }
    }

    // ------------------------------
    // utilities
    // ------------------------------
    private static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * Updates the cached block information and optionally logs it.
     *
     * @param key  Cache key (dimension + position).
     * @param time Timestamp of the cache.
     * @param info ProbeInfo to store.
     */
    private static void putBlockCache(Pair<Integer, BlockPos> key, long time, ProbeInfo info) {
        Pair<Long, ProbeInfo> value = Pair.of(time, info);
        cachedInfo.put(key, value);
        CommonProxy.getLogger().debug("Updated block cache: dim={} pos={} time={} infoPresent={}", key.getLeft(), key.getRight(), value.getLeft(), value.getRight() != null);
    }

    /**
     * Updates the cached entity information and optionally logs it.
     *
     * @param uuid Entity UUID.
     * @param time Timestamp of the cache.
     * @param info ProbeInfo to store.
     */
    private static void putEntityCache(UUID uuid, long time, ProbeInfo info) {
        Pair<Long, ProbeInfo> value = Pair.of(time, info);
        cachedEntityInfo.put(uuid, value);
        CommonProxy.getLogger().debug("Updated entity cache: uuid={} time={} infoPresent={}", uuid, value.getLeft(), value.getRight() != null);
    }
}
