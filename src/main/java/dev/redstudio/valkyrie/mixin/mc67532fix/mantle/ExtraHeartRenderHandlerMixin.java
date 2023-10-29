package dev.redstudio.valkyrie.mixin.mc67532fix.mantle;

import dev.redstudio.valkyrie.config.ValkyrieConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import slimeknights.mantle.client.ExtraHeartRenderHandler;

@Mixin(value = ExtraHeartRenderHandler.class, remap = false)
public final class ExtraHeartRenderHandlerMixin {

    @ModifyVariable(method = "renderHealthbar", at = @At(value = "STORE"), name = "top", remap = false)
    private int raiseMantleHealthbar(final int original) {
        return original - ValkyrieConfig.mc67532Fix.offset;
    }
}
