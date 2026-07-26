package draylar.goml.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import draylar.goml.api.ClaimUtils;
import draylar.goml.other.OriginOwner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity implements OriginOwner {
    public FallingBlockEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;canPlaceAt(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z"))
    private boolean cantPlaceOnClaim(boolean bool, @Local(ordinal = 0) BlockPos pos) {
        if (this.level().isClientSide()) {
            return bool;
        }

        if (bool) {
            return ClaimUtils.hasMatchingClaims(this.level(), pos, this.goml$getOriginSafe());
        }

        return false;
    }

    @Inject(method = "causeFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;ceil(F)I", ordinal = 0), cancellable = true)
    private void blockFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (this.level().isClientSide()) {
            return;
        }

        if (!ClaimUtils.hasMatchingClaims(this.level(), this.blockPosition(), this.goml$getOriginSafe())) {
            cir.setReturnValue(false);
        }
    }
}
