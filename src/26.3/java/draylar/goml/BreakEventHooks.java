package draylar.goml;

import draylar.goml.api.ClaimUtils;
import draylar.goml.api.PermissionReason;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

final class BreakEventHooks {
    private BreakEventHooks() {
    }

    static void register() {
        NeoForge.EVENT_BUS.addListener(BreakEventHooks::onBreakBlock);
    }

    private static void onBreakBlock(BreakBlockEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        var result = EventHandlers.testPermission(
                ClaimUtils.getClaimsAt(event.getLevel(), event.getPos()),
                event.getPlayer(),
                InteractionHand.MAIN_HAND,
                event.getPos(),
                PermissionReason.BLOCK_PROTECTED
        );
        if (result instanceof InteractionResult.Fail) {
            event.setCanceled(true);
        }
    }
}
