package io.redstudioragnarok.valkyrie.handlers;

import io.redstudioragnarok.valkyrie.Valkyrie;
import io.redstudioragnarok.valkyrie.gui.WarningScreen;
import io.redstudioragnarok.valkyrie.utils.OptiNotFine;
import io.redstudioragnarok.valkyrie.utils.ValkyrieTickEvent;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

import static io.redstudioragnarok.valkyrie.Valkyrie.mc;

public class ClientEventHandler {

    private static int quarterTickCount;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onClientTickEvent(TickEvent.ClientTickEvent clientTickEvent) {
        if (clientTickEvent.phase == TickEvent.Phase.START) {
            Valkyrie.getCloudRenderer().updateCloudColour();

            quarterTickCount++;
            if (quarterTickCount == 5) {
                MinecraftForge.EVENT_BUS.post(new ValkyrieTickEvent.QuarterTickEvent());
                quarterTickCount = 0;
            }
        }
    }

    @SubscribeEvent
    public static void onGuiOpenEvent(GuiOpenEvent guiOpenEvent) {
        if (!(guiOpenEvent.getGui() instanceof GuiMainMenu))
            return;

        final List<String> messages = new ArrayList<>();

        if (OptiNotFine.isOptiFineInstalled()) {
            messages.add("OptiFine is installed.");
            messages.add("When OptiFine is installed, parts of Valkyrie will be disabled.");
            messages.add("This will greatly reduce the benefits of Valkyrie.");

            if (Loader.instance().getActiveModList().size() > 128)
                messages.add("In addition, OptiFine seems to reduce performance heavily in modded scenarios.");

            messages.add("");
            messages.add("So try using OptiFine only when you need a feature that Valkyrie doesn't have yet.");

            messages.add("");
            messages.add("");
        }

        if (!messages.isEmpty() && !Valkyrie.warningShown && !Valkyrie.snoozerFile.exists()) {
            guiOpenEvent.setGui(new WarningScreen(messages));
            Valkyrie.warningShown = true;
        }
    }

    @SubscribeEvent
    public static void onQuarterTickEvent(ValkyrieTickEvent.QuarterTickEvent quarterTickEvent) {
        Valkyrie.getCloudRenderer().updateSettings();

        mc.gameSettings.useVbo = true;
        mc.gameSettings.anaglyph = false;
        mc.gameSettings.clouds = 0;

        ForgeModContainer.forgeCloudsEnabled = false;
    }
}
