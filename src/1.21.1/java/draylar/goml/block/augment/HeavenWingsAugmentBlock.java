package draylar.goml.block.augment;

import draylar.goml.GetOffMyLawn;
import draylar.goml.api.Claim;
import draylar.goml.api.ClaimUtils;
import draylar.goml.block.SelectiveClaimAugmentBlock;
import draylar.goml.registry.GOMLBlocks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HeavenWingsAugmentBlock extends SelectiveClaimAugmentBlock {
    private static final Set<UUID> GRANTED_FLIGHT = ConcurrentHashMap.newKeySet();

    public HeavenWingsAugmentBlock(Properties settings, String texture) {
        super("heaven_wings", settings, texture);
    }

    public static void registerEvents() {
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (!(event.getEntity() instanceof ServerPlayer player)) {
                return;
            }
            GetOffMyLawn.NEXT_TICK_TASK.add(() -> {
                if (player.isRemoved()) {
                    return;
                }

                var wings = GOMLBlocks.HEAVEN_WINGS.getFirst();
                boolean canFly = ClaimUtils.getClaimsAt(player.level(), player.blockPosition())
                        .filter(entry -> entry.getValue().hasAugment(wings)
                                && wings.canApply(entry.getValue(), player))
                        .isNotEmpty();
                if (!canFly) {
                    revokeFlight(player);
                }
            });
        });
    }

    @Override
    public void applyEffect(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            GRANTED_FLIGHT.add(player.getUUID());
            serverPlayer.getAbilities().mayfly = true;
            serverPlayer.onUpdateAbilities();
        }
    }

    @Override
    public void removeEffect(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            revokeFlight(serverPlayer);
        }
    }

    @Override
    public void onPlayerExit(Claim claim, Player player) {
        var canFly = ClaimUtils.getClaimsAt(player.level(), player.blockPosition())
                .filter(x -> x.getValue() != claim && x.getValue().hasAugment(this) && this.canApply(x.getValue(), player)).isNotEmpty();

        if (!canFly) {
            super.onPlayerExit(claim, player);
        }
    }

    public static void revokeFlight(ServerPlayer player) {
        if (GRANTED_FLIGHT.remove(player.getUUID()) && !player.isCreative() && !player.isSpectator()) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }
}
