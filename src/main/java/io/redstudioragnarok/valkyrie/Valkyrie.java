package io.redstudioragnarok.valkyrie;

import io.redstudioragnarok.valkyrie.config.ValkyrieConfig;
import io.redstudioragnarok.valkyrie.handlers.ClientEventHandler;
import io.redstudioragnarok.valkyrie.handlers.DebugHandler;
import io.redstudioragnarok.valkyrie.keys.KeyBindings;
import io.redstudioragnarok.valkyrie.renderer.CloudRenderer;
import io.redstudioragnarok.valkyrie.utils.JvmCheckUtil;
import io.redstudioragnarok.valkyrie.utils.ModReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.io.FileUtils;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

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
    public static void preInit(FMLPreInitializationEvent preInitializationEvent) {
        new Thread(JvmCheckUtil::checkJavaVersion).start();
    }

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
