package dev.redstudio.valkyrie.mixin;

import dev.redstudio.valkyrie.Valkyrie;
import dev.redstudio.valkyrie.config.ValkyrieConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static dev.redstudio.valkyrie.ProjectConstants.*;

@Mixin(Minecraft.class)
public class MinecraftMixin {

	@Shadow
	private ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException {throw new AssertionError();}

	@Inject(method = "processKeyF3", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;printChatMessage(Lnet/minecraft/util/text/ITextComponent;)V", ordinal = 9, shift = At.Shift.AFTER))
	private void printCustomF3Shortcuts(final int auxKey, final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		final GuiNewChat guiNewChat = Valkyrie.MC.ingameGUI.getChatGUI();

		guiNewChat.printChatMessage(new TextComponentTranslation("debug.wireframeTerrain.help"));
		guiNewChat.printChatMessage(new TextComponentTranslation("debug.wireframeClouds.help"));
	}

	/**
	 * @reason Remove the version from the window title and add configurability.
	 * @author Desoroxxx
	 */
	@ModifyConstant(method = "createDisplay", constant = @Constant(stringValue = "Minecraft 1.12.2"))
	private String modifyWindowTitle(String title) {
		return ValkyrieConfig.general.windowTitle + (FMLLaunchHandler.isDeobfuscatedEnvironment() ? " Development Environment" : "");
	}

	/**
	 * @reason Use 32 bit depth buffer if enabled in config.
	 * @author Desoroxxx
	 */
	@ModifyConstant(method = "createDisplay", constant = @Constant(intValue = 24))
	private int modifyDepthBits(int bits) {
		return ValkyrieConfig.general.highPrecisionDepthBuffer ? 32 : 24;
	}

	/**
	 * @reason Replace Minecraft icon with the new icons and add configurability.
	 * @author Desoroxxx
	 */
	@Overwrite
	public void setWindowIcon() {
		InputStream icon16 = null;
		InputStream icon32 = null;
		InputStream icon48 = null;
		InputStream icon128 = null;
		InputStream icon256 = null;

		try {
			if (ValkyrieConfig.general.customIcons) {
				icon16 = valkyrie$getCustomIcon("icon_16");
				icon32 = valkyrie$getCustomIcon("icon_32");
				icon48 = valkyrie$getCustomIcon("icon_48");
				icon128 = valkyrie$getCustomIcon("icon_128");
				icon256 = valkyrie$getCustomIcon("icon_256");
			} else {
				if (VERSION.contains("Dev") || FMLLaunchHandler.isDeobfuscatedEnvironment()) {
					icon16 = valkyrie$getIcon(true, 16);
					icon32 = valkyrie$getIcon(true, 32);
					icon48 = valkyrie$getIcon(true, 48);
					icon128 = valkyrie$getIcon(true, 128);
					icon256 = valkyrie$getIcon(true, 256);
				} else {
					icon16 = valkyrie$getIcon(false, 16);
					icon32 = valkyrie$getIcon(false, 32);
					icon48 = valkyrie$getIcon(false, 48);
					icon128 = valkyrie$getIcon(false, 128);
					icon256 = valkyrie$getIcon(false, 256);
				}
			}

			Display.setIcon(new ByteBuffer[]{this.readImageToBuffer(icon16), this.readImageToBuffer(icon32), this.readImageToBuffer(icon48), this.readImageToBuffer(icon128), this.readImageToBuffer(icon256)});
		} catch (IOException ioException) {
			RED_LOGGER.printFramedError("Minecraft Initialization", "Could not set window icons", "LWJGL default icons will not be replaced", ioException.getMessage());
		} catch (NullPointerException nullPointerException) {
			RED_LOGGER.printFramedError("Minecraft Initialization", "Could not set window icons", "LWJGL default icons will not be replaced", nullPointerException.getMessage(), "This is probably due to custom icons being enabled when no custom icons are set or found");
		} finally {
			IOUtils.closeQuietly(icon16);
			IOUtils.closeQuietly(icon32);
			IOUtils.closeQuietly(icon48);
			IOUtils.closeQuietly(icon128);
			IOUtils.closeQuietly(icon256);
		}
	}

	@Unique
	private static InputStream valkyrie$getIcon(final boolean dev, final int size) {
		return Valkyrie.class.getResourceAsStream("/assets/" + ID + "/icons/" + (dev ? "dev/" : "") + "icon_" + size + ".png");
	}

	@Unique
	private static InputStream valkyrie$getCustomIcon(final String name) {
		try {
			return new FileInputStream(Valkyrie.MC.gameDir + "/resourcepacks/icons/" + name + ".png");
		} catch (FileNotFoundException fileNotFoundException) {
			RED_LOGGER.printFramedError("Minecraft Initialization", "Could not find the specified custom icon", "", "Custom Icon Name: " + name, fileNotFoundException.getMessage());

			return null;
		}
	}
}
