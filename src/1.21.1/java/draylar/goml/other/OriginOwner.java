package draylar.goml.other;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface OriginOwner {
    @Nullable
    BlockPos goml$getOrigin();
    void goml$setOrigin(BlockPos pos);

    default BlockPos goml$getOriginSafe() {
        var origin = this.goml$getOrigin();
        if (origin == null) {
            goml$tryFilling();
            origin = this.goml$getOrigin();
            if (origin == null) {
                return BlockPos.ZERO;
            }
        }
        return origin;
    }

    void goml$tryFilling();
}
