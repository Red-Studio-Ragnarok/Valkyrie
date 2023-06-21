package io.redstudioragnarok.valkyrie.mixin;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(RenderGlobal.class)
public class RenderGlobalMixin {

    @Shadow private ViewFrustum viewFrustum;
    @Shadow private ChunkRenderDispatcher renderDispatcher;
    @Shadow private int renderDistanceChunks = -1;
    @Shadow @Final private Set<BlockPos> setLightUpdates;
    @Shadow private Set<RenderChunk> chunksToUpdate;

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

    /**
     * Parallelize adding the chunks to update in the `chunksToUpdate` set, this greatly improves performance on high-render distances.
     * <p>
     * Sparkc report that lead me to this: <a href="https://spark.lucko.me/YxY4EQlvGp?hl=89">report</a>
     */
    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Ljava/util/Set;addAll(Ljava/util/Collection;)Z"))
    private boolean fasterAddAll(Set set, Collection collection) {
        chunksToUpdate = (Set<RenderChunk>) Stream.concat(this.chunksToUpdate.parallelStream(), collection.parallelStream()).collect(Collectors.toCollection(LinkedHashSet::new));

        return true;
    }
}
