package dev.redstudio.valkyrie.mixin;

import net.jafama.FastMath;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(MathHelper.class)
public class MathHelperMixin {

	/// @reason Improving performance
	/// @author Desoroxxx
	@Overwrite
	public static float sin(float value) {
		return (float) FastMath.sinQuick(value);
	}

	/// @reason Improving performance
	/// @author Desoroxxx
	@Overwrite
	public static float cos(float value) {
		return (float) FastMath.cosQuick(value);
	}

	/// @reason Improving performance
	/// @author Desoroxxx
	@Overwrite
	public static float sqrt(float value) {
		return (float) FastMath.sqrtQuick(value);
	}

	/// @reason Improving performance
	/// @author Desoroxxx
	@Overwrite
	public static float sqrt(double value) {
		return (float) FastMath.sqrt(value);
	}

	/// @reason Improving performance
	/// @author Desoroxxx
	@Overwrite
	public static int floor(float value) {
		return (int) FastMath.floor(value);
	}

	/// @reason Improving performance
	/// @author Desoroxxx
	@Overwrite
	public static int fastFloor(double value) {
		return (int) FastMath.floor(value);
	}

	/// @reason Improving performance
	/// @author Desoroxxx
	@Overwrite
	public static int floor(double value) {
		return (int) FastMath.floor(value);
	}

	/// @reason Improving performance
	/// @author Desoroxxx
	@Overwrite
	public static int absFloor(double value) {
		return (int) FastMath.floor(Math.abs(value));
	}

	/// @reason Improving performance
	/// @author Desoroxxx
	@Overwrite
	public static int abs(int value) {
		return FastMath.abs(value);
	}

	/// @reason Improving performance
	/// @author Desoroxxx
	@Overwrite
	public static int ceil(float value) {
		return (int) FastMath.ceil(value);
	}

	/// @reason Improving performance
	/// @author Desoroxxx
	@Overwrite
	public static int ceil(double value) {
		return (int) FastMath.ceil(value);
	}
}
