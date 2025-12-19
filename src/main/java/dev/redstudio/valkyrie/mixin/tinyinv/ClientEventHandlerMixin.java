package dev.redstudio.valkyrie.mixin.tinyinv;

import com.nuparu.tinyinv.events.ClientEventHandler;
import com.nuparu.tinyinv.utils.client.RenderUtils;
import dev.redstudio.valkyrie.Valkyrie;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = ClientEventHandler.class, remap = false)
public final class ClientEventHandlerMixin {

	/// @reason Fix Tiny Inv breaking spectator hotbar
	/// @author Luna Mira Lage (Desoroxxx)
	@Overwrite(remap = false)
	public static void onHotbarRender(final RenderGameOverlayEvent.Pre event) {
		if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR)
			return;

		event.setCanceled(true);

		if (Valkyrie.MC.playerController.isSpectator())
			Valkyrie.MC.ingameGUI.getSpectatorGui().renderTooltip(event.getResolution(), event.getPartialTicks());
		else
			RenderUtils.renderHotbar(event.getResolution(), event.getPartialTicks());
	}
}
