package dev.redstudio.valkyrie.mixin;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.nio.ByteBuffer;
import java.util.List;

@Mixin(WorldVertexBufferUploader.class)
public final class WorldVertexBufferUploaderMixin {

    /**
     * @reason Improves speed by tracking the index in the post-draw loop making it O(n) instead of O(n^2).
     * @reason Using @Overwrite to have no overhead, I assume that nearly no mods will Mixin it anyway, this could easily be an injection if incompatibilities are found.
     * @author Desoroxxx
     */
    @Overwrite
    public void draw(final BufferBuilder vertexBuffer) {
        if (vertexBuffer.getVertexCount() == 0)
            return;

        final ByteBuffer byteBuffer = vertexBuffer.getByteBuffer();

        final VertexFormat format = vertexBuffer.getVertexFormat();
        final int formatSize = format.getSize();
        final List<VertexFormatElement> elementList = format.getElements();

        int index = 0;
        for (final VertexFormatElement currentElement : elementList) {
            byteBuffer.position(format.getOffset(index));

            currentElement.getUsage().preDraw(format, index, formatSize, byteBuffer);

            ++index;
        }

        GlStateManager.glDrawArrays(vertexBuffer.getDrawMode(), 0, vertexBuffer.getVertexCount());

        index = 0;
        for (final VertexFormatElement currentElement : elementList) {
            currentElement.getUsage().postDraw(format, index, formatSize, byteBuffer);

            index++;
        }

        vertexBuffer.reset();
    }
}
