package io.redstudioragnarok.valkyrie.mixin;

import net.minecraft.client.gui.GuiMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(GuiMainMenu.class)
public class GuiMainMenuMixin {

    private static final List<String> valkyrieSplashes = new ArrayList<>(Arrays.asList(
            "Listen to the Valkyries' whispers",
            "Welcome to Valhalla",
            "Imagine fancier particles",
            "Taste the mead of victory",
            "Your journey to Asgard starts here",
            "Prepare for Ragnarok",
            "Odin's eye is upon you",
            "Craft your legend",
            "Embrace the Viking spirit",
            "Minecraft: The Valhalla Edition"
    ));

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private boolean onIsEmpty(List<String> list) {
        list.addAll(valkyrieSplashes);

        return list.isEmpty();
    }
}
