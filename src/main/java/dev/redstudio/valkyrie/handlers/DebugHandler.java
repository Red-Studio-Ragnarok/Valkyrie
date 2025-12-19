package dev.redstudio.valkyrie.handlers;

import dev.redstudio.valkyrie.config.ValkyrieConfig;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

import static dev.redstudio.valkyrie.Valkyrie.MC;

public final class DebugHandler {

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onDebugList(final RenderGameOverlayEvent.Text event) {
		if (!MC.gameSettings.showDebugInfo)
			return;

		final ArrayList<String> debugScreenLeft = event.getLeft();

		debugScreenLeft.remove(4);
		debugScreenLeft.add(4, "T: " + MC.world.getDebugLoadedEntities());

		if (!debugScreenLeft.get(debugScreenLeft.size() - 1).equals(""))
			debugScreenLeft.add("");

		if (ValkyrieConfig.graphics.clouds.enabled)
			debugScreenLeft.add(String.format("%s<Valkyrie>%s Clouds are enabled, render distance: %s, height: %s, layers: %s,  wireframe is %s", TextFormatting.RED, TextFormatting.RESET, ValkyrieConfig.graphics.clouds.renderDistance, ValkyrieConfig.graphics.clouds.height, ValkyrieConfig.graphics.clouds.layers, ValkyrieConfig.debug.wireframeClouds ? "on" : "off"));
		else
			debugScreenLeft.add(String.format("%s<Valkyrie>%s Clouds are disabled", TextFormatting.RED, TextFormatting.RESET));
	}
}
