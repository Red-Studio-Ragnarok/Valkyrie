package io.redstudioragnarok.valkyrie.config;

import com.cleanroommc.configanytime.ConfigAnytime;
import io.redstudioragnarok.valkyrie.Valkyrie;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;

import static dev.redstudio.valkyrie.ProjectConstants.NAME;
import static io.redstudioragnarok.valkyrie.Valkyrie.MC;
import static dev.redstudio.valkyrie.ProjectConstants.ID;

@Config(modid = ID, name = NAME)
public class ValkyrieConfig {

    public static final GeneralConfig general = new GeneralConfig();

    public static final ZoomConfig zoom = new ZoomConfig();

    public static final GraphicsConfig graphics = new GraphicsConfig();

    public static final MC67532Fix mc67532Fix = new MC67532Fix();

    public static final DebugConfig debug = new DebugConfig();

    public static class GeneralConfig {

        @Config.RequiresMcRestart
        public boolean highPrecisionDepthBuffer = false;
        public boolean customIcons = false;

        public String windowTitle = "Minecraft";
    }

    public static class ZoomConfig {

        public boolean smoothZoom = true;
        public boolean smoothCamera = true;

        @Config.RangeDouble(min = 1, max = 10)
        public double zoomMultiplier = 3;
        @Config.RangeDouble(min = 1, max = 10)
        public double smoothZoomSpeed = 5;
    }

    public static class GraphicsConfig {

        public final CloudsConfig clouds = new CloudsConfig();

        public final FogConfig fog = new FogConfig();

        public final LeavesConfig leaves = new LeavesConfig();

        public static class CloudsConfig {

            public boolean enabled = true;

            public int height = 256;
            @Config.RangeInt(min = 4)
            public int renderDistance = 32;
            public int layers = 1;

            public float saturation = 0.5F;
        }

        public static class FogConfig {

            public boolean enabled = true;
            public boolean distanceFog = true;
            public boolean waterFog = true;
            public boolean lavaFog = true;
        }

        public static class LeavesConfig {

            public boolean fancyLeaves = true;
            public boolean leavesCulling = true;

            @Config.RangeInt(min = 1, max = 127)
            public byte leavesCullingDepth = 4;
        }
    }



    public static class MC67532Fix {

        @Config.RequiresMcRestart
        public boolean enabled = true;

        @Config.RangeInt(min = 3, max = 127)
        public byte offset = 3;
    }

    public static class DebugConfig {

        public boolean enabled = true;
        public boolean wireframeClouds = false;
        public boolean wireframeTerrain = false;
    }

    @Mod.EventBusSubscriber(modid = ID)
    private static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent onConfigChangedEvent) {
            if (onConfigChangedEvent.getModID().equals(ID)) {
                ConfigManager.sync(ID, Config.Type.INSTANCE);

                Display.setTitle(ValkyrieConfig.general.windowTitle);
                MC.setWindowIcon();

                // The values do not matter as we inject code that fetches it from the config file
                Blocks.LEAVES.setGraphicsLevel(true);
                Blocks.LEAVES2.setGraphicsLevel(true);

                MC.renderGlobal.loadRenderers();

                Valkyrie.updateDebugHandler();
            }
        }
    }

    static {
        ConfigAnytime.register(ValkyrieConfig.class);
    }
}
