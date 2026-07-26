package draylar.goml.api.event;

import net.minecraft.server.level.ServerPlayer;

public class ServerPlayerUpdateEvents {
    public static final SimpleEvent<PlayerNameChangedEvent> NAME_CHANGED = new SimpleEvent<>(
        (listeners) -> player -> {
            for (var event : listeners) {
                event.onNameChanged(player);
            }
        }
    );

    @FunctionalInterface
    public interface PlayerNameChangedEvent {
        void onNameChanged(ServerPlayer player);
    }
}
