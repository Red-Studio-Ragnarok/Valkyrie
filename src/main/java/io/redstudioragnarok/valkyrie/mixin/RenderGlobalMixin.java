package io.redstudioragnarok.valkyrie.mixin;

import io.redstudioragnarok.valkyrie.Valkyrie;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(RenderGlobal.class)
public abstract class RenderGlobalMixin {

    @Shadow private ViewFrustum viewFrustum;
    @Shadow private ChunkRenderDispatcher renderDispatcher;
    @Shadow private int renderDistanceChunks = -1;
    @Shadow @Final private Set<BlockPos> setLightUpdates;
    @Shadow private int cloudTickCounter;

    @Shadow protected abstract int getRenderedChunks();

    /**
     * Gets the render info for use on the Debug screen
     *
     * @reason Update the F3 screen according other changes
     * @author Desoroxxx
     */
    @Overwrite
    public String getDebugInfoRenders() {
        return String.format("C: %d/%d D: %d, L: %d, %s", getRenderedChunks(), viewFrustum.renderChunks.length, renderDistanceChunks, setLightUpdates.size(), renderDispatcher == null ? "null" : renderDispatcher.getDebugInfo());
    }

    /**
     * @reason Improving performance
     * @author Desoroxxx
     */
    @Overwrite
    public void renderClouds(float partialTicks, int pass, double x, double y, double z) {
        Valkyrie.getCloudRenderer().render(cloudTickCounter, partialTicks);
    }
}
