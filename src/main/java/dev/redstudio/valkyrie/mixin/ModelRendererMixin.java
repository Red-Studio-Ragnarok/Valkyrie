package dev.redstudio.valkyrie.mixin;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.nio.FloatBuffer;
import java.util.List;

/// Improves the performance of the [ModelRenderer][net.minecraft.client.model.ModelRenderer] class.
///
/// It uses a rotation matrix to handle all rotation transformations at once, thus reducing the number of OGL calls.
/// Moreover, it utilizes faster trigonometric computations.
///
/// @author Luna Lage (Desoroxxx)
/// @author Nessiesson
/// @author Ven
/// @since 0.2
@Mixin(ModelRenderer.class)
public abstract class ModelRendererMixin {

	@Shadow
	private float rotationPointX;
	@Shadow
	private float rotationPointY;
	@Shadow
	private float rotationPointZ;
	@Shadow
	private float rotateAngleX;
	@Shadow
	private float rotateAngleY;
	@Shadow
	private float rotateAngleZ;
	@Shadow
	private boolean compiled;
	@Shadow
	private int displayList;
	@Shadow
	private boolean showModel;
	@Shadow
	private boolean isHidden;
	@Shadow
	private List<ModelRenderer> childModels;
	@Shadow
	private float offsetX;
	@Shadow
	private float offsetY;
	@Shadow
	private float offsetZ;

	@Shadow
	protected abstract void compileDisplayList(final float scale);

	@Unique
	private static final FloatBuffer valkyrie$buffer = BufferUtils.createFloatBuffer(16);
	@Unique
	private static final Matrix4f valkyrie$matrix = new Matrix4f();

	/// @reason Use a rotation matrix to handle all rotation transformations at once, thus reducing the number of OGL calls.
	/// @reason Utilizes faster trigonometric computations.
	/// @author Luna Lage (Desoroxxx)
	@Overwrite
	public void render(final float scale) {
		if (isHidden || !showModel)
			return;

		if (!compiled)
			compileDisplayList(scale);

		GlStateManager.pushMatrix();

		valkyrie$applyTransformation(scale, true);

		GlStateManager.callList(displayList);

		if (childModels != null)
			for (final ModelRenderer childModel : childModels)
				childModel.render(scale);

		GlStateManager.popMatrix();
	}

	/// @reason Use a rotation matrix to handle all rotation transformations at once, thus reducing the number of OGL calls.
	/// @reason Utilizes faster trigonometric computations.
	/// @author Luna Lage (Desoroxxx)
	@Overwrite
	public void renderWithRotation(final float scale) {
		if (isHidden || !showModel)
			return;

		if (!compiled)
			compileDisplayList(scale);

		GlStateManager.pushMatrix();

		valkyrie$applyTransformation(scale, false);

		GlStateManager.callList(displayList);

		GlStateManager.popMatrix();
	}

	/// @reason Use a rotation matrix to handle all rotation transformations at once, thus reducing the number of OGL calls.
	/// @reason Utilizes faster trigonometric computations.
	/// @author Luna Lage (Desoroxxx)
	@Overwrite
	public void postRender(final float scale) {
		if (isHidden || !showModel)
			return;

		if (!compiled)
			compileDisplayList(scale);

		valkyrie$applyTransformation(scale, false);
	}

	/// Apply the model rotation using a rotation matrix updating only the relevant values.
	///
	/// @author Luna Lage (Desoroxxx)
	/// @author Ven
	@Unique
	private void valkyrie$applyTransformation(final float scale, final boolean applyOffset) {
		valkyrie$matrix.identity();

		if (applyOffset)
			valkyrie$matrix.translate(offsetX, offsetY, offsetZ);

		if (this.rotateAngleX == 0 && this.rotateAngleY == 0 && this.rotateAngleZ == 0) {
			if (this.rotationPointX != 0 || this.rotationPointY != 0 || this.rotationPointZ != 0)
				valkyrie$matrix.translate(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
		} else {
			valkyrie$matrix.translate(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);

			if (rotateAngleZ != 0)
				valkyrie$matrix.rotate(rotateAngleZ, 0, 0, 1);

			if (rotateAngleY != 0)
				valkyrie$matrix.rotate(rotateAngleY, 0, 1, 0);

			if (rotateAngleX != 0)
				valkyrie$matrix.rotate(rotateAngleX, 1, 0, 0);
		}

		valkyrie$matrix.get(valkyrie$buffer);
		GlStateManager.multMatrix(valkyrie$buffer);
	}
}
