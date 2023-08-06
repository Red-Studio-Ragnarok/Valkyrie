package io.redstudioragnarok.valkyrie.handlers;

import io.redstudioragnarok.valkyrie.config.ValkyrieConfig;
import io.redstudioragnarok.valkyrie.keys.KeyBindings;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import static io.redstudioragnarok.valkyrie.Valkyrie.MC;
import static io.redstudioragnarok.valkyrie.utils.ModReference.ID;

public final class KeyInputHandler {

    @SubscribeEvent
    public static void onKeyboardInput(InputEvent.KeyInputEvent keyInputEvent) {
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
