package draylar.goml.mixin;

import draylar.goml.block.ClaimAnchorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBlockMixin {

    @Inject(at = @At("HEAD"), method = "isPushable", cancellable = true)
    private static void goml_isMoveable(BlockState state, Level world, BlockPos pos, Direction motionDir, boolean canBreak, Direction pistonDir, CallbackInfoReturnable<Boolean> cir) {
        if (world.isClientSide()) {
            return;
        }

        if(state.getBlock() instanceof ClaimAnchorBlock) {
            cir.setReturnValue(false);
        }
    }
}
