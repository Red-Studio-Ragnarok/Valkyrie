package io.redstudioragnarok.valkyrie.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

import static io.redstudioragnarok.valkyrie.Valkyrie.mc;

@Mixin(RenderGlobal.class)
public class RenderGlobalMixin {

    @Shadow private ViewFrustum viewFrustum;
    @Shadow private ChunkRenderDispatcher renderDispatcher;
    @Shadow private int renderDistanceChunks;
    @Shadow @Final private Set<BlockPos> setLightUpdates;
    @Shadow private Set<RenderChunk> chunksToUpdate;
    @Shadow private List<RenderGlobal.ContainerLocalRenderInformation> renderInfos;

    @Shadow private int getRenderedChunks() { throw new AssertionError(); }

    /**
     * Gets the render info for use on the Debug screen
     *
     * @reason Remove unused `renderChunksMany` info as `renderChunksMany` is always true
     * @author Desoroxxx
     */
    @Overwrite
    public String getDebugInfoRenders() {
        return String.format("C: %d/%d D: %d, L: %d, %s", getRenderedChunks(), viewFrustum.renderChunks.length, renderDistanceChunks, setLightUpdates.size(), renderDispatcher == null ? "null" : renderDispatcher.getDebugInfo());
    }

    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Ljava/util/Queue;isEmpty()Z"))
    private boolean disableOriginalIteration(final Queue<RenderGlobal.ContainerLocalRenderInformation> queue) {
        return true;
    }

    @Inject(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V", ordinal = 1, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void valkyrieIteration(final Entity viewEntity, final double partialTicks, final ICamera camera, final int frameCount, final boolean playerSpectator, final CallbackInfo callbackInfo, @Local(ordinal = 1) final BlockPos blockPos, @Local(ordinal = 0) final Queue<RenderGlobal.ContainerLocalRenderInformation> queue, @Local(ordinal = 2) boolean renderChunksMany) {
        RenderGlobal.ContainerLocalRenderInformation currentRenderInfo;
        RenderChunk currentChunk, adjacentChunk;
        EnumFacing currentDirection;
        RenderGlobal.ContainerLocalRenderInformation newRenderInfo;

        final HashSet<RenderChunk> processedChunks = new HashSet<>();

        while ((currentRenderInfo = queue.poll()) != null) {
            currentChunk = currentRenderInfo.renderChunk;
            currentDirection = currentRenderInfo.facing;

            renderInfos.add(currentRenderInfo);

            for (EnumFacing nextDirection : EnumFacing.VALUES) {
                adjacentChunk = getRenderChunkOffset(blockPos, currentChunk, nextDirection);

                if (processedChunks.contains(adjacentChunk))
                    continue;

                if (adjacentChunk != null && camera.isBoundingBoxInFrustum(adjacentChunk.boundingBox) && (!renderChunksMany || !currentRenderInfo.hasDirection(nextDirection.getOpposite())) && (!renderChunksMany || currentDirection == null || currentChunk.getCompiledChunk().isVisible(currentDirection.getOpposite(), nextDirection)) && adjacentChunk.setFrameIndex(frameCount)) {
                    newRenderInfo = mc.renderGlobal.new ContainerLocalRenderInformation(adjacentChunk, nextDirection, currentRenderInfo.counter + 1);
                    newRenderInfo.setDirection(currentRenderInfo.setFacing, nextDirection);

                    queue.add(newRenderInfo);
                    processedChunks.add(adjacentChunk);
                }
            }
        }
    }

    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"))
    private boolean replaceContain(final Set<RenderChunk> set, final Object renderChunk) {
        return chunksToUpdate.contains((RenderChunk) renderChunk);
    }

    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Ljava/util/Set;addAll(Ljava/util/Collection;)Z"))
    private boolean skipAddAll(final Set<RenderChunk> set, final Collection<RenderChunk> collection) {
        return true;
    }

    /**
     * @reason Make this faster by using bitwise operators
     * @author Desoroxxx
     */
    @Overwrite
    private RenderChunk getRenderChunkOffset(final BlockPos playerPos, final RenderChunk renderChunkBase, final EnumFacing facing) {
        final BlockPos blockpos = renderChunkBase.getBlockPosOffset16(facing);

        if (MathHelper.abs(playerPos.getX() - blockpos.getX()) > this.renderDistanceChunks << 4)
            return null;
        else if (blockpos.getY() >= 0 && blockpos.getY() < 256)
            return MathHelper.abs(playerPos.getZ() - blockpos.getZ()) > this.renderDistanceChunks << 4 ? null : this.viewFrustum.getRenderChunk(blockpos);
        else
            return null;
    }
}
