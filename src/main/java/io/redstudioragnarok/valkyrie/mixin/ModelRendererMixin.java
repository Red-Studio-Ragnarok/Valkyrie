package io.redstudioragnarok.valkyrie.mixin;

import net.jafama.FastMath;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.nio.FloatBuffer;
import java.util.List;

@Mixin(ModelRenderer.class)
public class ModelRendererMixin {

    @Shadow private float rotationPointX;
    @Shadow private float rotationPointY;
    @Shadow private float rotationPointZ;
    @Shadow private float rotateAngleX;
    @Shadow private float rotateAngleY;
    @Shadow private float rotateAngleZ;
    @Shadow private boolean compiled;
    @Shadow private int displayList;
    @Shadow private boolean showModel;
    @Shadow private boolean isHidden;
    @Shadow private List<ModelRenderer> childModels;
    @Shadow private float offsetX;
    @Shadow private float offsetY;
    @Shadow private float offsetZ;

    @Shadow private void compileDisplayList(float scale) {throw new AssertionError();}

    @Unique private final FloatBuffer valkyrie$buffer = BufferUtils.createFloatBuffer(16);

    /**
     * @reason Improving performance, this updated implementation utilizes a rotation matrix to handle all rotation transformations at once, thus reducing the number of trigonometric computations and OGL calls. Moreover, it utilizes a buffer to directly load the rotation matrix into OGL.
     * @author Desoroxxx
     */
    @Overwrite
    @SideOnly(Side.CLIENT)
    public void render(float scale) {
        if (isHidden || !showModel)
            return;

        if (!compiled)
            compileDisplayList(scale);

        GlStateManager.pushMatrix();

        GlStateManager.translate(offsetX, offsetY, offsetZ);

        GlStateManager.translate(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);

        final float cosX = (float) FastMath.cosQuick(rotateAngleX);
        final float sinX = (float) FastMath.sinQuick(rotateAngleX);
        final float cosY = (float) FastMath.cosQuick(rotateAngleY);
        final float sinY = (float) FastMath.sinQuick(rotateAngleY);
        final float cosZ = (float) FastMath.cosQuick(rotateAngleZ);
        final float sinZ = (float) FastMath.sinQuick(rotateAngleZ);


//      ┌─────────────────────────────┬─────────────────────────────┬─────────────────┬───┐
//      │  cosY*cosZ                  │  cosY*sinZ                  │  -sinY          │ 0 │
//      │  cosZ*sinX*sinY - cosX*sinZ │  cosX*cosZ + sinX*sinY*sinZ │  cosY*sinX      │ 0 │
//      │  cosX*cosZ*sinY + sinX*sinZ │ -cosZ*sinX + cosX*sinY*sinZ │  cosX*cosY      │ 0 │
//      │  0                          │  0                          │  0              │ 1 │
//      └─────────────────────────────┴─────────────────────────────┴─────────────────┴───┘
        final float[] rotationMatrix = {
                cosY * cosZ,                        cosY * sinZ,                       -sinY,                         0,
                sinX * sinY * cosZ - cosX * sinZ,   cosX * cosZ + sinX * sinY * sinZ,   sinX * cosY,                  0,
                cosX * sinY * cosZ + sinX * sinZ,  -cosZ * sinX + cosX * sinY * sinZ,   cosX * cosY,                  0,
                0,                                  0,                                  0,                            1
        };

        valkyrie$buffer.put(rotationMatrix).flip();
        GL11.glMultMatrix(valkyrie$buffer);

        GlStateManager.callList(displayList);

        if (childModels != null)
            for (ModelRenderer childModel : childModels)
                childModel.render(scale);

        GlStateManager.popMatrix();
    }
}
