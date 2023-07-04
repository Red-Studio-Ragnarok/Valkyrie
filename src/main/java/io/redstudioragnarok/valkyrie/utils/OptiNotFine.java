package io.redstudioragnarok.valkyrie.utils;

import net.minecraftforge.fml.client.FMLClientHandler;

/**
 * This class provides methods to communicate with OptiNotFine (Can you see that I hate OptiFine?)
 * <p>
 * Todo: Use {@link FMLClientHandler#hasOptifine}
 * Todo: Strip from the server
 *
 * @author Desoroxxx
 */
public class OptiNotFine {

    private static boolean isOptiFineInstalled = true;

    private static Class installerClass = null;

    /**
     * Checks if OptiNotFine is installed.
     * <p>
     * This method works by attempting to load the "Shaders" class from the OptiFine package.
     * <p>
     * If the method is unable to find the "Shaders" class, this indicates that OptiFine is not installed, and the method will return false.
     * <p>
     * However, if the method is able to successfully load the class, the method will return true.
     *
     * @return True if the shaders are enabled, false if they are disabled or if OptiNotFine is not installed.
     */
    public static boolean isOptiFineInstalled() {
        if (!isOptiFineInstalled)
            return false;

        try {
            if (installerClass == null)
                installerClass = Class.forName("optifine.Installer");

            return true;
        } catch (ClassNotFoundException ignored) {
            isOptiFineInstalled = false;
        }

        return false;
    }
}
