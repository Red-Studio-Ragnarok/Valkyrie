package io.redstudioragnarok.valkyrie.mixin;

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
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static io.redstudioragnarok.valkyrie.Valkyrie.mc;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Shadow private float farPlaneDistance;

    @Shadow private float getFOVModifier(float partialTicks, boolean useFOVSetting) { throw new AssertionError(); }

    @Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;fovSetting:F", ordinal = 0), method = {"getFOVModifier(FZ)F"})
    private float getFov(final GameSettings settings) {
        return ZoomHandler.changeFovBasedOnZoom(settings.fovSetting);
    }

    /**
     * @reason Improving performance
     * @author Desoroxxx
     */
    @Overwrite
    private void setupFog(int startCoords, float partialTicks) {
        FogRenderer.setupFog(startCoords, farPlaneDistance, partialTicks);
    }

    /**
     * @reason Improving performance
     * @author Desoroxxx
     */
    @Overwrite
    private void renderCloudsCheck(RenderGlobal renderGlobalIn, float partialTicks, int pass, double x, double y, double z) {
        if (!ValkyrieConfig.clouds.enabled)
            return;

        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        Project.gluPerspective(getFOVModifier(partialTicks, true), (float) mc.displayWidth / (float) mc.displayHeight, 0.05F, (ValkyrieConfig.clouds.renderDistance * 16) * 4);
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        Valkyrie.getCloudRenderer().setupFog(partialTicks);
        Valkyrie.getCloudRenderer().render(mc.renderGlobal.cloudTickCounter, partialTicks);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        Project.gluPerspective(getFOVModifier(partialTicks, true), (float) mc.displayWidth / (float) mc.displayHeight, 0.05F, (mc.gameSettings.renderDistanceChunks * 16) * MathHelper.SQRT_2);
        GlStateManager.matrixMode(5888);
    }
}
