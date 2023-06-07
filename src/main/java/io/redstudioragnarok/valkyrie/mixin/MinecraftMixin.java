package io.redstudioragnarok.valkyrie.mixin;

import io.redstudioragnarok.valkyrie.Valkyrie;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static io.redstudioragnarok.valkyrie.utils.ModReference.*;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow private ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException { throw new AssertionError(); }

    /**
     * @reason Replace Minecraft icon with the new icons.
     * @author Desoroxxx
     */
    @Overwrite
    private void setWindowIcon() {
        InputStream icon16 = null;
        InputStream icon32 = null;
        InputStream icon48 = null;
        InputStream icon128 = null;
        InputStream icon256 = null;

        try {
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

            Display.setIcon(new ByteBuffer[]{this.readImageToBuffer(icon16), this.readImageToBuffer(icon32), this.readImageToBuffer(icon48), this.readImageToBuffer(icon128), this.readImageToBuffer(icon256)});
        } catch (IOException ioexception) {
            LOG.error("Couldn't set Minecraft icons", ioexception);
            LOG.error("LWJGL default icons will not be replaced");
            LOG.error("Please report this issue:");
            LOG.error("https://linkify.cz/ValkyrieBugReport");
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
}
