package draylar.goml.mixin;

import com.jamieswhiteshirt.rtree3i.Entry;
import com.jamieswhiteshirt.rtree3i.Selection;
import draylar.goml.api.ClaimBox;
import draylar.goml.api.Claim;
import draylar.goml.api.ClaimUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin which prevents TNT primed by player A from going off in player B's claim.
 */
@Mixin(PrimedTnt.class)
public abstract class TntEntityMixin extends Entity {

    @Shadow private LivingEntity owner;

    public TntEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(at = @At("HEAD"), method = "explode", cancellable = true)
    private void goml_attemptExplosion(CallbackInfo ci) {
        if (level().isClientSide() || this.owner == null) {
            return;
        }

        Selection<Entry<ClaimBox, Claim>> claimsFound = ClaimUtils.getClaimsAt(level(), blockPosition());
        if (!claimsFound.isEmpty()) {
            boolean noPermission;
            if (this.owner instanceof Player player) {
                noPermission = claimsFound.anyMatch(boxInfo -> !boxInfo.getValue().hasPermission(player));
            } else {
                noPermission = claimsFound.anyMatch(boxInfo -> !boxInfo.getValue().hasPermission(this.owner.getUUID()));
            }

            if (noPermission) {
                ci.cancel();
            }
        }
    }
}
