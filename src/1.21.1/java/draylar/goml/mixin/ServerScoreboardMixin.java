package draylar.goml.mixin;

import draylar.goml.other.VanillaTeamGroups;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerScoreboard.class)
public class ServerScoreboardMixin {
    @Inject(method = "onTeamRemoved", at = @At("TAIL"))
    private void goml$removeTeamFromClaims(PlayerTeam team, CallbackInfo ci) {
        VanillaTeamGroups.onRemove(team);
    }
}
