package io.redstudioragnarok.valkyrie.mixin;

import net.jafama.FastMath;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
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

    /**
     * @reason Improving performance, this updated implementation utilizes a rotation matrix to handle all rotation transformations at once, thus reducing the number of trigonometric computations and OGL calls. Moreover, it utilizes a buffer to directly load the rotation matrix into OGL.
     * @author Desoroxxx
     */
    @Overwrite
    public void render(float scale) {
        if (this.isHidden || !this.showModel)
            return;

        if (!this.compiled)
            this.compileDisplayList(scale);

        GL11.glPushMatrix();

        GL11.glTranslatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

        final float cosX = (float) FastMath.cos(this.rotateAngleX);
        final float sinX = (float) FastMath.sin(this.rotateAngleX);
        final float cosY = (float) FastMath.cos(this.rotateAngleY);
        final float sinY = (float) FastMath.sin(this.rotateAngleY);
        final float cosZ = (float) FastMath.cos(this.rotateAngleZ);
        final float sinZ = (float) FastMath.sin(this.rotateAngleZ);

        final float[] rotationMatrix = {cosY * cosZ, cosY * sinZ, -sinY, 0, sinX * sinY * cosZ - cosX * sinZ, sinX * sinY * sinZ + cosX * cosZ, sinX * cosY, 0, cosX * sinY * cosZ + sinX * sinZ, cosX * sinY * sinZ - sinX * cosZ, cosX * cosY, 0, 0, 0, 0, 1};

        buffer.put(rotationMatrix).flip();
        GL11.glMultMatrix(buffer);

        GL11.glCallList(this.displayList);

        if (this.childModels != null)
            for (ModelRenderer childModel : this.childModels)
                childModel.render(scale);

        GL11.glPopMatrix();

        GL11.glTranslatef(-this.offsetX, -this.offsetY, -this.offsetZ);
    }
}
