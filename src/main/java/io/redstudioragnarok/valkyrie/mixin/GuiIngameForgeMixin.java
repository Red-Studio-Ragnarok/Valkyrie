package io.redstudioragnarok.valkyrie.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiIngameForge.class)
public abstract class GuiIngameForgeMixin extends GuiIngame {

    private GuiIngameForgeMixin(Minecraft mcIn) {super(mcIn);}

    @Shadow(remap = false) protected abstract void renderArmor(int width, int height);
    @Shadow(remap = false) protected abstract void renderHealthMount(int width, int height);
    @Shadow(remap = false) protected abstract void renderAir(int width, int height);
    @Shadow(remap = false) protected abstract void renderJumpBar(int width, int height);
    @Shadow(remap = false) protected abstract void renderExperience(int width, int height);

    @Redirect(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/GuiIngameForge;renderHealth(II)V", remap = false))
    private void raiseHealth(final GuiIngameForge guiIngameForge, final int width, final int height) {
        guiIngameForge.renderHealth(width, height - 3);
    }

    @Redirect(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/GuiIngameForge;renderArmor(II)V", remap = false))
    private void raiseArmor(final GuiIngameForge guiIngameForge, final int width, final int height) {
        renderArmor(width, height - 3);
    }

    @Redirect(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/GuiIngameForge;renderFood(II)V", remap = false))
    private void raiseFood(final GuiIngameForge guiIngameForge, final int width, final int height) {
        guiIngameForge.renderFood(width, height - 3);
    }

    @Redirect(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/GuiIngameForge;renderHealthMount(II)V", remap = false))
    private void raiseHealthMount(final GuiIngameForge guiIngameForge, final int width, final int height) {
        renderHealthMount(width, height - 3);
    }

    @Redirect(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/GuiIngameForge;renderAir(II)V", remap = false))
    private void raiseAir(final GuiIngameForge guiIngameForge, final int width, final int height) {
        renderAir(width, height - 3);
    }

    @Redirect(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/GuiIngameForge;renderJumpBar(II)V", remap = false))
    private void raiseJumpBar(final GuiIngameForge guiIngameForge, final int width, final int height) {
        renderJumpBar(width, height - 3);
    }

    @Redirect(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/GuiIngameForge;renderExperience(II)V", remap = false))
    private void raiseExperience(final GuiIngameForge guiIngameForge, final int width, final int height) {
        renderExperience(width, height - 3);
    }
}
