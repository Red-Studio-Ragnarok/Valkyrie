package io.redstudioragnarok.valkyrie.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.categories.SpectatorDetails;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static io.redstudioragnarok.valkyrie.Valkyrie.mc;

@Mixin(GuiSpectator.class)
public class GuiSpectatorMixin extends Gui {

    @Shadow @Final private static ResourceLocation WIDGETS;

    @Shadow private void renderSlot(int p_175266_1_, int p_175266_2_, float p_175266_3_, float p_175266_4_, ISpectatorMenuObject p_175266_5_) { throw new AssertionError(); }

    /**
     * Render the hotbar for the spectator
     *
     * @reason Fix MC-67532
     *
     * @author Desoroxxx
     */
    @Overwrite
    protected void renderPage(ScaledResolution scaledResolution, float alpha, int x, float y, SpectatorDetails spectatorDetails) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(1, 1, 1, alpha);
        mc.getTextureManager().bindTexture(WIDGETS);
        drawTexturedModalRect((float) (x - 91), y - 3, 0, 0, 182, 22);

        if (spectatorDetails.getSelectedSlot() >= 0)
            drawTexturedModalRect((float) (x - 91 - 1 + spectatorDetails.getSelectedSlot() * 20), y - 4, 0, 22, 24, 24);

        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 0; i < 9; ++i)
            renderSlot(i, scaledResolution.getScaledWidth() / 2 - 90 + i * 20 + 2, y, alpha, spectatorDetails.getObject(i));

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
    }
}
