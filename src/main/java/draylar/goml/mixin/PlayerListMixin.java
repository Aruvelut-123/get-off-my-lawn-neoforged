package draylar.goml.mixin;

import draylar.goml.block.augment.HeavenWingsAugmentBlock;
import draylar.goml.api.event.ServerPlayerUpdateEvents;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Inject(method = "remove", at = @At("HEAD"))
    private void goml_remove(ServerPlayer player, CallbackInfo ci) {
        HeavenWingsAugmentBlock.revokeFlight(player);
    }

    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;"), slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=multiplayer.player.joined.renamed")))
    private void goml_onPlayerConnect(Connection connection, ServerPlayer player, CommonListenerCookie clientData, CallbackInfo ci) {
        ServerPlayerUpdateEvents.NAME_CHANGED.invoker().onNameChanged(player);
    }
}
