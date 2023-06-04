package io.redstudioragnarok.valkyrie.config;

import io.redstudioragnarok.valkyrie.utils.ModReference;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = ModReference.id, name = ModReference.name)
public class ValkyrieConfig {

    public static final ZoomConfig zoom = new ZoomConfig();

    public static final CloudsConfig clouds = new CloudsConfig();

    public static class ZoomConfig {

        public boolean smoothZoom = true;
        public boolean smoothCamera = true;

        @Config.RangeDouble(min = 1, max = 10)
        public double zoomMultiplier = 3;
        @Config.RangeDouble(min = 1, max = 10)
        public double smoothZoomSpeed = 5;
    }

    public static class CloudsConfig {

        public int height = 256;
        public int renderDistance = 32;
    }

    @Mod.EventBusSubscriber(modid = ModReference.id)
    private static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent onConfigChangedEvent) {
            if (onConfigChangedEvent.getModID().equals(ModReference.id))
                ConfigManager.sync(ModReference.id, Config.Type.INSTANCE);
        }
    }
}
