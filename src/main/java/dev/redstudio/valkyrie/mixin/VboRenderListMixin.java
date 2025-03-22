package dev.redstudio.valkyrie.mixin;

import dev.redstudio.valkyrie.config.ValkyrieConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.VboRenderList;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.redstudio.valkyrie.Valkyrie.MC;

@Mixin(VboRenderList.class)
public final class VboRenderListMixin {

	@Inject(method = "renderChunkLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V"))
	private void startWireFrame(final CallbackInfo callbackInfo) {
		if (ValkyrieConfig.debug.wireframeTerrain && MC.isSingleplayer())
			GlStateManager.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
	}

	@Inject(method = "renderChunkLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;popMatrix()V", shift = At.Shift.BEFORE))
	private void stopWireFrame(final CallbackInfo callbackInfo) {
		if (ValkyrieConfig.debug.wireframeTerrain && MC.isSingleplayer())
			GlStateManager.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}
}
