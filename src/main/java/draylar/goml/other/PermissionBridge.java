package draylar.goml.other;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.function.Predicate;

import static draylar.goml.GetOffMyLawn.id;

/**
 * Registers and queries native NeoForge permission nodes while preserving the
 * permission identifiers used by the Fabric version.
 */
public final class PermissionBridge {
    private static final Map<Identifier, PermissionNode<Boolean>> BOOLEAN_NODES = new LinkedHashMap<>();
    private static final Map<Identifier, PermissionNode<Integer>> INTEGER_NODES = new LinkedHashMap<>();

    static {
        booleanNode("command/help", true);
        booleanNode("command/trust", true);
        booleanNode("command/untrust", true);
        booleanNode("command/addowner", true);
        booleanNode("command/list", true);
        booleanNode("command/escape", true);
        booleanNode("command/fixaugments", true);
        booleanNode("command/admin/escape", true);
        booleanNode("command/admin/removeowner", true);

        levelNode("command/admin", PermissionLevel.ADMINS);
        levelNode("command/admin/admin_mode", PermissionLevel.ADMINS);
        levelNode("command/admin/info", PermissionLevel.ADMINS);
        levelNode("command/admin/world", PermissionLevel.ADMINS);
        levelNode("command/admin/general", PermissionLevel.ADMINS);
        levelNode("command/admin/remove", PermissionLevel.ADMINS);
        levelNode("command/admin/reload", PermissionLevel.OWNERS);
        levelNode("command/admin/updateallclaims", PermissionLevel.OWNERS);
        levelNode("modify_others", PermissionLevel.ADMINS);
        levelNode("teleport", PermissionLevel.ADMINS);

        integerNode("claim_limit");
        integerNode("claim_limit/minecraft/overworld");
        integerNode("claim_limit/minecraft/the_nether");
        integerNode("claim_limit/minecraft/the_end");
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(PermissionBridge::gatherNodes);
    }

    private static void gatherNodes(PermissionGatherEvent.Nodes event) {
        BOOLEAN_NODES.values().forEach(event::addNodes);
        INTEGER_NODES.values().forEach(event::addNodes);
    }

    private static void booleanNode(String path, boolean defaultValue) {
        var identifier = id(path);
        BOOLEAN_NODES.put(identifier, new PermissionNode<>(
                identifier,
                PermissionTypes.BOOLEAN,
                (player, uuid, contexts) -> defaultValue
        ));
    }

    private static void levelNode(String path, PermissionLevel level) {
        var identifier = id(path);
        BOOLEAN_NODES.put(identifier, new PermissionNode<>(
                identifier,
                PermissionTypes.BOOLEAN,
                (player, uuid, contexts) -> player != null
                        && player.permissions().hasPermission(new Permission.HasCommandLevel(level))
        ));
    }

    private static void integerNode(String path) {
        var identifier = id(path);
        INTEGER_NODES.put(identifier, new PermissionNode<>(
                identifier,
                PermissionTypes.INTEGER,
                (player, uuid, contexts) -> -1
        ));
    }

    public static boolean checkPermission(Player player, Identifier permission, PermissionLevel level) {
        if (player instanceof ServerPlayer serverPlayer) {
            var node = BOOLEAN_NODES.get(permission);
            if (node != null) {
                return PermissionAPI.getPermission(serverPlayer, node);
            }
        }
        return player.permissions().hasPermission(new Permission.HasCommandLevel(level));
    }

    public static boolean checkPermission(CommandSourceStack source, Identifier permission, PermissionLevel level) {
        if (source.getPlayer() instanceof ServerPlayer serverPlayer) {
            var node = BOOLEAN_NODES.get(permission);
            if (node != null) {
                return PermissionAPI.getPermission(serverPlayer, node);
            }
        }
        return source.permissions().hasPermission(new Permission.HasCommandLevel(level));
    }

    public static boolean checkPermission(Player player, Identifier permission, boolean defaultValue) {
        if (player instanceof ServerPlayer serverPlayer) {
            var node = BOOLEAN_NODES.get(permission);
            if (node != null) {
                return PermissionAPI.getPermission(serverPlayer, node);
            }
        }
        return defaultValue;
    }

    public static boolean checkPermission(CommandSourceStack source, Identifier permission, boolean defaultValue) {
        if (source.getPlayer() instanceof ServerPlayer serverPlayer) {
            var node = BOOLEAN_NODES.get(permission);
            if (node != null) {
                return PermissionAPI.getPermission(serverPlayer, node);
            }
        }
        return defaultValue;
    }

    public static Predicate<CommandSourceStack> require(Identifier permission, PermissionLevel level) {
        return source -> checkPermission(source, permission, level);
    }

    public static Predicate<CommandSourceStack> require(Identifier permission, boolean defaultValue) {
        return source -> checkPermission(source, permission, defaultValue);
    }

    public static OptionalInt checkPermissionInteger(Player player, Identifier permission) {
        if (player instanceof ServerPlayer serverPlayer) {
            var node = INTEGER_NODES.get(permission);
            if (node != null) {
                int value = PermissionAPI.getPermission(serverPlayer, node);
                if (value >= 0) {
                    return OptionalInt.of(value);
                }
            }
        }
        return OptionalInt.empty();
    }

    private PermissionBridge() {
    }
}
