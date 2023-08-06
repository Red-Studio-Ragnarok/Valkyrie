package io.redstudioragnarok.valkyrie.gui;

import io.redstudioragnarok.valkyrie.Valkyrie;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.io.IOException;
import java.util.List;

import static io.redstudioragnarok.valkyrie.utils.ModReference.RED_LOG;

/**
 * A gui to display a warning message.
 * <p>
 * Mostly copied from <a href="https://github.com/ACGaming/UniversalTweaks/blob/main/src/main/java/mod/acgaming/universaltweaks/util/compat/UTCompatScreen.java#L45">Universal Tweaks Compat Screen</a>
 */
public class WarningScreen extends GuiScreen {

    public final List<String> messages;
    private int textHeight;

    public WarningScreen(List<String> messages) {
        this.messages = messages;
    }

    @Override
    public void initGui() {
        buttonList.clear();

        textHeight = messages.size() * fontRenderer.FONT_HEIGHT;

        GuiButton doneButton = new GuiButton(0, width / 2 + 25, Math.min(height / 2 + textHeight / 2 + fontRenderer.FONT_HEIGHT, height - 30), I18n.format("gui.done"));
        GuiButton snoozeButton = new GuiButton(1, width / 2 - 175, Math.min(height / 2 + textHeight / 2 + fontRenderer.FONT_HEIGHT, height - 30), I18n.format("valkyrie.snooze"));

        doneButton.setWidth(150);
        snoozeButton.setWidth(150);

        buttonList.add(doneButton);
        buttonList.add(snoozeButton);

    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 0)
            Valkyrie.MC.displayGuiScreen(new GuiMainMenu());
        else if (button.id == 1) {
            Valkyrie.MC.displayGuiScreen(new GuiMainMenu());

            try {
                if (!Valkyrie.snoozerFile.exists() && (!Valkyrie.snoozerFile.createNewFile())) {
                    RED_LOG.printFramedError("Warning Screen", "Could not create snoozer file", "Non critical exception, you will sill get warned next time you boot the game");
                }
            } catch (IOException ioException) {
                RED_LOG.printFramedError("Warning Screen", "Cannot init configs, an IOException occurred", "Non critical exception, you will sill get warned next time you boot the game", ioException.getMessage());
            } catch (SecurityException securityException) {
                RED_LOG.printFramedError("Warning Screen", "Cannot init configs, a security manager blocked the operation", "Non critical exception, you will sill get warned next time you boot the game", securityException.getMessage());
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int posY = height / 2 - textHeight / 2;

        for (String message : messages) {
            drawCenteredString(fontRenderer, message, width / 2, posY, 16777215);
            posY += fontRenderer.FONT_HEIGHT;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
