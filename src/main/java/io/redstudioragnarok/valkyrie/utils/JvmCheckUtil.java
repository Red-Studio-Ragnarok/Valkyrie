package io.redstudioragnarok.valkyrie.utils;

import net.minecraft.client.resources.I18n;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Reimplementation of <a href="https://github.com/Darkhax-Minecraft/OldJavaWarning">Old Java Warning</a>
 *
 */
public class JvmCheckUtil {

    public static void checkJavaVersion () {
        final String[] propertyStrings = new String[] { "sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch" };

        for (final String property : propertyStrings) {
            final String value = System.getProperty(property);

            if (value != null && !value.contains("64"))
                displayWarning(I18n.format("jvmCheck.32bit.description"), I18n.format("jvmCheck.title"), I18n.format("jvmCheck.adoptiumLink"));
        }

        String minVersion = "1.8.0_372";

        String[] VersionConfig = minVersion.split("_");
        String[] VersionSystem = System.getProperty("java.version").split("_");

        if(VersionConfig.length> 1 && VersionSystem.length>1)
            if(VersionConfig[1].matches("[0-9]+") && VersionSystem[1].matches("[0-9]+"))
                if (Integer.parseInt(minVersion.split("_")[1])>Integer.parseInt(System.getProperty("java.version").split("_")[1]))
                    displayWarning(I18n.format("jvmCheck.outdated.description"), I18n.format("jvmCheck.title"), I18n.format("jvmCheck.adoptiumLink"));
    }

    private static void displayWarning(String message, String title, String url) {
        final String[] options = { I18n.format("jvmCheck.download"), I18n.format("jvmCheck.ignore") };
        final int response = JOptionPane.showOptionDialog(getPopupFrame(), message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);

        // Download
        if (response == 0) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException exception) {
                JOptionPane.showMessageDialog(getPopupFrame(), "Could not access adoptium website. Do you have Internet?", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static JFrame getPopupFrame () {
        final JFrame parent = new JFrame();
        parent.setAlwaysOnTop(true);
        return parent;
    }
}
