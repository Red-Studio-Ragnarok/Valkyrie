package io.redstudioragnarok.valkyrie.handlers;

import io.redstudioragnarok.valkyrie.Valkyrie;
import io.redstudioragnarok.valkyrie.utils.ValkyrieTickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientEventHandler {

    private static int quarterTickCount;

    @SubscribeEvent
    public static void onQuarterTickEvent(ValkyrieTickEvent.QuarterTickEvent quarterTickEvent) {
        Valkyrie.getCloudRenderer().updateSettings();
    }

    @SubscribeEvent
    public static void onClientTickEvent(TickEvent.ClientTickEvent clientTickEvent) {
        if (clientTickEvent.phase == TickEvent.Phase.END) {
            quarterTickCount++;
            if (quarterTickCount == 5) {
                MinecraftForge.EVENT_BUS.post(new ValkyrieTickEvent.QuarterTickEvent());
                quarterTickCount = 0;
            }
        }
    }
}
