package io.redstudioragnarok.valkyrie.mixin.appleskin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import squeek.appleskin.client.HUDOverlayHandler;

@Mixin(value = HUDOverlayHandler.class, remap = false)
public class HUDOverlayHandlerMixin {

    @ModifyArg(method = "onPreRender", at = @At(value = "INVOKE", target = "Lsqueek/appleskin/client/HUDOverlayHandler;drawExhaustionOverlay(FLnet/minecraft/client/Minecraft;IIF)V", remap = false), index = 3, remap = false)
    private int raiseExhaustionOverlay(final int original) {
        return original - 3;
    }

    @ModifyArg(method = "onRender", at = @At(value = "INVOKE", target = "Lsqueek/appleskin/client/HUDOverlayHandler;drawSaturationOverlay(FFLnet/minecraft/client/Minecraft;IIF)V", remap = false), index = 4, remap = false)
    private int raiseSaturationOverlay(final int original) {
        return original - 3;
    }

    @ModifyArg(method = "onRender", at = @At(value = "INVOKE", target = "Lsqueek/appleskin/client/HUDOverlayHandler;drawHungerOverlay(IILnet/minecraft/client/Minecraft;IIF)V", remap = false), index = 4, remap = false)
    private int raiseHungerOverlay(final int original) {
        return original - 3;
    }
}
