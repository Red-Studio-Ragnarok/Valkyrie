package io.redstudioragnarok.valkyrie.mixin;

import net.jafama.FastMath;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.FloatBuffer;
import java.util.List;

@Mixin(ModelRenderer.class)
public class ModelRendererMixin {

    @Shadow public float rotationPointX;
    @Shadow public float rotationPointY;
    @Shadow public float rotationPointZ;
    @Shadow public float rotateAngleX;
    @Shadow public float rotateAngleY;
    @Shadow public float rotateAngleZ;
    @Shadow private boolean compiled;
    @Shadow private int displayList;
    @Shadow public boolean showModel;
    @Shadow public boolean isHidden;
    @Shadow public List<ModelRenderer> childModels;
    @Shadow @Final public String boxName;
    @Shadow public float offsetX;
    @Shadow public float offsetY;
    @Shadow public float offsetZ;

    final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);

    @Shadow
    private void compileDisplayList(float scale) {
        throw new AssertionError();
    }

    /**
     * @reason Improving performance
     * @author Desoroxxx
     */
    @Overwrite
    @SideOnly(Side.CLIENT)
    public void render(float scale) {
        if (this.isHidden || !this.showModel)
            return;

        if (!this.compiled)
            this.compileDisplayList(scale);

        GL11.glPushMatrix();

        // Apply the rotation point transformation
        GL11.glTranslatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

        // Compute rotation matrix
        final float cosX = (float) FastMath.cos(this.rotateAngleX);
        final float sinX = (float) FastMath.sin(this.rotateAngleX);
        final float cosY = (float) FastMath.cos(this.rotateAngleY);
        final float sinY = (float) FastMath.sin(this.rotateAngleY);
        final float cosZ = (float) FastMath.cos(this.rotateAngleZ);
        final float sinZ = (float) FastMath.sin(this.rotateAngleZ);

        // Build rotation matrix
        final float[] rotationMatrix = {cosY * cosZ, cosY * sinZ, -sinY, 0, sinX * sinY * cosZ - cosX * sinZ, sinX * sinY * sinZ + cosX * cosZ, sinX * cosY, 0, cosX * sinY * cosZ + sinX * sinZ, cosX * sinY * sinZ - sinX * cosZ, cosX * cosY, 0, 0, 0, 0, 1};

        // Load rotation matrix into OpenGL
        buffer.put(rotationMatrix).flip();
        GL11.glMultMatrix(buffer);

        // Render the display list and the child models
        GL11.glCallList(this.displayList);

        if (this.childModels != null)
            for (ModelRenderer childModel : this.childModels)
                childModel.render(scale);

        GL11.glPopMatrix();

        // Translate back by the offset amount
        GL11.glTranslatef(-this.offsetX, -this.offsetY, -this.offsetZ);
    }
}
