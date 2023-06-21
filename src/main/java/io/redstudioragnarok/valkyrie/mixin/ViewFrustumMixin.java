package io.redstudioragnarok.valkyrie.mixin;

import net.jafama.FastMath;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

// Todo: Look into just replacing the entire class with ASM
@Mixin(ViewFrustum.class)
public class ViewFrustumMixin {

    @Shadow @Final protected RenderGlobal renderGlobal;
    @Shadow @Final protected World world;
    @Shadow protected int countChunksY;
    @Shadow protected int countChunksX;
    @Shadow protected int countChunksZ;
    @Shadow public RenderChunk[] renderChunks;

    /**
     * @reason Improving the performance of this method by using a single loop instead of multiple nested ones and avoiding allocating in loop.
     *         Improving the performance of this method is beneficial as it reduces lag when loading renderer.
     *         For example, when loading in a world, changing the render distance, changing graphics quality, etc.
     * @reason Using @Overwrite to have no overhead, I assume that nearly no mods will Mixin it anyway, this could easily be an injection if incompatibilities are found.
     * @author Desoroxxx
     */
    @Overwrite
    protected void createRenderChunks(IRenderChunkFactory renderChunkFactory) {
        final int totalRenderChunks = countChunksX * countChunksY * countChunksZ;

        int xChunkIndex = 0;
        int yChunkIndex = 0;
        int zChunkIndex = 0;

        renderChunks = new RenderChunk[totalRenderChunks];

        for (int i = 0; i < totalRenderChunks; ++i) {
            if (xChunkIndex == countChunksX) {
                xChunkIndex = 0;
                ++yChunkIndex;
                if (yChunkIndex == countChunksY) {
                    yChunkIndex = 0;
                    ++zChunkIndex;
                }
            }

            renderChunks[i] = new RenderChunk(world, renderGlobal, i);
            renderChunks[i].setPosition(xChunkIndex * 16, yChunkIndex * 16, zChunkIndex * 16);

            ++xChunkIndex;
        }
    }

    /**
     * @reason Improving the performance of this method by using bitwise operators since render chunk size is always the same.
     *         Improving the performance of this method is beneficial as it reduces lag when loading renderer.
     *         For example, when loading in a world, changing the render distance, changing graphics quality, etc.
     *         Not only that but it is also used when updating the frustum which is done each time the viewEntity changes position.
     * @reason Using @Overwrite to have no overhead, I assume that nearly no mods will Mixin it anyway, this could easily be an injection if incompatibilities are found.
     * @author Desoroxxx
     */
    @Overwrite
    public void updateChunkPositions(double viewEntityX, double viewEntityZ) {
        final int baseX = (int) (FastMath.floor(viewEntityX) - 8);
        final int baseZ = (int) (FastMath.floor(viewEntityZ) - 8);
        final int renderDistanceX = countChunksX * 16;

        for (int chunkX = 0; chunkX < countChunksX; ++chunkX) {
            final int adjustedX = getBaseCoordinate(baseX, renderDistanceX, chunkX);

            for (int chunkZ = 0; chunkZ < countChunksZ; ++chunkZ) {
                final int adjustedZ = getBaseCoordinate(baseZ, renderDistanceX, chunkZ);

                for (int chunkY = 0; chunkY < countChunksY; ++chunkY) {
                    final int adjustedY = chunkY << 4;

                    renderChunks[(chunkZ * countChunksY + chunkY) * countChunksX + chunkX].setPosition(adjustedX, adjustedY, adjustedZ);
                }
            }
        }
    }

    /**
     * @reason Improving the performance of this method by using bitwise operators since render chunk size is always the same.
     *         Improving the performance of this method is beneficial as it reduces lag when loading renderer.
     *         For example, when loading in a world, changing the render distance, changing graphics quality, etc.
     *         Not only that but it is also used when updating the frustum which is done each time the viewEntity changes position.
     * @reason Using @Overwrite to have no overhead, I assume that nearly no mods will Mixin it anyway, this could easily be an injection if incompatibilities are found.
     * @author Desoroxxx
     */
    @Overwrite
    private int getBaseCoordinate(int base, int renderDistance, int chunkIndex) {
        int coordinate = chunkIndex << 4;
        int offset = coordinate - base + renderDistance >> 1;

        if (offset < 0)
            offset += renderDistance;

        if (offset >= renderDistance)
            return coordinate - renderDistance;

        return coordinate;
    }

    /**
     * @reason Improving the performance of this method by using bitwise operators since render chunk size is always the same.
     *         Improving the performance of this method is beneficial for FPS stability, as it reduces FPS drops when a lot of blocks are marked for update.
     *         For example, when loading in a world, teleporting, moving really fast or when using mods that changes a lot of blocks all at once.
     * @reason Using @Overwrite to have no overhead, I assume that nearly no mods will Mixin it anyway, this could easily be an injection if incompatibilities are found.
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
            final int normalizedX = (x % countChunksX + countChunksX) % countChunksX;

            for (int y = chunkMinY; y <= chunkMaxY; ++y) {
                final int normalizedY = (y % countChunksY + countChunksY) % countChunksY;

                for (int z = chunkMinZ; z <= chunkMaxZ; ++z) {
                    final int normalizedZ = (z % countChunksZ + countChunksZ) % countChunksZ;

                    renderChunks[(normalizedZ * countChunksY + normalizedY) * countChunksX + normalizedX].setNeedsUpdate(updateImmediately);
                }
            }
        }
    }

    /**
     * @reason Improving the performance of this method by using bitwise operators since render chunk size is always the same.
     *         Making this faster improves the speed of loading render chunks and thus makes it less likely to have so far away chunks never load.
     * @reason Using @Overwrite to have no overhead, I assume that nearly no mods will Mixin it anyway, this could easily be an injection if incompatibilities are found.
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
