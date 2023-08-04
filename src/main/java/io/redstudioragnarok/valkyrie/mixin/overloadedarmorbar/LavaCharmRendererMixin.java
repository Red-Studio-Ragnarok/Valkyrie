package io.redstudioragnarok.valkyrie.mixin.overloadedarmorbar;

import locusway.overpoweredarmorbar.overlay.LavaCharmRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LavaCharmRenderer.class)
public final class LavaCharmRendererMixin {

    @ModifyVariable(method = "renderLavaCharm", at = @At(value = "STORE"), name = "height", remap = false)
    private int raiseMantleHealthbar(final int original) {
        return original - 3;
    }
}
