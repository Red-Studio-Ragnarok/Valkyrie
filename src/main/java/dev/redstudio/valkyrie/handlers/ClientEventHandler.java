package dev.redstudio.valkyrie.handlers;

import dev.redstudio.redcore.ticking.RedClientTickEvent;
import dev.redstudio.redcore.utils.OptiNotFine;
import dev.redstudio.valkyrie.Valkyrie;
import dev.redstudio.valkyrie.gui.WarningScreen;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

import static dev.redstudio.valkyrie.Valkyrie.MC;

public final class ClientEventHandler {

	@SubscribeEvent
	public static void onGuiOpenEvent(GuiOpenEvent guiOpenEvent) {
		if (!(guiOpenEvent.getGui() instanceof GuiMainMenu))
			return;

		final List<String> messages = new ArrayList<>();

		if (OptiNotFine.isOptiFineInstalled()) {
			messages.add("OptiFine is installed.");
			messages.add("When OptiFine is installed, parts of Valkyrie will be disabled.");
			messages.add("This will greatly reduce the benefits of Valkyrie.");

			if (Loader.instance().getActiveModList().size() > 128)
				messages.add("In addition, OptiFine seems to reduce performance heavily in modded scenarios.");

			messages.add("");
			messages.add("So try using OptiFine only when you need a feature that Valkyrie doesn't have yet.");

			messages.add("");
			messages.add("");
		}

		if (Loader.isModLoaded("essential")) {
			messages.add("Essential is installed.");
			messages.add("When Essential is installed, parts of Valkyrie will be disabled.");
			messages.add("This will reduce the benefits of Valkyrie.");

			messages.add("");
			messages.add("");
		}

		if (!messages.isEmpty() && !Valkyrie.warningShown && !Valkyrie.snoozerFile.exists()) {
			guiOpenEvent.setGui(new WarningScreen(messages));
			Valkyrie.warningShown = true;
		}
	}

	@SubscribeEvent
	public static void onPlayer(PlayerEvent.PlayerLoggedInEvent playerLoggedInEvent) {
		if (playerLoggedInEvent.player != MC.player)
			return;

		Valkyrie.getCloudRenderer().updateCloudColour();
	}

	@SubscribeEvent
	public static void onClientTickEvent(TickEvent.ClientTickEvent clientTickEvent) {
		if (clientTickEvent.phase == TickEvent.Phase.START)
			return;

		Valkyrie.getCloudRenderer().updateCloudColour();
	}

	@SubscribeEvent
	public static void onPentaTickEvent(RedClientTickEvent.PentaTickEvent pentaTickEvent) {
		Valkyrie.getCloudRenderer().updateSettings();

		MC.gameSettings.useVbo = true;
		MC.gameSettings.anaglyph = false;
		MC.gameSettings.clouds = 0;

		ForgeModContainer.forgeCloudsEnabled = false;

//        if (mc.debugRenderer == null)
//            return;
//
//        mc.debugRenderer.chunkBorderEnabled = false;
//        mc.debugRenderer.pathfindingEnabled = false;
//        mc.debugRenderer.waterEnabled = false;
//        mc.debugRenderer.heightMapEnabled = false;
//        mc.debugRenderer.collisionBoxEnabled = false;
//        mc.debugRenderer.neighborsUpdateEnabled = true;
//        mc.debugRenderer.solidFaceEnabled = false;
	}
}
