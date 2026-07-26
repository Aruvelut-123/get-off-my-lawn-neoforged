package draylar.goml.mixin;

import draylar.goml.api.ClaimUtils;
import draylar.goml.other.LegacyNbtHelper;
import draylar.goml.other.OriginOwner;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements OriginOwner {
    @Shadow private Level level;

    @Shadow public abstract BlockPos blockPosition();

    @Unique
    private BlockPos originPos;

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void goml$isInvulnerable(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (this.level.isClientSide()) {
            return;
        }

        if (!ClaimUtils.canDamageEntity(this.level, (Entity) (Object) this, damageSource)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "saveWithoutId", at = @At("TAIL"))
    private void writeGomlNbt(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
        if (this.originPos != null) {
            nbt.put("goml:origin", LegacyNbtHelper.fromBlockPos(this.originPos));
        }
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void readGomlNbt(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains("goml:origin")) {
            this.originPos = LegacyNbtHelper.toBlockPos(nbt.getCompound("goml:origin"));
        }
    }

    @Override
    public BlockPos goml$getOrigin() {
        return this.originPos;
    }

    @Override
    public void goml$setOrigin(BlockPos pos) {
        this.originPos = pos;
    }

    @Override
    public void goml$tryFilling() {
        this.originPos = this.blockPosition();
    }
}
