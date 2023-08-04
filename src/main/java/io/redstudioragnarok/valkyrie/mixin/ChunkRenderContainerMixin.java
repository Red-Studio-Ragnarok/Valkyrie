package io.redstudioragnarok.valkyrie.mixin;

import net.minecraft.client.renderer.ChunkRenderContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChunkRenderContainer.class)
public final class ChunkRenderContainerMixin {

    @Shadow private double viewEntityX;
    @Shadow private double viewEntityY;
    @Shadow private double viewEntityZ;

    @Unique private static final BlockPos.MutableBlockPos valkyrie$mutableBlockPos = new BlockPos.MutableBlockPos();

    /**
     * @reason Make this faster by reusing a mutable blockpos instead of creating a new one every time
     * @reason Using @Overwrite to have no overhead, I assume that nearly no mods will Mixin it anyway, this could easily be an injection if incompatibilities are found.
     * @author Desoroxxx
     */
    @Overwrite
    public void preRenderChunk(final RenderChunk renderChunk) {
        valkyrie$mutableBlockPos.setPos(renderChunk.getPosition());

        GlStateManager.translate(valkyrie$mutableBlockPos.getX() - viewEntityX, valkyrie$mutableBlockPos.getY() - viewEntityY, valkyrie$mutableBlockPos.getZ() - viewEntityZ);
    }
}
