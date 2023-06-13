package io.redstudioragnarok.valkyrie.mixin;

import io.redstudioragnarok.valkyrie.Valkyrie;
import io.redstudioragnarok.valkyrie.config.ValkyrieConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.apache.commons.io.IOUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static io.redstudioragnarok.valkyrie.Valkyrie.mc;
import static io.redstudioragnarok.valkyrie.utils.ModReference.*;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow private ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException { throw new AssertionError(); }

    /**
     * @reason Remove the version from the window title and add configurability.
     * @author Desoroxxx
     */
    @Overwrite
    private void createDisplay() throws LWJGLException {
        Display.setResizable(true);
        Display.setTitle(ValkyrieConfig.general.windowTitle);

        try {
            Display.create((new PixelFormat()).withDepthBits(ValkyrieConfig.general.highPrecisionDepthBuffer ? 32 : 24));
        } catch (LWJGLException lwjglexception) {
            LOG.error("Couldn't set pixel format", lwjglexception);
            LOG.error("Please report this issue:");
            LOG.error(NEW_ISSUE_URL);

            Display.create();
        }
    }

    /**
     * @reason Replace Minecraft icon with the new icons.
     * @author Desoroxxx
     */
    @Overwrite
    public void setWindowIcon() {
        InputStream icon16 = null;
        InputStream icon32 = null;
        InputStream icon48 = null;
        InputStream icon128 = null;
        InputStream icon256 = null;

        try {
            if (ValkyrieConfig.general.customIcons) {
                icon16 = getCustomIcon("icon_16");
                icon32 = getCustomIcon("icon_32");
                icon48 = getCustomIcon("icon_48");
                icon128 = getCustomIcon("icon_128");
                icon256 = getCustomIcon("icon_256");
            } else {
                if (VERSION.contains("Dev") || FMLLaunchHandler.isDeobfuscatedEnvironment()) {
                    icon16 = getIcon(true, 16);
                    icon32 = getIcon(true, 32);
                    icon48 = getIcon(true, 48);
                    icon128 = getIcon(true, 128);
                    icon256 = getIcon(true, 256);
                } else {
                    icon16 = getIcon(false, 16);
                    icon32 = getIcon(false, 32);
                    icon48 = getIcon(false, 48);
                    icon128 = getIcon(false, 128);
                    icon256 = getIcon(false, 256);
                }
            }

            Display.setIcon(new ByteBuffer[]{this.readImageToBuffer(icon16), this.readImageToBuffer(icon32), this.readImageToBuffer(icon48), this.readImageToBuffer(icon128), this.readImageToBuffer(icon256)});
        } catch (IOException ioException) {
            LOG.error("Couldn't set window icons", ioException);
            LOG.error("LWJGL default icons will not be replaced");
            LOG.error("Please report this issue:");
            LOG.error(NEW_ISSUE_URL);
        } catch (NullPointerException nullPointerException) {
            LOG.error("Couldn't set window icons", nullPointerException);
            LOG.error("LWJGL default icons will not be replaced");
            LOG.error("This is probably due to custom icons being enabled when no custom icons are set or found");
        } finally {
            IOUtils.closeQuietly(icon16);
            IOUtils.closeQuietly(icon32);
            IOUtils.closeQuietly(icon48);
            IOUtils.closeQuietly(icon128);
            IOUtils.closeQuietly(icon256);
        }
    }

    private static InputStream getIcon(final boolean dev, final int size) {
        return Valkyrie.class.getResourceAsStream("/assets/" + ID + "/icons/" + (dev ? "dev/" : "") + "icon_" + size + ".png");
    }

    private static InputStream getCustomIcon(final String name) {
        try {
            return new FileInputStream(mc.gameDir + "/resourcepacks/icons/" + name + ".png");
        } catch (FileNotFoundException fileNotFoundException) {
            LOG.error("Couldn't find the specified custom icon: {}", name, fileNotFoundException);

            return null;
        }
    }
}
