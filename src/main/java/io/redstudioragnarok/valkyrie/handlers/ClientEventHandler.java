package io.redstudioragnarok.valkyrie.handlers;

import io.redstudioragnarok.valkyrie.Valkyrie;
import io.redstudioragnarok.valkyrie.utils.ValkyrieTickEvent;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

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
    public static void onQuarterTickEvent(ValkyrieTickEvent.QuarterTickEvent quarterTickEvent) {
        Valkyrie.getCloudRenderer().updateSettings();

        mc.gameSettings.useVbo = true;
        mc.gameSettings.anaglyph = false;

        ForgeModContainer.forgeCloudsEnabled = false;
    }
}
