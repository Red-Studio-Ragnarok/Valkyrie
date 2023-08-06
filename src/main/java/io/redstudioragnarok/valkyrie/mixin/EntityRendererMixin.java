package io.redstudioragnarok.valkyrie.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.redstudioragnarok.valkyrie.Valkyrie;
import io.redstudioragnarok.valkyrie.config.ValkyrieConfig;
import io.redstudioragnarok.valkyrie.handlers.ZoomHandler;
import io.redstudioragnarok.valkyrie.renderer.FogRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.redstudioragnarok.valkyrie.Valkyrie.mc;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Shadow private float farPlaneDistance;

    @Shadow private float getFOVModifier(final float partialTicks, final boolean useFOVSetting) { throw new AssertionError(); }

    @Redirect(method = "getFOVModifier", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;fovSetting:F", ordinal = 0))
    private float getFov(final GameSettings settings) {
        return ZoomHandler.changeFovBasedOnZoom(settings.fovSetting);
    }

    @ModifyExpressionValue(method = "renderWorldPass", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;posY:D", ordinal = 1))
    private double forceUnderCheck(final double value) {
        return 0;
    }

    @ModifyExpressionValue(method = "renderWorldPass", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;posY:D", ordinal = 2))
    private double forceAboveCheck(final double value) {
        return 0;
    }

    @Inject(method = "setupFog", at = @At(value = "HEAD"), cancellable = true)
    private void setupFog(final int startCoords, final float partialTicks, final CallbackInfo callbackInfo) {
        FogRenderer.setupFog(startCoords, farPlaneDistance, partialTicks);
        callbackInfo.cancel();
    }

    @Inject(method = "renderCloudsCheck", at = @At(value = "HEAD"), cancellable = true)
    private void renderCloudsCheck(final RenderGlobal renderGlobalIn, final float partialTicks, final int pass, final double x, final double y, final double z, final CallbackInfo callbackInfo) {
        if (!ValkyrieConfig.graphics.clouds.enabled)
            return;

        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        Project.gluPerspective(getFOVModifier(partialTicks, true), (float) mc.displayWidth / (float) mc.displayHeight, 0.05F, (ValkyrieConfig.graphics.clouds.renderDistance * 16) * 4);
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        Valkyrie.getCloudRenderer().setupFog(partialTicks);
        Valkyrie.getCloudRenderer().render(mc.renderGlobal.cloudTickCounter, partialTicks);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        Project.gluPerspective(getFOVModifier(partialTicks, true), (float) mc.displayWidth / (float) mc.displayHeight, 0.05F, (mc.gameSettings.renderDistanceChunks * 16) * MathHelper.SQRT_2);
        GlStateManager.matrixMode(5888);

        callbackInfo.cancel();
    }
}
