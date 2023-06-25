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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import static io.redstudioragnarok.valkyrie.Valkyrie.mc;

@Mixin(RenderGlobal.class)
public class RenderGlobalMixin {

    @Shadow private ViewFrustum viewFrustum;
    @Shadow private ChunkRenderDispatcher renderDispatcher;
    @Shadow private int renderDistanceChunks = -1;
    @Shadow @Final private Set<BlockPos> setLightUpdates;
    @Shadow private Set<RenderChunk> chunksToUpdate;
    @Shadow private List<RenderGlobal.ContainerLocalRenderInformation> renderInfos;

    @Shadow private int getRenderedChunks() { throw new AssertionError(); }
    @Shadow private RenderChunk getRenderChunkOffset(BlockPos playerPos, RenderChunk renderChunkBase, EnumFacing facing) { throw new AssertionError(); }

    private static final EnumFacing[] ENUM_FACING_VALUES = EnumFacing.values();

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
    private boolean disableOriginalIteration(Queue<RenderGlobal.ContainerLocalRenderInformation> queue) {
        return true;
    }

    @Inject(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V", ordinal = 1, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void valkyrieIteration(final Entity viewEntity, final double partialTicks, final ICamera camera, final int frameCount, final boolean playerSpectator, final CallbackInfo callbackInfo, @Local(ordinal = 1) final BlockPos blockpos, @Local(ordinal = 0) final Queue<RenderGlobal.ContainerLocalRenderInformation> queue, @Local(ordinal = 1) final boolean flag1) {
        while (!queue.isEmpty()) {
            final RenderGlobal.ContainerLocalRenderInformation currentRenderInfo = queue.poll();
            final RenderChunk currentChunk = currentRenderInfo.renderChunk;
            final EnumFacing currentDirection = currentRenderInfo.facing;

            this.renderInfos.add(currentRenderInfo);

            for (EnumFacing nextDirection : ENUM_FACING_VALUES) {
                final RenderChunk adjacentChunk = this.getRenderChunkOffset(blockpos, currentChunk, nextDirection);

                if ((!flag1 || !currentRenderInfo.hasDirection(nextDirection.getOpposite())) && (!flag1 || currentDirection == null || currentChunk.getCompiledChunk().isVisible(currentDirection.getOpposite(), nextDirection)) && adjacentChunk != null && adjacentChunk.setFrameIndex(frameCount) && camera.isBoundingBoxInFrustum(adjacentChunk.boundingBox)) {
                    final RenderGlobal.ContainerLocalRenderInformation newRenderInfo = mc.renderGlobal.new ContainerLocalRenderInformation(adjacentChunk, nextDirection, currentRenderInfo.counter + 1);
                    newRenderInfo.setDirection(currentRenderInfo.setFacing, nextDirection);

                    queue.add(newRenderInfo);
                }
            }
        }
    }

    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"))
    private boolean disableChunkUpdateQueueReplacement(final Set<RenderChunk> set, final Object renderChunk) {
        return chunksToUpdate.contains((RenderChunk) renderChunk);
    }

    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Ljava/util/Set;addAll(Ljava/util/Collection;)Z"))
    private boolean skipAddAll(final Set<RenderChunk> set, final Collection<RenderChunk> collection) {
        return true;
    }
}
