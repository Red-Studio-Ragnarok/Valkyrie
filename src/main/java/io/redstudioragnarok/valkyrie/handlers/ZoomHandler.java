package io.redstudioragnarok.valkyrie.handlers;

import io.redstudioragnarok.valkyrie.config.ValkyrieConfig;

import static io.redstudioragnarok.valkyrie.Valkyrie.mc;
import static io.redstudioragnarok.valkyrie.keys.KeyBindings.zoom;

/**
 * This class is responsible for handling zoom level and related settings.
 * <p>
 * It manages the zoom level changes and adjustments to the field of view and mouse sensitivity based on the zoom level.
 * The class also considers whether smooth zoom and smooth camera configurations are enabled when making adjustments.
 */
public class ZoomHandler {

    private static long lastFrameTime = System.currentTimeMillis();
    private static float currentLevel = 1;
    private static float defaultMouseSensitivity;
    private static boolean isDefaultMouseSensitivitySet = false;

    /**
     * This method changes the Field of View (FOV) based on the zoom level.
     * <p>
     * If the zoom key is down, the method increases the zoom level to a target zoom level.
     * If smooth zoom is enabled in the configurations, it smoothly transitions the zoom level from the current level to the target level.
     * <p>
     * If the smooth camera configuration is disabled, the mouse sensitivity is also adjusted based on the zoom level. If the zoom key is down, mouse sensitivity is decreased.
     * When the zoom key is released, mouse sensitivity is reset to its default value.
     * <p>
     * The method also enables or disables smooth camera based on the configuration when zoom key is down or released.
     *
     * @param fov The initial Field of View to be adjusted based on the zoom level.
     *
     * @return The adjusted Field of View after considering the current zoom level.
     */
    public static float changeFovBasedOnZoom(float fov) {
        float targetLevel = 1;
        long currentFrameTime = System.currentTimeMillis();
        float deltaTime = (currentFrameTime - lastFrameTime) / 1000.0F;
        lastFrameTime = currentFrameTime;

        if (zoom.isKeyDown()) {
            targetLevel = (float) ValkyrieConfig.zoom.zoomMultiplier;

            if (!isDefaultMouseSensitivitySet) {
                defaultMouseSensitivity = mc.gameSettings.mouseSensitivity;
                isDefaultMouseSensitivitySet = true;
            }

            if (ValkyrieConfig.zoom.smoothCamera) {
                mc.gameSettings.smoothCamera = true;
                mc.gameSettings.mouseSensitivity = (float) (defaultMouseSensitivity * (currentLevel / ValkyrieConfig.zoom.zoomMultiplier));
            } else
                mc.gameSettings.mouseSensitivity = defaultMouseSensitivity / currentLevel;
        } else if (isDefaultMouseSensitivitySet) {
            if (ValkyrieConfig.zoom.smoothCamera)
                mc.gameSettings.smoothCamera = false;

            mc.gameSettings.mouseSensitivity = defaultMouseSensitivity;

            isDefaultMouseSensitivitySet = false;
        }

        if (ValkyrieConfig.zoom.smoothZoom)
            currentLevel += (targetLevel - currentLevel) * ValkyrieConfig.zoom.smoothZoomSpeed * deltaTime;
        else
            currentLevel = targetLevel;

        return fov / currentLevel;
    }
}
