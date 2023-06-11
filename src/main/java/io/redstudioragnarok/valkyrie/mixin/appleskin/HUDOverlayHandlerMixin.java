package io.redstudioragnarok.valkyrie.mixin.appleskin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import squeek.appleskin.client.HUDOverlayHandler;

@Mixin(value = HUDOverlayHandler.class, remap = false)
public class HUDOverlayHandlerMixin {

    @Redirect(method = "onPreRender", at = @At(value = "INVOKE", target = "Lsqueek/appleskin/client/HUDOverlayHandler;drawExhaustionOverlay(FLnet/minecraft/client/Minecraft;IIF)V", remap = false), remap = false)
    private void raiseExhaustionOverlay(final float exhaustion, final Minecraft mc, final int left, final int top, final float alpha) {
        HUDOverlayHandler.drawExhaustionOverlay(exhaustion, mc, left, top -3, alpha);
    }

    @Redirect(method = "onRender", at = @At(value = "INVOKE", target = "Lsqueek/appleskin/client/HUDOverlayHandler;drawSaturationOverlay(FFLnet/minecraft/client/Minecraft;IIF)V", remap = false), remap = false)
    private void raiseSaturationOverlay(final float saturationGained, final float saturationLevel, final Minecraft mc, final int left, final int top, final float alpha) {
        HUDOverlayHandler.drawSaturationOverlay(saturationGained, saturationLevel, mc, left, top -3, alpha);
    }

    @Redirect(method = "onRender", at = @At(value = "INVOKE", target = "Lsqueek/appleskin/client/HUDOverlayHandler;drawHungerOverlay(IILnet/minecraft/client/Minecraft;IIF)V", remap = false), remap = false)
    private void raiseHungerOverlay(final int hungerRestored, final int foodLevel, final Minecraft mc, final int left, final int top, final float alpha) {
        HUDOverlayHandler.drawHungerOverlay(hungerRestored, foodLevel, mc, left, top -3, alpha);
    }
}
