package dev.redstudio.valkyrie.handlers;

import dev.redstudio.valkyrie.config.ValkyrieConfig;
import dev.redstudio.valkyrie.keys.KeyBindings;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import static dev.redstudio.valkyrie.ProjectConstants.ID;
import static dev.redstudio.valkyrie.Valkyrie.MC;

public final class KeyInputHandler {

	@SubscribeEvent
	public static void onKeyboardInput(final InputEvent.KeyInputEvent keyInputEvent) {
		if (KeyBindings.openConfig.isKeyDown())
			MC.displayGuiScreen(FMLClientHandler.instance().getGuiFactoryFor(Loader.instance().getIndexedModList().get(ID)).createConfigGui(MC.currentScreen));

		if (Keyboard.isKeyDown(Keyboard.KEY_F3)) {
			if (Keyboard.isKeyDown(Keyboard.KEY_X))
				ValkyrieConfig.debug.wireframeTerrain = !ValkyrieConfig.debug.wireframeTerrain;

			if (Keyboard.isKeyDown(Keyboard.KEY_C))
				ValkyrieConfig.debug.wireframeClouds = !ValkyrieConfig.debug.wireframeClouds;
		}
	}
}
