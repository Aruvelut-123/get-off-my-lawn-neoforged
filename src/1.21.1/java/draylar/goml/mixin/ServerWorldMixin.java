package draylar.goml.mixin;

import draylar.goml.other.OriginOwner;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public class ServerWorldMixin {
    @Inject(method = "addEntity", at = @At("HEAD"))
    private void onEntitySpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (((OriginOwner) entity).goml$getOrigin() == null) {
            ((OriginOwner) entity).goml$setOrigin(entity.blockPosition());
        }
    }
}
