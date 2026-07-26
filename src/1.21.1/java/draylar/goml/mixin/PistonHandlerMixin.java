package draylar.goml.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import draylar.goml.api.ClaimUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;

@Mixin(PistonStructureResolver.class)
public class PistonHandlerMixin {
    @Shadow @Final private List<BlockPos> toPush;
    @Shadow @Final private List<BlockPos> toDestroy;
    @Shadow @Final private Level level;
    @Shadow @Final private Direction pushDirection;
    @Unique
    private boolean claimsEmpty;
    private HashSet<UUID> trusted;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void storeClaimInfo(Level world, BlockPos pos, Direction dir, boolean retracted, CallbackInfo ci) {
        if (world.isClientSide()) {
            return;
        }

        var claims = ClaimUtils.getClaimsAt(world, pos);
        this.claimsEmpty = claims.isEmpty();
        this.trusted = new HashSet<>();
        claims.forEach(x -> {
            this.trusted.addAll(x.getValue().getOwners());
            this.trusted.addAll(x.getValue().getTrusted());
        });
    }

    @ModifyReturnValue(method = "resolve", at = @At("RETURN"))
    private boolean preventMovement(boolean value) {
        if (this.level.isClientSide()) {
            return value;
        }

        if (value) {
            if (!checkClaims(this.toPush) || !checkClaims(this.toDestroy)) {
                this.toPush.clear();
                this.toDestroy.clear();
                return false;
            }
            return true;
        }

        return false;
    }

    @Unique
    private boolean checkClaims(List<BlockPos> blocks) {
        for (var pos : blocks) {
            var claims = ClaimUtils.getClaimsAt(this.level, pos);

            boolean firstFound = true;

            if (claims.isEmpty() && this.claimsEmpty) {
                firstFound = false;
            }

            if (firstFound && claims.noneMatch(x -> x.getValue().hasPermission(this.trusted))) {
                return false;
            }

            var mut = new BlockPos.MutableBlockPos();
            claims = ClaimUtils.getClaimsAt(this.level, mut.set(pos).move(this.pushDirection));
            if (claims.isEmpty() && this.claimsEmpty) {
                continue;
            }

            if (claims.noneMatch(x -> x.getValue().hasPermission(this.trusted))) {
                return false;
            }
        }
        return true;
    }
}
