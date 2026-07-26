package draylar.goml.block.entity;

import com.mojang.serialization.Codec;
import draylar.goml.GetOffMyLawn;
import draylar.goml.api.Augment;
import draylar.goml.api.Claim;
import draylar.goml.api.ClaimBox;
import draylar.goml.api.ClaimUtils;
import draylar.goml.registry.GOMLEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClaimAnchorBlockEntity extends BlockEntity {

    private static final String AUGMENT_LIST_KEY = "AugmentPositions";

    private final List<BlockPos> loadPositions = new ArrayList<>();

    private Claim claim;
    private ClaimBox box;

    public ClaimAnchorBlockEntity(BlockPos pos, BlockState state) {
        super(GOMLEntities.CLAIM_ANCHOR.get(), pos, state);
    }

    public static <T extends BlockEntity> void tick(Level eWorld, BlockPos pos, BlockState state, T blockEntity) {
        if (eWorld instanceof ServerLevel world && blockEntity instanceof ClaimAnchorBlockEntity anchor) {

            // Claim is null, world probably just loaded, re-grab claim
            if (anchor.claim == null) {
                var collect = ClaimUtils.getClaimsAt(anchor.level, anchor.worldPosition).filter(x -> x.getValue().getOrigin().equals(pos)).collect(Collectors.toList());

                if (collect.isEmpty()) {
                    GetOffMyLawn.LOGGER.warn(String.format("A Claim Anchor at %s tried to initialize its claim, but one could not be found! Was the claim removed without the anchor?", anchor.worldPosition));
                    world.destroyBlock(pos, true);
                    for (var lPos : anchor.loadPositions) {
                        world.destroyBlock(lPos, true);
                    }
                    return;
                } else {
                    var entry = collect.get(0);
                    anchor.claim = entry.getValue();
                    anchor.box = entry.getKey();
                }
            }

            if (anchor.claim.isDestroyed()) {
                world.destroyBlock(pos, true);
                return;
            }

            // no augments, some queued from fromTag
            if (!anchor.loadPositions.isEmpty()) {
                anchor.claim.internal_disableUpdates();
                for (BlockPos foundPos : anchor.loadPositions) {
                    BlockEntity foundEntity = anchor.level.getBlockEntity(foundPos);

                    if (foundEntity instanceof ClaimAugmentBlockEntity be) {
                        anchor.claim.addAugment(foundPos, be.getAugment());
                    } else {
                        GetOffMyLawn.LOGGER.warn(String.format("A Claim Anchor at %s tried to load a child at %s, but none were found!", anchor.worldPosition.toString(), foundPos.toString()));
                    }
                }

                anchor.loadPositions.clear();
                anchor.claim.internal_enableUpdates();
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);
        var positions = view.list(AUGMENT_LIST_KEY, Codec.LONG);
        for (BlockPos loadPosition : this.loadPositions) {
            positions.add(loadPosition.asLong());
        }
        if (this.claim != null) {
            for (Map.Entry<BlockPos, Augment> entry : this.claim.getAugments().entrySet()) {
                positions.add(entry.getKey().asLong());
            }
        }
    }

    @Override
    public void loadAdditional(ValueInput view) {
        var positions = view.listOrEmpty(AUGMENT_LIST_KEY, Codec.LONG);
        positions.forEach(sub -> {
            BlockPos foundPos = BlockPos.of(sub);
            this.loadPositions.add(foundPos);
        });

        super.loadAdditional(view);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState oldState) {
        super.preRemoveSideEffects(pos, oldState);
        if (this.level != null) {
            ClaimUtils.getClaimsAt(level, pos).forEach(claimedArea -> {
                if (ClaimUtils.canDestroyClaimBlock(claimedArea, null, pos)) {
                    claimedArea.getValue().destroy();
                }
            });
        }
    }

    public void addChild(BlockPos pos, Augment augment) {
        this.claim.addAugment(pos, augment);
    }

    public void removeChild(BlockPos pos) {
        this.claim.removeAugment(pos);
    }

    @Nullable
    public Claim getClaim() {
        return this.claim;
    }

    @Nullable
    public ClaimBox getBox() {
        return this.box;
    }

    public void setClaim(Claim claim, ClaimBox box) {
        this.claim = claim;
        this.box = box;
    }

    public boolean hasAugment(Augment augment) {
        return this.claim.hasAugment(augment);
    }

    @Deprecated
    public boolean hasAugment() {
        return this.claim.hasAugment();
    }

    @Deprecated
    public Map<BlockPos, Augment> getAugments() {
        return this.claim.getAugments();
    }

    @Deprecated
    public List<Player> getPreviousTickPlayers() {
        return List.of();
    }

    public void from(ClaimAnchorBlockEntity be) {
        this.claim = be.claim;
    }

    @Override
    public void setRemoved() {
        // Reset players in claim
        super.setRemoved();
    }
}
