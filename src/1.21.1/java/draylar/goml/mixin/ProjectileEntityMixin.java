package draylar.goml.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import draylar.goml.api.ClaimUtils;
import draylar.goml.other.OriginOwner;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

@Mixin(Projectile.class)
public abstract class ProjectileEntityMixin extends Entity implements OriginOwner {
    @Shadow @Nullable private UUID ownerUUID;

    public ProjectileEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "onHit", at = @At("HEAD"), cancellable = true)
    private void preventEffects(HitResult hitResult, CallbackInfo ci) {
        if (this.level().isClientSide()) {
            return;
        }

        if (!ClaimUtils.hasMatchingClaims(this.level(), this.blockPosition(), this.goml$getOriginSafe(), this.ownerUUID)) {
            ci.cancel();
        }
    }

    @Inject(method = "mayInteract", at = @At("HEAD"), cancellable = true)
    private void preventModification(Level world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (world.isClientSide()) {
            return;
        }

        if (!ClaimUtils.hasMatchingClaims(this.level(), this.blockPosition(), this.goml$getOriginSafe(), this.ownerUUID)) {
            cir.setReturnValue(false);
        }
    }
}
