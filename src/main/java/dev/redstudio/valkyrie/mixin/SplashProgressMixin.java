package dev.redstudio.valkyrie.mixin;

import net.minecraftforge.fml.client.SplashProgress;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static dev.redstudio.valkyrie.ProjectConstants.NAME;
import static net.minecraftforge.fml.common.FMLLog.log;

@Mixin(value = SplashProgress.class, remap = false)
public class SplashProgressMixin {

    @Shadow(remap = false) private static int max_texture_size;

    /**
     * @reason Actually get the max texture size the OpenGL implementation supports, potentially allowing for bigger atlases on certain GPUs.
     * @author Desoroxxx
     */
    @Overwrite(remap = false)
    public static int getMaxTextureSize() {
        if (max_texture_size != -1)
            return max_texture_size;

        log.debug(NAME + ":" + "Max texture size isn't cached, getting it and caching...");

        max_texture_size = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);

        log.debug(NAME + ":" + "Max texture size is " + max_texture_size);

        return max_texture_size;
    }
}
