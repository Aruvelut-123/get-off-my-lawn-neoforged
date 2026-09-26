package draylar.goml.mixin.augment;

import draylar.goml.api.ClaimUtils;
import draylar.goml.registry.GOMLBlocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
public abstract class EnderManMixin extends Monster {

    private EnderManMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(
            method = "teleport(DDD)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void goml$attemptTeleport(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        boolean b = ClaimUtils.getClaimsAt(this.level(), this.blockPosition())
                .anyMatch(claim -> claim.getValue().hasAugment(GOMLBlocks.ENDER_BINDING.getFirst()));

        if (b) {
            cir.setReturnValue(false);
        }
    }

    @Mixin(targets = {"net/minecraft/world/entity/monster/EnderMan$EndermanLeaveBlockGoal"})
    public static abstract class EndermanLeaveBlockGoalMixin extends Goal {
        @Shadow @Final private EnderMan enderman;

        @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
        private void goml$cancelInClaim(CallbackInfoReturnable<Boolean> cir) {
            boolean b = ClaimUtils.getClaimsAt(this.enderman.level(), this.enderman.blockPosition())
                    .anyMatch(claim -> claim.getValue().hasAugment(GOMLBlocks.ENDER_BINDING.getFirst()));

            if (b) {
                cir.setReturnValue(false);
            }
        }
    }

    @Mixin(targets = {"net/minecraft/world/entity/monster/EnderMan$EndermanTakeBlockGoal"})
    public static abstract class EndermanTakeBlockGoalMixin extends Goal {
        @Shadow @Final private EnderMan enderman;

        @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
        private void goml$cancelInClaim(CallbackInfoReturnable<Boolean> cir) {
            boolean b = ClaimUtils.getClaimsAt(this.enderman.level(), this.enderman.blockPosition())
                    .anyMatch(claim -> claim.getValue().hasAugment(GOMLBlocks.ENDER_BINDING.getFirst()));

            if (b) {
                cir.setReturnValue(false);
            }
        }
    }
}
