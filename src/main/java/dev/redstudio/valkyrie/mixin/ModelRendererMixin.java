package dev.redstudio.valkyrie.mixin;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.spongepowered.asm.mixin.*;

import java.nio.FloatBuffer;
import java.util.List;

/// Improves the performance of the [ModelRenderer][ModelRenderer] class.
///
/// It uses a rotation matrix to handle all rotation transformations at once, thus reducing the number of OGL calls.
/// Moreover, it uses faster trigonometric computations.
///
/// @author Luna Lage (Desoroxxx)
/// @author Nessiesson
/// @author Ven
/// @since 0.2
@Mixin(ModelRenderer.class)
public abstract class ModelRendererMixin {

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
	@Shadow public float offsetX;
	@Shadow public float offsetY;
	@Shadow public float offsetZ;

	@Shadow protected abstract void compileDisplayList(final float scale);

	@Unique private static final FloatBuffer valkyrie$buffer = BufferUtils.createFloatBuffer(16);
	@Unique private static final Matrix4f valkyrie$matrix = new Matrix4f();

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

		if (rotateAngleX == 0 && rotateAngleY == 0 && rotateAngleZ == 0) {
			if (rotationPointX != 0 || rotationPointY != 0 || rotationPointZ != 0)
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
