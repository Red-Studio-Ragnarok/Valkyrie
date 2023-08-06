package io.redstudioragnarok.valkyrie.mixin.mc67532fix;

import io.redstudioragnarok.valkyrie.config.ValkyrieConfig;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static io.redstudioragnarok.valkyrie.Valkyrie.MC;

@Mixin(value = GuiIngame.class, priority = -100000000)
public final class GuiIngameMixin extends Gui {

    @Shadow @Final private static ResourceLocation WIDGETS_TEX_PATH;

    @Shadow private void renderHotbarItem(int x, int y, float partialTicks, EntityPlayer player, ItemStack stack) { throw new AssertionError(); }

    /**
     * Render the hotbar for the player
     *
     * @reason Fix MC-67532
     *
     * @author Desoroxxx
     */
    @Overwrite
    protected void renderHotbar(ScaledResolution scaledResolution, float partialTicks) {
        if (!(MC.getRenderViewEntity() instanceof EntityPlayer))
            return;

        GlStateManager.color(1, 1, 1, 1);
        MC.getTextureManager().bindTexture(WIDGETS_TEX_PATH);

        EntityPlayer player = (EntityPlayer) MC.getRenderViewEntity();
        ItemStack offhandItem = player.getHeldItemOffhand();
        EnumHandSide offhandSide = player.getPrimaryHand().opposite();
        int screenWidth = scaledResolution.getScaledWidth();
        int halfWidth = screenWidth / 2;
        float originalZLevel = zLevel;
        zLevel = -90;

        // Draw the hotbar
        drawTexturedModalRect(halfWidth - 91, scaledResolution.getScaledHeight() - 22 - ValkyrieConfig.mc67532Fix.offset, 0, 0, 182, 22);
        // Draw the selected slot
        drawTexturedModalRect(halfWidth - 91 - 1 + player.inventory.currentItem * 20, scaledResolution.getScaledHeight() - 22 - 1 - ValkyrieConfig.mc67532Fix.offset, 0, 22, 24, 24);

        // If there is an item in the offhand, draw the offhand slot
        if (!offhandItem.isEmpty()) {
            int xPos = offhandSide == EnumHandSide.LEFT ? halfWidth - 91 - 29 : halfWidth + 91;
            drawTexturedModalRect(xPos, scaledResolution.getScaledHeight() - 23 - ValkyrieConfig.mc67532Fix.offset, offhandSide == EnumHandSide.LEFT ? 24 : 53, 22, 29, 24);
        }

        zLevel = originalZLevel;

        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderHelper.enableGUIStandardItemLighting();

        // Render each item in the hotbar
        for (int slot = 0; slot < 9; slot++) {
            int x = halfWidth - 90 + slot * 20 + 2;
            int y = scaledResolution.getScaledHeight() - 16 - 3 - ValkyrieConfig.mc67532Fix.offset;
            renderHotbarItem(x, y, partialTicks, player, player.inventory.mainInventory.get(slot));
        }

        // If there is an item in the offhand, render it
        if (!offhandItem.isEmpty()) {
            int y = scaledResolution.getScaledHeight() - 16 - 3 - ValkyrieConfig.mc67532Fix.offset;
            int x = offhandSide == EnumHandSide.LEFT ? halfWidth - 91 - 26 : halfWidth + 91 + 10;
            renderHotbarItem(x, y, partialTicks, player, offhandItem);
        }

        // If the attack indicator is set to 2, render it
        if (MC.gameSettings.attackIndicator == 2) {
            float attackStrength = MC.player.getCooledAttackStrength(0.0F);
            if (attackStrength < 1) {
                int yPos = scaledResolution.getScaledHeight() - 20 - ValkyrieConfig.mc67532Fix.offset;
                int xPos = offhandSide == EnumHandSide.RIGHT ? halfWidth - 91 - 22 : halfWidth + 91 + 6;
                MC.getTextureManager().bindTexture(Gui.ICONS);
                int indicatorHeight = (int) (attackStrength * 19.0F);
                GlStateManager.color(1, 1, 1, 1);
                drawTexturedModalRect(xPos, yPos, 0, 94, 18, 18);
                drawTexturedModalRect(xPos, yPos + 18 - indicatorHeight, 18, 112 - indicatorHeight, 18, indicatorHeight);
            }
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
    }
}
