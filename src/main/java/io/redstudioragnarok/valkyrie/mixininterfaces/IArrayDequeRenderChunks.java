package io.redstudioragnarok.valkyrie.mixininterfaces;

import net.minecraft.client.renderer.chunk.RenderChunk;

import java.util.ArrayDeque;

public interface IArrayDequeRenderChunks {

    ArrayDeque<RenderChunk> valkyrie$renderChunks = new ArrayDeque<>();
}
