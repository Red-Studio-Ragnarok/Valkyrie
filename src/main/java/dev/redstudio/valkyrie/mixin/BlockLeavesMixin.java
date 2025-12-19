package dev.redstudio.valkyrie.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.redstudio.valkyrie.config.ValkyrieConfig;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.redstudio.valkyrie.Valkyrie.MC;

// TODO: So Mojang added  new video settings in newer versions, which is See-Through Leaves, instead of the leaves having black spots it has colored but still darker pixels, this would be amazing
@Mixin(BlockLeaves.class)
public final class BlockLeavesMixin {

	@Shadow protected boolean leavesFancy;

	@Unique private static final BlockPos.MutableBlockPos valkyrie$mutableBlockPos = new BlockPos.MutableBlockPos();

	@Inject(method = "setGraphicsLevel", at = @At(value = "RETURN"))
	private void setValkyrieGraphicsLevel(final CallbackInfo callbackInfo) {
		leavesFancy = ValkyrieConfig.graphics.leaves.fancyLeaves;
	}

	@ModifyExpressionValue(method = "shouldSideBeRendered", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;shouldSideBeRendered(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z"))
	private boolean doCulling(final boolean isSideVisible, final IBlockState iBlockState, final IBlockAccess iBlockAccess, final BlockPos blockPos, final EnumFacing enumFacing) {
		if (!ValkyrieConfig.graphics.leaves.leavesCulling || !ValkyrieConfig.graphics.leaves.fancyLeaves)
			return isSideVisible;

		final byte depth = ValkyrieConfig.graphics.leaves.leavesCullingDepth;

		valkyrie$mutableBlockPos.setPos(blockPos);

		final World world = MC.world;

		for (byte i = 1; i <= depth; i++) {
			valkyrie$mutableBlockPos.move(enumFacing);

			if (!(world.getBlockState(valkyrie$mutableBlockPos).getBlock() instanceof BlockLeaves))
				return isSideVisible;
		}

		return false;
	}
}
