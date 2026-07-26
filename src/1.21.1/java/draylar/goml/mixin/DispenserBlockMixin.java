package draylar.goml.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import draylar.goml.api.ClaimUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {
    @Shadow @Final public static DirectionProperty FACING;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void safeSetBlock(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo ci) {
        var nextPos = pos.relative(state.getValue(FACING));

        if (!ClaimUtils.hasMatchingClaims(world, nextPos, pos)) {
            ci.cancel();
        }
    }
}
