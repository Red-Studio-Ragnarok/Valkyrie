package io.redstudioragnarok.valkyrie.mixin.mantle;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import slimeknights.mantle.client.ExtraHeartRenderHandler;

@Mixin(value = ExtraHeartRenderHandler.class, remap = false)
public class ExtraHeartRenderHandlerMixin {

    @ModifyVariable(method = "renderHealthbar", at = @At(value = "STORE"), name = "top", remap = false)
    private int raiseMantleHealthbar(int original) {
        return original - 3;
    }
}
