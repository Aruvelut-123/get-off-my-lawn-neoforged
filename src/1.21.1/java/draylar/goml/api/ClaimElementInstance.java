package draylar.goml.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ClaimElementInstance {

    private final BlockState claimElement;
    private final BlockPos pos;

    public ClaimElementInstance(BlockState claimElement, BlockPos pos) {
        this.claimElement = claimElement;
        this.pos = pos;
    }

    public BlockState getClaimElement() {
        return claimElement;
    }

    public BlockPos getPos() {
        return pos;
    }
}
