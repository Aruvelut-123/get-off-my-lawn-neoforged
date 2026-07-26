package draylar.goml.mixin;

import draylar.goml.other.GomlPlayer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerEntityMixin implements GomlPlayer {

    @Unique
    private boolean goml_adminMode = false;

    @Inject(method = "restoreFrom", at = @At("HEAD"))
    private void goml_copyAdminMode(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        this.goml_adminMode = ((GomlPlayer) oldPlayer).goml_getAdminMode();
    }

    @Override
    public void goml_setAdminMode(boolean value) {
        this.goml_adminMode = value;
    }

    @Override
    public boolean goml_getAdminMode() {
        return this.goml_adminMode;
    }
}
