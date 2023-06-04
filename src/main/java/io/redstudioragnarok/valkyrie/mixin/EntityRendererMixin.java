package io.redstudioragnarok.valkyrie.mixin;

import io.redstudioragnarok.valkyrie.handlers.ZoomHandler;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;fovSetting:F", ordinal = 0), method = {"getFOVModifier(FZ)F"})
    private float getFov(final GameSettings settings) {
        return ZoomHandler.changeFovBasedOnZoom(settings.fovSetting);
    }
}
