package dev.redstudio.valkyrie.mixin.mc67532fix;

import dev.redstudio.valkyrie.config.ValkyrieConfig;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiIngameForge.class)
public final class GuiIngameForgeMixin {

	@ModifyArg(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/GuiIngameForge;renderHealth(II)V", remap = false), index = 1)
	private int raiseHealth(final int original) {
		return original - ValkyrieConfig.mc67532Fix.offset;
	}

	@ModifyArg(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/GuiIngameForge;renderArmor(II)V", remap = false), index = 1)
	private int raiseArmor(final int original) {
		return original - ValkyrieConfig.mc67532Fix.offset;
	}

	@ModifyArg(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/GuiIngameForge;renderFood(II)V", remap = false), index = 1)
	private int raiseFood(final int original) {
		return original - ValkyrieConfig.mc67532Fix.offset;
	}

	@ModifyArg(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/GuiIngameForge;renderHealthMount(II)V", remap = false), index = 1)
	private int raiseHealthMount(final int original) {
		return original - ValkyrieConfig.mc67532Fix.offset;
	}

	@ModifyArg(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/GuiIngameForge;renderAir(II)V", remap = false), index = 1)
	private int raiseAir(final int original) {
		return original - ValkyrieConfig.mc67532Fix.offset;
	}

	@ModifyArg(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/GuiIngameForge;renderJumpBar(II)V", remap = false), index = 1)
	private int raiseJumpBar(final int original) {
		return original - ValkyrieConfig.mc67532Fix.offset;
	}

	@ModifyArg(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/GuiIngameForge;renderExperience(II)V", remap = false), index = 1)
	private int raiseExperience(final int original) {
		return original - ValkyrieConfig.mc67532Fix.offset;
	}
}
