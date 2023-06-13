package io.redstudioragnarok.valkyrie.mixin.tinyinv;

import com.nuparu.tinyinv.utils.Utils;
import com.nuparu.tinyinv.utils.client.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
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

import static io.redstudioragnarok.valkyrie.Valkyrie.mc;

@Mixin(value = RenderUtils.class, remap = false)
public class RenderUtilsMixin {

    @Shadow(remap = false) @Final private static ResourceLocation WIDGETS_TEX_PATH;

    @Shadow(remap = false) private static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height, double zLevel) { throw new AssertionError(); }
    @Shadow(remap = false) private static void renderHotbarItem(int p_184044_1_, int p_184044_2_, float p_184044_3_, EntityPlayer player, ItemStack stack, Minecraft mc) { throw new AssertionError(); }

    /**
     * Render the hotbar for the player
     *
     * @reason Fix MC-67532
     *
     * @author Desoroxxx
     */
    @Overwrite(remap = false)
    public static void renderHotbar(ScaledResolution sr, float partialTicks) {
        if (!(mc.getRenderViewEntity() instanceof EntityPlayer))
            return;

        Minecraft mc = Minecraft.getMinecraft();
        float zLevel;
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(WIDGETS_TEX_PATH);
        EntityPlayer entityplayer = (EntityPlayer)mc.getRenderViewEntity();
        ItemStack itemstack = entityplayer.getHeldItemOffhand();
        EnumHandSide enumhandside = entityplayer.getPrimaryHand().opposite();
        int i = sr.getScaledWidth() / 2;
        zLevel = -90;
        int slots = Utils.getHotbarSlots();
        int width = 20 * slots + 2;
        int x = i - width / 2;

        for(int l = 0; l < slots; ++l) {
            int w = l != 0 && l != slots - 1 ? 20 : 21;
            drawTexturedModalRect(x, sr.getScaledHeight() - 22 - 3, l == 0 ? 0 : (l == slots - 1 ? 161 : 21 + 20 * (l - 1)), 0, w, 22, zLevel);
            x += w;
        }

        drawTexturedModalRect(i - 91 - 1 + entityplayer.inventory.currentItem * 20 - (slots - 9) * 10, sr.getScaledHeight() - 22 - 1 - 3, 0, 22, 24, 22, zLevel);
        if (!itemstack.isEmpty()) {
            if (enumhandside == EnumHandSide.LEFT) {
                drawTexturedModalRect(i - 91 - 29, sr.getScaledHeight() - 23 - 3, 24, 22, 29, 24, zLevel);
            } else {
                drawTexturedModalRect(i + 91, sr.getScaledHeight() - 23 - 3, 53, 22, 29, 24, zLevel);
            }
        }

        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderHelper.enableGUIStandardItemLighting();

        for(int l = 0; l < slots; ++l) {
            int i1 = i - 90 + l * 20 + 2 - (slots - 9) * 10;
            int j1 = sr.getScaledHeight() - 16 - 3 - 3;
            renderHotbarItem(i1, j1, partialTicks, entityplayer, entityplayer.inventory.mainInventory.get(l), mc);
        }

        if (!itemstack.isEmpty()) {
            int l1 = sr.getScaledHeight() - 16 - 3 - 3;
            if (enumhandside == EnumHandSide.LEFT) {
                renderHotbarItem(i - 91 - 26, l1, partialTicks, entityplayer, itemstack, mc);
            } else {
                renderHotbarItem(i + 91 + 10, l1, partialTicks, entityplayer, itemstack, mc);
            }
        }

        if (mc.gameSettings.attackIndicator == 2) {
            float f1 = mc.player.getCooledAttackStrength(0);
            if (f1 < 1) {
                int i2 = sr.getScaledHeight() - 20 - 3;
                int j2 = i + 91 + 6;
                if (enumhandside == EnumHandSide.RIGHT) {
                    j2 = i - 91 - 22;
                }

                mc.getTextureManager().bindTexture(Gui.ICONS);
                int k1 = (int)(f1 * 19);
                GlStateManager.color(1, 1, 1, 1);
                drawTexturedModalRect(j2, i2, 0, 94, 18, 18, zLevel);
                drawTexturedModalRect(j2, i2 + 18 - k1, 18, 112 - k1, 18, k1, zLevel);
            }
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
    }
}
