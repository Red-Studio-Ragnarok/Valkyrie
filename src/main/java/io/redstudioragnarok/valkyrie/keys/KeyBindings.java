package io.redstudioragnarok.valkyrie.keys;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

import static dev.redstudio.valkyrie.ProjectConstants.NAME;

/**
 * A class that holds and initialize all the keybindings for the mod.
 *
 * @author Desoroxxx
 */
public class KeyBindings {

    public static final KeyBinding openConfig = new KeyBinding(I18n.format("keyBinding.openConfig"), Keyboard.KEY_GRAVE, NAME);
    public static final KeyBinding zoom = new KeyBinding(I18n.format("keyBinding.zoom"), Keyboard.KEY_C, NAME);

    public static void init() {
        ClientRegistry.registerKeyBinding(openConfig);

        ClientRegistry.registerKeyBinding(zoom);
    }
}
