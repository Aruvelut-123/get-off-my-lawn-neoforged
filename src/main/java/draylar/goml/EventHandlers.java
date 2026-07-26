package draylar.goml;

import com.jamieswhiteshirt.rtree3i.Entry;
import com.jamieswhiteshirt.rtree3i.Selection;
import draylar.goml.api.Claim;
import draylar.goml.api.ClaimBox;
import draylar.goml.api.ClaimUtils;
import draylar.goml.api.PermissionReason;
import draylar.goml.api.event.ClaimEvents;
import draylar.goml.block.entity.ClaimAnchorBlockEntity;
import draylar.goml.registry.GOMLBlocks;
import draylar.goml.registry.GOMLTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class EventHandlers {
    private EventHandlers() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(EventHandlers::onEntityInteract);
        NeoForge.EVENT_BUS.addListener(EventHandlers::onAttackEntity);
        NeoForge.EVENT_BUS.addListener(EventHandlers::onRightClickBlock);
        NeoForge.EVENT_BUS.addListener(EventHandlers::onLeftClickBlock);
        NeoForge.EVENT_BUS.addListener(EventHandlers::onBreakBlock);
    }

    private static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        var player = event.getEntity();
        var world = event.getLevel();
        var entity = event.getTarget();

        if (world.isClientSide() || ClaimUtils.isInAdminMode(player)) {
            return;
        }

        if (GetOffMyLawn.CONFIG.canInteract(entity) || entity.is(GOMLTags.ALLOWED_INTERACTIONS_ENTITY)) {
            return;
        }

        if (entity instanceof OwnableEntity ownable && ownable.getOwner() == player) {
            return;
        }

        InteractionResult result;
        if (entity instanceof Player targetPlayer) {
            result = checkPlayerInteraction(player, targetPlayer);
        } else {
            result = testPermission(
                    ClaimUtils.getClaimsAt(world, entity.blockPosition()),
                    player,
                    event.getHand(),
                    entity.blockPosition(),
                    PermissionReason.ENTITY_PROTECTED
            );
        }

        if (result instanceof InteractionResult.Fail) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    private static InteractionResult checkPlayerInteraction(Player player, Player target) {
        var claims = ClaimUtils.getClaimsAt(player.level(), target.blockPosition());
        if (claims.isEmpty()) {
            return InteractionResult.PASS;
        }

        claims = claims.filter(entry -> entry.getValue().hasAugment(GOMLBlocks.PVP_ARENA.getFirst()));
        if (claims.isEmpty()) {
            return GetOffMyLawn.CONFIG.enablePvPinClaims ? InteractionResult.PASS : InteractionResult.FAIL;
        }

        var result = new MutableObject<InteractionResult>(InteractionResult.PASS);
        claims.forEach(entry -> {
            if (result.getValue() instanceof InteractionResult.Fail) {
                return;
            }

            var claim = entry.getValue();
            result.setValue(switch (claim.getData(GOMLBlocks.PVP_ARENA.getFirst().key)) {
                case EVERYONE -> InteractionResult.PASS;
                case DISABLED -> InteractionResult.FAIL;
                case TRUSTED -> claim.hasPermission(player) && claim.hasPermission(target)
                        ? InteractionResult.PASS : InteractionResult.FAIL;
                case UNTRUSTED -> !claim.hasPermission(player) && !claim.hasPermission(target)
                        ? InteractionResult.PASS : InteractionResult.FAIL;
            });
        });
        return result.getValue();
    }

    private static void onAttackEntity(AttackEntityEvent event) {
        var player = event.getEntity();
        var world = player.level();
        if (!world.isClientSide()
                && !ClaimUtils.canDamageEntity(world, event.getTarget(), world.damageSources().playerAttack(player))) {
            event.setCanceled(true);
        }
    }

    private static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        var world = event.getLevel();
        var player = event.getEntity();
        if (world.isClientSide()) {
            return;
        }

        var pos = event.getPos();
        if (!(event.getItemStack().getItem() instanceof BlockItem)) {
            var state = world.getBlockState(pos);
            if (GetOffMyLawn.CONFIG.canInteract(state.getBlock())
                    || state.is(GOMLTags.ALLOWED_INTERACTIONS_BLOCKS)) {
                return;
            }
        }

        var result = testPermission(
                ClaimUtils.getClaimsAt(world, pos),
                player,
                event.getHand(),
                pos,
                PermissionReason.AREA_PROTECTED
        );
        if (result == InteractionResult.PASS) {
            var adjacent = pos.relative(event.getFace());
            result = testPermission(
                    ClaimUtils.getClaimsAt(world, adjacent),
                    player,
                    event.getHand(),
                    adjacent,
                    PermissionReason.AREA_PROTECTED
            );
        }

        if (result instanceof InteractionResult.Fail) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    private static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        var world = event.getLevel();
        var player = event.getEntity();
        if (world.isClientSide()) {
            return;
        }

        var pos = event.getPos();
        var blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ClaimAnchorBlockEntity anchor
                && !(anchor.getClaim().isOwner(player) || ClaimUtils.isInAdminMode(player))) {
            event.setCanceled(true);
            return;
        }

        var result = testPermission(
                ClaimUtils.getClaimsAt(world, pos),
                player,
                event.getHand(),
                pos,
                PermissionReason.BLOCK_PROTECTED
        );
        if (result instanceof InteractionResult.Fail) {
            event.setCanceled(true);
        }
    }

    private static void onBreakBlock(BreakBlockEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        var result = testPermission(
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

    @ApiStatus.Internal
    public static InteractionResult testPermission(
            Selection<Entry<ClaimBox, Claim>> claims,
            Player player,
            InteractionHand hand,
            BlockPos pos,
            PermissionReason reason
    ) {
        Level world = player.level();
        if (world.isClientSide()) {
            return InteractionResult.PASS;
        }

        if (!claims.isEmpty()) {
            boolean denied = claims.anyMatch(entry -> !entry.getValue().hasPermission(player));
            if (denied && !ClaimUtils.isInAdminMode(player)) {
                InteractionResult check = ClaimEvents.PERMISSION_DENIED.invoker()
                        .check(player, world, hand, pos, reason);
                if (check.consumesAction() || check == InteractionResult.PASS) {
                    player.sendOverlayMessage(reason.getReason());
                    return InteractionResult.FAIL;
                }
            }
        }

        return InteractionResult.PASS;
    }
}
