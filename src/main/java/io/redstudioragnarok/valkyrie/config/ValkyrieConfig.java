package io.redstudioragnarok.valkyrie.config;

import io.redstudioragnarok.valkyrie.utils.ModReference;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = ModReference.ID, name = ModReference.NAME)
public class ValkyrieConfig {

    public static final GeneralConfig general = new GeneralConfig();

    public static final ZoomConfig zoom = new ZoomConfig();

    public static final CloudsConfig clouds = new CloudsConfig();

    public static final DebugConfig debug = new DebugConfig();

    public static class GeneralConfig {

        public boolean fogEnabled = true;
    }

    public static class ZoomConfig {

        public boolean smoothZoom = true;
        public boolean smoothCamera = true;

        @Config.RangeDouble(min = 1, max = 20)
        public double zoomMultiplier = 3;
        @Config.RangeDouble(min = 1, max = 10)
        public double smoothZoomSpeed = 5;
    }

    public static class CloudsConfig {

        public boolean enabled = true;

        public int height = 256;
        public int renderDistance = 128;
        public int layers = 1;
    }

    public static class DebugConfig {

        public boolean enabled = true;
        public boolean wireframeClouds = false;
    }

    @Mod.EventBusSubscriber(modid = ModReference.ID)
    private static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent onConfigChangedEvent) {
            if (onConfigChangedEvent.getModID().equals(ModReference.ID))
                ConfigManager.sync(ModReference.ID, Config.Type.INSTANCE);
        }
    }
}
