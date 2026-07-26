package draylar.goml.block.entity;

import com.jamieswhiteshirt.rtree3i.Entry;
import com.jamieswhiteshirt.rtree3i.Selection;
import draylar.goml.GetOffMyLawn;
import draylar.goml.api.Augment;
import draylar.goml.api.Claim;
import draylar.goml.api.ClaimBox;
import draylar.goml.api.ClaimUtils;
import draylar.goml.registry.GOMLEntities;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ClaimAugmentBlockEntity extends BlockEntity {

    private static final String PARENT_POSITION_KEY = "ParentPosition";
    private static final String CLAIM_POSITION_KEY = "ClaimPosition";
    private BlockPos parentPosition;
    private BlockPos claimPosition;
    private Augment augment;
    @Nullable
    private Claim claim;

    public ClaimAugmentBlockEntity(BlockPos pos, BlockState state) {
        super(GOMLEntities.CLAIM_AUGMENT.get(), pos, state);
    }

    public static <T extends BlockEntity> void tick(Level world, BlockPos pos, BlockState state, T baseBlockEntity) {
        if (world instanceof ServerLevel && baseBlockEntity instanceof ClaimAugmentBlockEntity entity) {
            // Parent is null and parent position is not null, assume we are just loading the augment from tags.
            if (entity.claim == null) {
                Selection<Entry<ClaimBox, Claim>> claims = null;

                if (entity.claimPosition != null) {
                    claims = ClaimUtils.getClaimsWithOrigin(world, entity.claimPosition);
                } else if (entity.parentPosition != null) {
                    claims = ClaimUtils.getClaimsAt(world, entity.parentPosition);
                }

                if (claims != null) {
                    if (claims.isNotEmpty()) {
                        entity.claim = claims.collect(Collectors.toList()).get(0).getValue();
                        entity.claimPosition = entity.claim.getOrigin();
                        entity.setChanged();
                    } else {
                        GetOffMyLawn.LOGGER.warn(String.format("An augment at %s tried to locate a parent at %s, but it could not be found!", entity.worldPosition.toString(), entity.claimPosition.toString()));
                        world.destroyBlock(pos, true);
                        return;
                    }
                } else {
                    GetOffMyLawn.LOGGER.warn(String.format("An augment at %s has an invalid parent and parent position! Removing now.", entity.worldPosition.toString()));
                    world.destroyBlock(pos, true);
                    return;
                }
            } else {
                if (entity.claim.isDestroyed()) {
                    world.destroyBlock(pos, true);
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registryLookup) {
        if (this.claimPosition != null) {
            tag.putLong(CLAIM_POSITION_KEY, this.claimPosition.asLong());
        }
        if (this.parentPosition != null) {
            tag.putLong(PARENT_POSITION_KEY, this.parentPosition.asLong());
        }

        super.saveAdditional(tag, registryLookup);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registryLookup) {
        if (tag.contains(CLAIM_POSITION_KEY)) {
            this.claimPosition = BlockPos.of(tag.getLong(CLAIM_POSITION_KEY));
        }

        this.parentPosition = BlockPos.of(tag.getLong(PARENT_POSITION_KEY));

        if (this.augment == null) {
            if (getBlockState().getBlock() instanceof Augment) {
                initialize((Augment) getBlockState().getBlock());
            }
        }

        super.loadAdditional(tag, registryLookup);
    }

    public void remove() {
        if (this.claim != null) {
            claim.removeAugment(worldPosition);
        }
    }

    public void setParent(BlockPos pos, Claim claim) {
        this.parentPosition = pos;
        this.claimPosition = claim.getOrigin();
        this.claim = claim;
        claim.addAugment(this.worldPosition, this.getAugment());
    }

    public void initialize(Augment augment) {
        this.augment = augment;
    }

    public Augment getAugment() {
        if (this.augment != null) {
            return augment;
        } else {
            return this.getBlockState().getBlock() instanceof Augment augment ? augment : Augment.noop();
        }
    }

    @Nullable
    public Claim getClaim() {
        return this.claim;
    }
}
