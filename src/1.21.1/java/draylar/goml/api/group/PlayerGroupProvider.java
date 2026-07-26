package draylar.goml.api.group;

import draylar.goml.other.GomlObjects;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public interface PlayerGroupProvider {
    static void register(String namespace, PlayerGroupProvider instance) {
        GomlObjects.PROVIDER_MAP.put(namespace, instance);
    }

    static Collection<PlayerGroupProvider> getAll() {
        return GomlObjects.PROVIDER_MAP.values();
    }

    @Nullable
    static PlayerGroupProvider get(String namespace) {
        return GomlObjects.PROVIDER_MAP.get(namespace);
    }

    static PlayerGroup getGroup(MinecraftServer server, PlayerGroup.Key key) {
        var p = get(key.providerId());

        if (p != null) {
            return p.fromKey(server, key);
        }
        return null;
    }

    static Collection<PlayerGroup> getShared(ServerPlayer player, UUID id) {
        var list = new ArrayList<PlayerGroup>();
        for (var provider : getAll()) {
            var g = provider.getGroupOf(player);
            if (g != null && g.isPartOf(id)) {
                list.add(g);
            }
        }
        return list;
    }

    static Collection<PlayerGroup> getAllGroups(ServerPlayer player) {
        var list = new ArrayList<PlayerGroup>();
        for (var provider : getAll()) {
            var g = provider.getGroupOf(player);
            if (g != null) {
                list.add(g);
            }
        }
        return list;
    }

    @Nullable
    PlayerGroup getGroupOf(Player player);

    @Nullable
    PlayerGroup getGroupOf(MinecraftServer server, UUID uuid);

    @Nullable
    PlayerGroup fromKey(MinecraftServer server, PlayerGroup.Key key);

    Component getName();

}
