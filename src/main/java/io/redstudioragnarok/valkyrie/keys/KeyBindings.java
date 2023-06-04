package io.redstudioragnarok.valkyrie.keys;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;

import static io.redstudioragnarok.valkyrie.Valkyrie.mc;

/**
 * A class that holds and initialize all the keybindings for the mod.
 * <p>
 * The reasons it does not use ClientRegistry.registerKeyBinding is that this is around 5 to 7 time faster.
 * It does not matter, I just spend 30 minutes benchmarking and researching what is the fastest way to do this, so I will use it.
 *
 * @author Desoroxxx
 */
public class KeyBindings {

    public static final String categoryName = I18n.format("name");

    public static final KeyBinding zoom = new KeyBinding(I18n.format("keyBinding.zoom"), Keyboard.KEY_C, categoryName);

    public static void init() {
        final ArrayList<KeyBinding> keybindings = new ArrayList<>(Arrays.asList(zoom));
        final ArrayList<KeyBinding> allKeyBindings = new ArrayList<>(Arrays.asList(mc.gameSettings.keyBindings));

        allKeyBindings.addAll(keybindings);

        mc.gameSettings.keyBindings = allKeyBindings.toArray(new KeyBinding[0]);
    }
}
