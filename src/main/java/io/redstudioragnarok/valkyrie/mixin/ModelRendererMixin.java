package io.redstudioragnarok.valkyrie.mixin;

import net.jafama.FastMath;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

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

    @Shadow private void compileDisplayList(float scale) { throw new AssertionError(); }

    private final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);

    private float prevRotateAngleX;
    private float prevRotateAngleY;
    private float prevRotateAngleZ;

    private float[] rotationMatrix = new float[16];

    /**
     *
     * @reason Improving performance, this updated implementation utilizes a rotation matrix to handle all rotation transformations at once, thus reducing the number of trigonometric computations and OGL calls. Moreover, it utilizes a buffer to directly load the rotation matrix into OGL.
     * @author Desoroxxx
     */
    @Overwrite
    public void render(float scale) {
        if (isHidden || !showModel)
            return;

        if (!compiled)
            compileDisplayList(scale);

        GlStateManager.pushMatrix();

        GlStateManager.translate(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);

        if (rotateAngleX != prevRotateAngleX || rotateAngleY != prevRotateAngleY || rotateAngleZ != prevRotateAngleZ) {
            final float cosX = (float) FastMath.cos(rotateAngleX);
            final float sinX = (float) FastMath.sin(rotateAngleX);
            final float cosY = (float) FastMath.cos(rotateAngleY);
            final float sinY = (float) FastMath.sin(rotateAngleY);
            final float cosZ = (float) FastMath.cos(rotateAngleZ);
            final float sinZ = (float) FastMath.sin(rotateAngleZ);

            rotationMatrix = new float[]{cosY * cosZ, cosY * sinZ, -sinY, 0, sinX * sinY * cosZ - cosX * sinZ, sinX * sinY * sinZ + cosX * cosZ, sinX * cosY, 0, cosX * sinY * cosZ + sinX * sinZ, cosX * sinY * sinZ - sinX * cosZ, cosX * cosY, 0, 0, 0, 0, 1};

            buffer.put(rotationMatrix).flip();

            prevRotateAngleX = rotateAngleX;
            prevRotateAngleY = rotateAngleY;
            prevRotateAngleZ = rotateAngleZ;
        }

        GlStateManager.multMatrix(buffer);

        GlStateManager.callList(displayList);

        if (childModels != null)
            for (ModelRenderer childModel : childModels)
                childModel.render(scale);

        GlStateManager.popMatrix();

        GlStateManager.translate(-offsetX, -offsetY, -offsetZ);
    }
}
