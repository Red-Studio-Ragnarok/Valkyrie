package io.redstudioragnarok.valkyrie.utils;

import static dev.redstudio.valkyrie.ProjectConstants.RED_LOGGER;

public class ValkyrieUtils {

    public static boolean isVersionOutdated(final String currentVersion, final String latestVersion) {
        final String[] currentVersionParts = currentVersion.split("\\.");
        final String[] latestVersionParts = latestVersion.split("\\.");

        for (int i = 0; i < Math.min(currentVersionParts.length, latestVersionParts.length); i++) {
            final int comparison = compareVersionParts(currentVersionParts[i], latestVersionParts[i]);

            if (comparison != 0)
                return comparison < 0;
        }

        return false;
    }

    public static int compareVersionParts(final String currentPart, final String latestPart) {
        try {
            final int current = Integer.parseInt(currentPart);
            final int latest = Integer.parseInt(latestPart);

            return Integer.compare(current, latest);
        } catch (NumberFormatException numberFormatException) {
            RED_LOGGER.printFramedError("Version Checking", "Could not parse version string", "Non critical error, version checking will not be accurate", numberFormatException.getMessage());
            return 0;
        }
    }
}
