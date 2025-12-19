package dev.redstudio.valkyrie.mixin;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import dev.redstudio.valkyrie.mixin.optifine.OptifineGuiMainMenuMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.gui.NotificationModUpdateScreen;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

import static dev.redstudio.valkyrie.ProjectConstants.ID;

/// When updating this method, remember to update [OptifineGuiMainMenuMixin]
@Mixin(GuiMainMenu.class)
public final class GuiMainMenuMixin extends GuiScreen {

	@Shadow @Final private float minceraftRoll;
	@Shadow private String splashText;
	@Shadow private float panoramaTimer;
	@Shadow private int widthCopyright;
	@Shadow private int widthCopyrightRest;
	@Shadow private int openGLWarning2Width;
	@Shadow private int openGLWarningX1;
	@Shadow private int openGLWarningY1;
	@Shadow private int openGLWarningX2;
	@Shadow private int openGLWarningY2;
	@Shadow private String openGLWarning1;
	@Shadow private String openGLWarning2;
	@Shadow(remap = false) private NotificationModUpdateScreen modUpdateNotification;

	@Shadow private void renderSkybox(final int mouseX, final int mouseY, final float partialTicks) {throw new AssertionError();}

	@Unique private static final ResourceLocation VALKYRIE$TITLE_TEXTURE = new ResourceLocation(ID, "textures/gui/title/minecraft.png");

	@Unique private static final List<String> valkyrie$valkyrieSplashes = new ArrayList<>(Arrays.asList(
			"Listen to the Valkyries' whispers",
			"Welcome to Valhalla",
			"Imagine fancier particles",
			"Taste the mead of victory",
			"Your journey to Asgard starts here",
			"Prepare for Ragnarok",
			"Odin's eye is upon you",
			"Craft your legend",
			"Embrace the Viking spirit",
			"Minecraft: The Valhalla Edition"));

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
	private boolean onIsEmpty(final List<String> list) {
		list.addAll(valkyrie$valkyrieSplashes);

		return list.isEmpty();
	}

	/// @reason Update tittle screen with the new Minecraft logo, as well as remove the "Java Edition" logo
	/// @author Luna Mira Lage (Desoroxxx)
	@Inject(method = "drawScreen", at = @At(value = "HEAD"), cancellable = true, require = 0)
	public void drawScreen(final int mouseX, final int mouseY, final float partialTicks, final CallbackInfo callbackInfo) {
		panoramaTimer += partialTicks;
		GlStateManager.disableAlpha();
		renderSkybox(mouseX, mouseY, partialTicks);
		GlStateManager.enableAlpha();
		drawGradientRect(0, 0, width, height, -2130706433, 16777215);
		drawGradientRect(0, 0, width, height, 0, Integer.MIN_VALUE);
		mc.getTextureManager().bindTexture(VALKYRIE$TITLE_TEXTURE);
		GlStateManager.color(1, 1, 1, 1);

		final int titleX = width / 2 - 137;

		if ((double) minceraftRoll < 1.0E-4) {
			drawTexturedModalRect(titleX, 30, 0, 0, 99, 44);
			drawTexturedModalRect(titleX + 99, 30, 129, 0, 27, 44);
			drawTexturedModalRect(titleX + 99 + 26, 30, 126, 0, 3, 44);
			drawTexturedModalRect(titleX + 99 + 26 + 3, 30, 99, 0, 26, 44);
			drawTexturedModalRect(titleX + 155, 30, 0, 45, 155, 44);
		} else {
			drawTexturedModalRect(titleX, 30, 0, 0, 155, 44);
			drawTexturedModalRect(titleX + 155, 30, 0, 45, 155, 44);
		}

		splashText = ForgeHooksClient.renderMainMenu((GuiMainMenu) mc.currentScreen, fontRenderer, width, height, splashText);

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) (width / 2 + 90), 70, 0);
		GlStateManager.rotate(-20, 0, 0, 1);
		float f = 1.8F - MathHelper.abs(MathHelper.sin((float) (Minecraft.getSystemTime() % 1000L) / 1000 * ((float) Math.PI * 2)) * 0.1F);
		f = f * 100 / (float) (fontRenderer.getStringWidth(splashText) + 32);
		GlStateManager.scale(f, f, f);
		drawCenteredString(fontRenderer, splashText, 0, -8, -256);
		GlStateManager.popMatrix();

		final List<String> brandings = Lists.reverse(FMLCommonHandler.instance().getBrandings(true));
		for (int brandingLine = 0; brandingLine < brandings.size(); brandingLine++) {
			String branding = brandings.get(brandingLine);
			if (!Strings.isNullOrEmpty(branding))
				drawString(fontRenderer, branding, 2, height - (10 + brandingLine * (fontRenderer.FONT_HEIGHT + 1)), 16777215);
		}

		drawString(fontRenderer, "Copyright Mojang AB. Do not distribute!", widthCopyrightRest, height - 10, -1);

		if (mouseX > widthCopyrightRest && mouseX < widthCopyrightRest + widthCopyright && mouseY > height - 10 && mouseY < height && Mouse.isInsideWindow())
			drawRect(widthCopyrightRest, height - 1, widthCopyrightRest + widthCopyright, height, -1);

		if (openGLWarning1 != null && !openGLWarning1.isEmpty()) {
			drawRect(openGLWarningX1 - 2, openGLWarningY1 - 2, openGLWarningX2 + 2, openGLWarningY2 - 1, 1428160512);
			drawString(fontRenderer, openGLWarning1, openGLWarningX1, openGLWarningY1, -1);
			drawString(fontRenderer, openGLWarning2, (width - openGLWarning2Width) / 2, (buttonList.get(0)).y - 12, -1);
		}

		super.drawScreen(mouseX, mouseY, partialTicks);

		modUpdateNotification.drawScreen(mouseX, mouseY, partialTicks);

		callbackInfo.cancel();
	}
}
