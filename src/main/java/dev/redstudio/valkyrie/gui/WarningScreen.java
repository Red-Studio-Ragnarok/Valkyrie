package dev.redstudio.valkyrie.gui;

import dev.redstudio.valkyrie.Valkyrie;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;

import java.io.IOException;
import java.util.List;

import static dev.redstudio.valkyrie.ProjectConstants.RED_LOGGER;

/// A gui to display a warning message.
///
/// Mostly copied from [Universal Tweaks Compat Screen](https://github.com/ACGaming/UniversalTweaks/blob/main/src/main/java/mod/acgaming/universaltweaks/util/compat/UTCompatScreen.java#L45)
public final class WarningScreen extends GuiScreen {

	public final List<String> messages;
	private int textHeight;

	public WarningScreen(final List<String> messages) {
		this.messages = messages;
	}

	@Override
	public void initGui() {
		buttonList.clear();

		textHeight = messages.size() * fontRenderer.FONT_HEIGHT;

		final GuiButton doneButton = new GuiButton(0, width / 2 + 25, Math.min(height / 2 + textHeight / 2 + fontRenderer.FONT_HEIGHT, height - 30), I18n.format("gui.done"));
		final GuiButton snoozeButton = new GuiButton(1, width / 2 - 175, Math.min(height / 2 + textHeight / 2 + fontRenderer.FONT_HEIGHT, height - 30), I18n.format("valkyrie.snooze"));

		doneButton.setWidth(150);
		snoozeButton.setWidth(150);

		buttonList.add(doneButton);
		buttonList.add(snoozeButton);
	}

	@Override
	public void actionPerformed(final GuiButton button) {
		if (button.id == 0)
			Valkyrie.MC.displayGuiScreen(new GuiMainMenu());
		else if (button.id == 1) {
			Valkyrie.MC.displayGuiScreen(new GuiMainMenu());

			try {
				if (!Valkyrie.snoozerFile.exists() && (!Valkyrie.snoozerFile.createNewFile()))
					RED_LOGGER.framedError("Warning Screen", "Could not create snoozer file", "Non critical exception, you will sill get warned next time you boot the game");
			} catch (final IOException ioException) {
				RED_LOGGER.framedError("Warning Screen", "Cannot init configs, an IOException occurred", "Non critical exception, you will sill get warned next time you boot the game", ioException.getMessage());
			} catch (final SecurityException securityException) {
				RED_LOGGER.framedError("Warning Screen", "Cannot init configs, a security manager blocked the operation", "Non critical exception, you will sill get warned next time you boot the game", securityException.getMessage());
			}
		}
	}

	@Override
	public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
		drawDefaultBackground();

		int posY = height / 2 - textHeight / 2;

		for (final String message : messages) {
			drawCenteredString(fontRenderer, message, width / 2, posY, 16777215);
			posY += fontRenderer.FONT_HEIGHT;
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
