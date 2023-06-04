package io.redstudioragnarok.valkyrie.handlers;

import io.redstudioragnarok.valkyrie.Valkyrie;
import io.redstudioragnarok.valkyrie.config.ValkyrieConfig;
import io.redstudioragnarok.valkyrie.utils.ModReference;
import io.redstudioragnarok.valkyrie.utils.ValkyrieTickEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

import java.util.ArrayList;
import java.util.Objects;

import static io.redstudioragnarok.valkyrie.Valkyrie.mc;

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

    @SubscribeEvent
    public static void onDebugList(RenderGameOverlayEvent.Text event) {
        if (mc.gameSettings.showDebugInfo && !Objects.equals(ForgeVersion.getVersion(), "14.23.5.2860") && !FMLLaunchHandler.isDeobfuscatedEnvironment()) {
            ArrayList<String> debugScreenRight = event.getRight();

            debugScreenRight.add(String.format("%sForge is outdated please update to 14.23.5.2860", TextFormatting.DARK_RED));
        }
    }
}
