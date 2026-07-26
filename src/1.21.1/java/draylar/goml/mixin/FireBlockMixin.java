package draylar.goml.mixin;

import draylar.goml.api.ClaimUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class)
public class FireBlockMixin {
    @Inject(method = "checkBurnOut", at = @At("HEAD"), cancellable = true)
    private void goml_preventFire(Level world, BlockPos pos, int spreadFactor, RandomSource random, int currentAge, Direction direction, CallbackInfo ci) {
        if (world.isClientSide()) {
            return;
        }

        if (!ClaimUtils.canFireDestroy(world, pos)) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;scheduleBlockTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V", shift = At.Shift.AFTER), cancellable = true)
    private void goml_preventFire2(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (!ClaimUtils.canFireDestroy(world, pos)) {
            ci.cancel();
        }
    }
}
