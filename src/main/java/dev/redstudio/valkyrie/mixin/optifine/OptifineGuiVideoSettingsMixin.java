package dev.redstudio.valkyrie.mixin.optifine;

import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Removes VBO, 3D Anaglyph and Clouds settings from the Video Settings screen.
 */
@Mixin(GuiVideoSettings.class)
public class OptifineGuiVideoSettingsMixin {

	@Shadow(remap = false)
	private static GameSettings.Options[] videoOptions;

	@Inject(method = "initGui", at = @At(value = "HEAD"))
	private void removeVideoOptions(CallbackInfo callbackInfo) {
		int newLength = videoOptions.length;
		for (GameSettings.Options option : videoOptions) {
			if (option == GameSettings.Options.USE_VBO)
				newLength--;
		}

		GameSettings.Options[] newOptions = new GameSettings.Options[newLength];

		int j = 0;
		for (GameSettings.Options option : videoOptions) {
			if (option != GameSettings.Options.USE_VBO)
				newOptions[j++] = option;
		}

		videoOptions = newOptions;
	}
}
