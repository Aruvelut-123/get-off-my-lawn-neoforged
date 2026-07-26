package draylar.goml.api;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Defines behavior for a claim Augment, which handles events for players inside claims.
 */
public interface Augment {

    static Augment noop() {
        return new Augment() {
        };
    }

    default void onPlayerEnter(Claim claim, Player player) {

    }

    default void onPlayerExit(Claim claim, Player player) {

    }

    default void tick(Claim claim, Level world) {

    }

    default void playerTick(Claim claim, Player player) {

    }

    default void onLoaded(Claim claim, BlockPos key) {

    }

    default boolean ticks() {
        return false;
    }

    default boolean canPlace(Claim claim, Level world, BlockPos pos) {
        return true;
    }

    default boolean hasSettings() {
        return false;
    }

    default void openSettings(Claim claim, ServerPlayer player, @Nullable Runnable closeCallback) {
    }

    default boolean isEnabled(Claim claim, Level world) {
        return true;
    }

    default Component getAugmentName() {
        return Component.literal("<unknown>");
    }
}
