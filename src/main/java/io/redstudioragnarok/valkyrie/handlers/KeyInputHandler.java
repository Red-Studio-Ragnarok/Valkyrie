package io.redstudioragnarok.valkyrie.handlers;

import io.redstudioragnarok.valkyrie.keys.KeyBindings;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import static io.redstudioragnarok.valkyrie.Valkyrie.mc;
import static io.redstudioragnarok.valkyrie.utils.ModReference.ID;

public final class KeyInputHandler {

    @SubscribeEvent
    public static void onKeyboardInput(InputEvent.KeyInputEvent keyInputEvent) {
        if (KeyBindings.openConfig.isKeyDown())
            mc.displayGuiScreen(FMLClientHandler.instance().getGuiFactoryFor(Loader.instance().getIndexedModList().get(ID)).createConfigGui(mc.currentScreen));
    }
}
