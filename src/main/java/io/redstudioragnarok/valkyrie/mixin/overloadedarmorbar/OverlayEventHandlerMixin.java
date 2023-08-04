package io.redstudioragnarok.valkyrie.mixin.overloadedarmorbar;

import locusway.overpoweredarmorbar.overlay.OverlayEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(OverlayEventHandler.class)
public final class OverlayEventHandlerMixin {

    @ModifyArg(method = "onRenderGameOverlayEventPre", at = @At(value = "INVOKE", target = "Llocusway/overpoweredarmorbar/overlay/OverlayEventHandler;renderArmorBar(II)V", remap = false), index = 1, remap = false)
    private int raiseExhaustionOverlay(final int original) {
        return original - 3;
    }
}
