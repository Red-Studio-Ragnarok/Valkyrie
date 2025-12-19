package dev.redstudio.valkyrie.mixin.optifine;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.redstudio.valkyrie.ProjectConstants.ID;

@Mixin(GuiMainMenu.class)
public class OptifineGuiMainMenuMixin extends GuiScreen {

	@Shadow
	@Final
	private float minceraftRoll;
	@Shadow
	private String splashText;
	@Shadow
	private float panoramaTimer;
	@Shadow
	private int widthCopyright;
	@Shadow
	private int widthCopyrightRest;
	@Shadow
	private int openGLWarning2Width;
	@Shadow
	private int openGLWarningX1;
	@Shadow
	private int openGLWarningY1;
	@Shadow
	private int openGLWarningX2;
	@Shadow
	private int openGLWarningY2;
	@Shadow
	private String openGLWarning1;
	@Shadow
	private String openGLWarning2;
	@Shadow(remap = false)
	private GuiScreen modUpdateNotification;

	@Shadow
	private void renderSkybox(int mouseX, int mouseY, float partialTicks) {throw new AssertionError();}

	private static final ResourceLocation TITLE_TEXTURE = new ResourceLocation(ID, "textures/gui/title/minecraft.png");

	private static final List<String> valkyrieSplashes = new ArrayList<>(Arrays.asList(
			"Listen to the Valkyries' whispers",
			"Welcome to Valhalla",
			"Imagine fancier particles",
			"Taste the mead of victory",
			"Your journey to Asgard starts here",
			"Prepare for Ragnarok",
			"Odin's eye is upon you",
			"Craft your legend",
			"Embrace the Viking spirit",
			"Minecraft: The Valhalla Edition"
	                                                                                  ));

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
	private boolean onIsEmpty(List<String> list) {
		list.addAll(valkyrieSplashes);

		return list.isEmpty();
	}

	/// @reason Update tittle screen with the new Minecraft logo, as well as remove the "Java Edition" logo
	/// @author Luna Mira Lage (Desoroxxx)
	@Inject(method = "drawScreen", at = @At(value = "HEAD"), cancellable = true)
	public void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo callbackInfo) {
		panoramaTimer += partialTicks;
		GlStateManager.disableAlpha();
		renderSkybox(mouseX, mouseY, partialTicks);
		GlStateManager.enableAlpha();
		drawGradientRect(0, 0, width, height, -2130706433, 16777215);
		drawGradientRect(0, 0, width, height, 0, Integer.MIN_VALUE);
		this.mc.getTextureManager().bindTexture(TITLE_TEXTURE);
		GlStateManager.color(1, 1, 1, 1);

		final int titleX = this.width / 2 - 137;

		if ((double) this.minceraftRoll < 1.0E-4) {
			this.drawTexturedModalRect(titleX + 0, 30, 0, 0, 99, 44);
			this.drawTexturedModalRect(titleX + 99, 30, 129, 0, 27, 44);
			this.drawTexturedModalRect(titleX + 99 + 26, 30, 126, 0, 3, 44);
			this.drawTexturedModalRect(titleX + 99 + 26 + 3, 30, 99, 0, 26, 44);
			this.drawTexturedModalRect(titleX + 155, 30, 0, 45, 155, 44);
		} else {
			this.drawTexturedModalRect(titleX + 0, 30, 0, 0, 155, 44);
			this.drawTexturedModalRect(titleX + 155, 30, 0, 45, 155, 44);
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

		List<String> brandings = Lists.reverse(FMLCommonHandler.instance().getBrandings(true));
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
