package dev.redstudio.valkyrie;

import dev.redstudio.redcore.ticking.RedClientTicker;
import dev.redstudio.valkyrie.config.ValkyrieConfig;
import dev.redstudio.valkyrie.handlers.ClientEventHandler;
import dev.redstudio.valkyrie.handlers.DebugHandler;
import dev.redstudio.valkyrie.handlers.KeyInputHandler;
import dev.redstudio.valkyrie.keys.KeyBindings;
import dev.redstudio.valkyrie.renderer.CloudRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

import static dev.redstudio.valkyrie.ProjectConstants.*;

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
@Mod(clientSideOnly = true, modid = ID, name = NAME, version = VERSION, dependencies = "required-after:mixinbooter@[" + MIXIN_BOOTER_VERSION + ",);required-after:configanytime@[" + CONFIG_ANYTIME_VERSION + ",);;required-after:redcore@[" + RED_CORE_VERSION + ",);", updateJSON = "https://forge.curseupdate.com/874067/" + ID)
public final class Valkyrie {

	public static boolean warningShown = false;

	public static File snoozerFile;

	public static final Minecraft MC = Minecraft.getMinecraft();

	private static CloudRenderer cloudRenderer;

	@Mod.EventHandler
	public static void preInit(FMLPreInitializationEvent preInitializationEvent) {
		snoozerFile = new File(preInitializationEvent.getModConfigurationDirectory() + "/" + VERSION + " Snoozer.txt");
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
		RedClientTicker.startClientTicker();
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
