package io.redstudioragnarok.valkyrie;

import io.redstudioragnarok.redcore.RedCore;
import io.redstudioragnarok.valkyrie.config.ValkyrieConfig;
import io.redstudioragnarok.valkyrie.handlers.ClientEventHandler;
import io.redstudioragnarok.valkyrie.handlers.DebugHandler;
import io.redstudioragnarok.valkyrie.handlers.KeyInputHandler;
import io.redstudioragnarok.valkyrie.keys.KeyBindings;
import io.redstudioragnarok.valkyrie.renderer.CloudRenderer;
import io.redstudioragnarok.valkyrie.utils.JvmCheckUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

import static io.redstudioragnarok.valkyrie.utils.ModReference.ID;
import static io.redstudioragnarok.valkyrie.utils.ModReference.NAME;
import static io.redstudioragnarok.valkyrie.utils.ModReference.VERSION;

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
@Mod(clientSideOnly = true, modid = ID, name = NAME, version = VERSION, dependencies = "required-after:redcore@[0.4,);", updateJSON = "https://raw.githubusercontent.com/Red-Studio-Ragnarok/Valkyrie/main/update.json")
public final class Valkyrie {

    public static boolean warningShown = false;

    public static File snoozerFile;

    public static final Minecraft MC = Minecraft.getMinecraft();

    private static CloudRenderer cloudRenderer;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent preInitializationEvent) {
        snoozerFile = new File(preInitializationEvent.getModConfigurationDirectory() + "/" + VERSION + " Snoozer.txt");

        new Thread(JvmCheckUtil::checkJavaVersion).start();
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent initializationEvent) {
        MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);
        MinecraftForge.EVENT_BUS.register(KeyInputHandler.class);

        KeyBindings.init();

        updateDebugHandler();

        GameSettings.Options.RENDER_DISTANCE.setValueMax(63);
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent postInitializationEvent) {
        RedCore.startClientTicker();
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
