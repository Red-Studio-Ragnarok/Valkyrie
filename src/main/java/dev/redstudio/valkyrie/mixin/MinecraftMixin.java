package dev.redstudio.valkyrie.mixin;

import dev.redstudio.valkyrie.Valkyrie;
import dev.redstudio.valkyrie.config.ValkyrieConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.*;
import java.nio.ByteBuffer;

import static dev.redstudio.valkyrie.ProjectConstants.*;

@Mixin(Minecraft.class)
public final class MinecraftMixin {

	@Shadow private ByteBuffer readImageToBuffer(final InputStream imageStream) throws IOException {throw new AssertionError();}

	@Inject(method = "processKeyF3", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;printChatMessage(Lnet/minecraft/util/text/ITextComponent;)V", ordinal = 9, shift = At.Shift.AFTER))
	private void printCustomF3Shortcuts(final int auxKey, final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		final GuiNewChat guiNewChat = Valkyrie.MC.ingameGUI.getChatGUI();

		guiNewChat.printChatMessage(new TextComponentTranslation("debug.wireframeTerrain.help"));
		guiNewChat.printChatMessage(new TextComponentTranslation("debug.wireframeClouds.help"));
	}

	/// @reason Remove the version from the window title and add configurability.
	/// @author Luna Mira Lage (Desoroxxx)
	@ModifyArg(method = "createDisplay", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setTitle(Ljava/lang/String;)V"))
	private String modifyWindowTitle(final String title) {
		return ValkyrieConfig.general.windowTitle + (FMLLaunchHandler.isDeobfuscatedEnvironment() ? " Development Environment" : "");
	}

	/// @reason Use 32 bit depth buffer if enabled in config.
	/// @author Luna Mira Lage (Desoroxxx)
	@ModifyConstant(method = "createDisplay", constant = @Constant(intValue = 24))
	private int modifyDepthBits(final int bits) {
		return ValkyrieConfig.general.highPrecisionDepthBuffer ? 32 : 24;
	}

	/// @reason Replace Minecraft icon with the new icons and add configurability.
	/// @author Luna Mira Lage (Desoroxxx)
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

				if (icon16 == null || icon32 == null || icon48 == null || icon128 == null || icon256 == null) {
					LOGGER.error("One or more custom icons could not be found, icon_16 {} icon_32 {} icon_48 {} icon_128 {} icon_256 {}", icon16, icon32, icon48, icon128, icon256);
					return;
				}
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

				if (icon16 == null || icon32 == null || icon48 == null || icon128 == null || icon256 == null) {
					LOGGER.error("One or more default icons could not be found, icon_16 {} icon_32 {} icon_48 {} icon_128 {} icon_256 {}", icon16, icon32, icon48, icon128, icon256);
					return;
				}
			}

			Display.setIcon(new ByteBuffer[]{readImageToBuffer(icon16), readImageToBuffer(icon32), readImageToBuffer(icon48), readImageToBuffer(icon128), readImageToBuffer(icon256)});
		} catch (final IOException ioException) {
			RED_LOGGER.framedError("Minecraft Initialization", "Could not set window icons", "LWJGL default icons will not be replaced", ioException.getMessage());
		} catch (final NullPointerException nullPointerException) {
			RED_LOGGER.framedError("Minecraft Initialization", "Could not set window icons", "LWJGL default icons will not be replaced", nullPointerException.getMessage(), "This is probably due to custom icons being enabled when no custom icons are set or found");
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
			RED_LOGGER.framedError("Minecraft Initialization", "Could not find the specified custom icon", "", "Custom Icon Name: " + name, fileNotFoundException.getMessage());

			return null;
		}
	}
}
