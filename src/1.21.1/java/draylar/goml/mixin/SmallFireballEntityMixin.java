package draylar.goml.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import draylar.goml.api.ClaimUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(SmallFireball.class)
public abstract class SmallFireballEntityMixin extends Fireball {

    public SmallFireballEntityMixin(EntityType<? extends Fireball> entityType, Level world) {
        super(entityType, world);
    }

    @WrapWithCondition(method = "onHitBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean safeSetBlock(Level world, BlockPos pos, BlockState state) {
        if (world.isClientSide()) {
            return true;
        }

        return ClaimUtils.canExplosionDestroy(world, pos, this);
    }
}
