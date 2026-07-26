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
    @Inject(
            method = "checkBurnOut(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ILnet/minecraft/util/RandomSource;I)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void goml_preventFire26_2(Level world, BlockPos pos, int spreadFactor, RandomSource random, int currentAge, CallbackInfo ci) {
        goml_cancelFireInClaim(world, pos, ci);
    }

    @Inject(
            method = "checkBurnOut(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ILnet/minecraft/util/RandomSource;Lnet/minecraft/core/Direction;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void goml_preventFire26_1(Level world, BlockPos pos, int spreadFactor, RandomSource random, Direction direction, CallbackInfo ci) {
        goml_cancelFireInClaim(world, pos, ci);
    }

    private static void goml_cancelFireInClaim(Level world, BlockPos pos, CallbackInfo ci) {
        if (world.isClientSide()) {
            return;
        }
        if (!ClaimUtils.canFireDestroy(world, pos)) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V", shift = At.Shift.AFTER), cancellable = true)
    private void goml_preventFire2(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (!ClaimUtils.canFireDestroy(world, pos)) {
            ci.cancel();
        }
    }
}
