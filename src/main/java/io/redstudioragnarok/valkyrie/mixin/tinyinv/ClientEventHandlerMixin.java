package io.redstudioragnarok.valkyrie.mixin.tinyinv;

import com.nuparu.tinyinv.events.ClientEventHandler;
import com.nuparu.tinyinv.utils.client.RenderUtils;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static io.redstudioragnarok.valkyrie.Valkyrie.MC;

@Mixin(value = ClientEventHandler.class, remap = false)
public class ClientEventHandlerMixin {

    /**
     * @reason Fix Tiny Inv breaking spectator hotbar
     * @author Desoroxxx
     */
    @Overwrite(remap = false)
    public static void onHotbarRender(net.minecraftforge.client.event.RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            event.setCanceled(true);

            if (MC.playerController.isSpectator())
                MC.ingameGUI.getSpectatorGui().renderTooltip(event.getResolution(), event.getPartialTicks());
            else
                RenderUtils.renderHotbar(event.getResolution(), event.getPartialTicks());
        }
    }
}
