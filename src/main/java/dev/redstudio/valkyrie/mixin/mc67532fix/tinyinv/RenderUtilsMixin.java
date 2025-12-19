package dev.redstudio.valkyrie.mixin.mc67532fix.tinyinv;

import com.nuparu.tinyinv.utils.Utils;
import com.nuparu.tinyinv.utils.client.RenderUtils;
import dev.redstudio.valkyrie.config.ValkyrieConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.*;

import static dev.redstudio.valkyrie.Valkyrie.MC;

@Mixin(value = RenderUtils.class, remap = false)
public final class RenderUtilsMixin {

	@Shadow(remap = false) @Final public static ResourceLocation WIDGETS_TEX_PATH;

	@Shadow(remap = false) public static void drawTexturedModalRect(final int x, final int y, final int textureX, final int textureY, final int width, final int height, final double zLevel) {throw new AssertionError();}

	@Shadow(remap = false) public static void renderHotbarItem(final int p_184044_1_, final int p_184044_2_, final float p_184044_3_, final EntityPlayer player, final ItemStack stack, final Minecraft mc) {throw new AssertionError();}

	/// Render the hotbar for the player
	///
	/// @reason Fix MC-67532
	/// @author Luna Mira Lage (Desoroxxx)
	@Overwrite(remap = false)
	public static void renderHotbar(final ScaledResolution sr, final float partialTicks) {
		if (!(MC.getRenderViewEntity() instanceof EntityPlayer))
			return;


		final float zLevel = -90;
		GlStateManager.color(1, 1, 1, 1);
		MC.getTextureManager().bindTexture(WIDGETS_TEX_PATH);
		final EntityPlayer entityplayer = (EntityPlayer) MC.getRenderViewEntity();
		final ItemStack itemstack = entityplayer.getHeldItemOffhand();
		final EnumHandSide enumhandside = entityplayer.getPrimaryHand().opposite();
		final int i = sr.getScaledWidth() / 2;
		final int slots = Utils.getHotbarSlots();
		final int width = 20 * slots + 2;
		int x = i - width / 2;

		for (int l = 0; l < slots; ++l) {
			int w = l != 0 && l != slots - 1 ? 20 : 21;
			drawTexturedModalRect(x, sr.getScaledHeight() - 22 - ValkyrieConfig.mc67532Fix.offset, l == 0 ? 0 : (l == slots - 1 ? 161 : 21 + 20 * (l - 1)), 0, w, 22, zLevel);
			x += w;
		}

		drawTexturedModalRect(i - 91 - 1 + entityplayer.inventory.currentItem * 20 - (slots - 9) * 10, sr.getScaledHeight() - 22 - 1 - ValkyrieConfig.mc67532Fix.offset, 0, 22, 24, 22, zLevel);
		if (!itemstack.isEmpty()) {
			if (enumhandside == EnumHandSide.LEFT) {
				drawTexturedModalRect(i - 91 - 29, sr.getScaledHeight() - 23 - ValkyrieConfig.mc67532Fix.offset, 24, 22, 29, 24, zLevel);
			} else {
				drawTexturedModalRect(i + 91, sr.getScaledHeight() - 23 - ValkyrieConfig.mc67532Fix.offset, 53, 22, 29, 24, zLevel);
			}
		}

		GlStateManager.enableRescaleNormal();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderHelper.enableGUIStandardItemLighting();

		for (int l = 0; l < slots; ++l) {
			final int i1 = i - 90 + l * 20 + 2 - (slots - 9) * 10;
			final int j1 = sr.getScaledHeight() - 16 - 3 - ValkyrieConfig.mc67532Fix.offset;
			renderHotbarItem(i1, j1, partialTicks, entityplayer, entityplayer.inventory.mainInventory.get(l), MC);
		}

		if (!itemstack.isEmpty()) {
			final int l1 = sr.getScaledHeight() - 16 - 3 - ValkyrieConfig.mc67532Fix.offset;
			if (enumhandside == EnumHandSide.LEFT) {
				renderHotbarItem(i - 91 - 26, l1, partialTicks, entityplayer, itemstack, MC);
			} else {
				renderHotbarItem(i + 91 + 10, l1, partialTicks, entityplayer, itemstack, MC);
			}
		}

		if (MC.gameSettings.attackIndicator == 2) {
			final float f1 = MC.player.getCooledAttackStrength(0);
			if (f1 < 1) {
				int i2 = sr.getScaledHeight() - 20 - ValkyrieConfig.mc67532Fix.offset;
				int j2 = i + 91 + 6;
				if (enumhandside == EnumHandSide.RIGHT) {
					j2 = i - 91 - 22;
				}

				MC.getTextureManager().bindTexture(Gui.ICONS);
				int k1 = (int) (f1 * 19);
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
