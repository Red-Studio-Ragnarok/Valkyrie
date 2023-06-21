package io.redstudioragnarok.valkyrie.mixin;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ViewFrustum.class)
public class ViewFrustumMixin {

    @Shadow @Final protected RenderGlobal renderGlobal;
    @Shadow @Final protected World world;
    @Shadow protected int countChunksY;
    @Shadow protected int countChunksX;
    @Shadow protected int countChunksZ;
    @Shadow public RenderChunk[] renderChunks;

    /**
     * @reason Improving the performance of this method by using bitwise operators since render chunk size is always the same.
     *         Improving the performance of this method is beneficial for FPS stability, as it reduces FPS drops when a lot of blocks are marked for update.
     *         For example, when loading in a world, teleporting, moving really fast or when using mods that changes a lot of blocks all at once.
     * @reason Using @Overwrite to not have overhead; I assume that nearly no mods will Mixin it anyway, this could easily be an injection if incompatibilities are found.
     * @author Desoroxxx
     */
    @Overwrite
    public void markBlocksForUpdate(final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ, final boolean updateImmediately) {
        final int chunkMinX = minX >> 4;
        final int chunkMinY = minY >> 4;
        final int chunkMinZ = minZ >> 4;
        final int chunkMaxX = maxX >> 4;
        final int chunkMaxY = maxY >> 4;
        final int chunkMaxZ = maxZ >> 4;

        for (int x = chunkMinX; x <= chunkMaxX; ++x) {
            final int normalizedX = (x % this.countChunksX + this.countChunksX) % this.countChunksX;

            for (int y = chunkMinY; y <= chunkMaxY; ++y) {
                final int normalizedY = (y % this.countChunksY + this.countChunksY) % this.countChunksY;

                for (int z = chunkMinZ; z <= chunkMaxZ; ++z) {
                    final int normalizedZ = (z % this.countChunksZ + this.countChunksZ) % this.countChunksZ;

                    RenderChunk renderchunk = this.renderChunks[(normalizedZ * this.countChunksY + normalizedY) * this.countChunksX + normalizedX];
                    renderchunk.setNeedsUpdate(updateImmediately);
                }
            }
        }
    }

    /**
     * @reason Improving the performance of this method by using bitwise operators since render chunk size is always the same.
     *         Making this faster improves the speed of loading render chunks and thus makes it less likely to have so far away chunks never load.
     * @reason Using @Overwrite to not have overhead, I assume that nearly no mods will Mixin it anyway, this could easily be an injection if incompatibilities are found.
     * @author Desoroxxx
     */
    @Overwrite
    protected RenderChunk getRenderChunk(final BlockPos blockPos) {
        int x = blockPos.getX() >> 4;
        final int y = blockPos.getY() >> 4;
        int z = blockPos.getZ() >> 4;

        if (y >= 0 && y < countChunksY) {
            x = (x % countChunksX + countChunksX) % countChunksX;
            z = (z % countChunksZ + countChunksZ) % countChunksZ;

            return renderChunks[(z * countChunksY + y) * countChunksX + x];
        } else
            return null;
    }
}
