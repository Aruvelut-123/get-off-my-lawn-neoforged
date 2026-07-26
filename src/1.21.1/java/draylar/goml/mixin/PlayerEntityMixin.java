package draylar.goml.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import draylar.goml.api.ClaimUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @ModifyExpressionValue(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
            )
    )
    private List<LivingEntity> filterSweepTargets(List<LivingEntity> original) {
        var source = this.damageSources().playerAttack((Player) (Object) this);
        original.removeIf(entity -> !ClaimUtils.canDamageEntity(this.level(), entity, source));
        return original;
    }
}
