package io.redstudioragnarok.valkyrie;

import io.redstudioragnarok.valkyrie.config.ValkyrieConfig;
import io.redstudioragnarok.valkyrie.handlers.ClientEventHandler;
import io.redstudioragnarok.valkyrie.handlers.DebugHandler;
import io.redstudioragnarok.valkyrie.keys.KeyBindings;
import io.redstudioragnarok.valkyrie.renderer.CloudRenderer;
import io.redstudioragnarok.valkyrie.utils.ModReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

//   /$$    /$$          /$$ /$$                           /$$
//  | $$   | $$         | $$| $$                          |__/
//  | $$   | $$ /$$$$$$ | $$| $$   /$$ /$$   /$$  /$$$$$$  /$$  /$$$$$$
//  |  $$ / $$/|____  $$| $$| $$  /$$/| $$  | $$ /$$__  $$| $$ /$$__  $$
//   \  $$ $$/  /$$$$$$$| $$| $$$$$$/ | $$  | $$| $$  \__/| $$| $$$$$$$$
//    \  $$$/  /$$__  $$| $$| $$_  $$ | $$  | $$| $$      | $$| $$_____/
//     \  $/  |  $$$$$$$| $$| $$ \  $$|  $$$$$$$| $$      | $$|  $$$$$$$
//      \_/    \_______/|__/|__/  \__/ \____  $$|__/      |__/ \_______/
//                                     /$$  | $$
//                                    |  $$$$$$/
//                                     \______/
@Mod(clientSideOnly = true, modid = ModReference.id, name = ModReference.name, version = ModReference.version)
public class Valkyrie {

    public static final Minecraft mc = Minecraft.getMinecraft();

    private static CloudRenderer cloudRenderer;

    @Mod.EventHandler
    public static void init(FMLInitializationEvent initializationEvent) {
        MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);

        KeyBindings.init();

        updateDebugHandler();

        GameSettings.Options.RENDER_DISTANCE.setValueMax(63);

        mc.gameSettings.useVbo = true;
        mc.gameSettings.anaglyph = false;

        ForgeModContainer.forgeCloudsEnabled = false;
    }

    public static void updateDebugHandler() {
        if (ValkyrieConfig.debug.enabled)
            MinecraftForge.EVENT_BUS.register(DebugHandler.class);
        else
            MinecraftForge.EVENT_BUS.unregister(DebugHandler.class);
    }

    public static CloudRenderer getCloudRenderer() {
        if (cloudRenderer == null)
            cloudRenderer = new CloudRenderer();

        return cloudRenderer;
    }
}
