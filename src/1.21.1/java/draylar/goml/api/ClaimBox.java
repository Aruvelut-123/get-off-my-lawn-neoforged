package draylar.goml.api;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.AABB;

public record ClaimBox(com.jamieswhiteshirt.rtree3i.Box rtree3iBox, AABB minecraftBox, BlockPos origin, int radius, int radiusY, boolean noShift) {
    public static final ClaimBox EMPTY = new ClaimBox(BlockPos.ZERO, 0, 0, true);

    public ClaimBox(BlockPos origin, int radius, int radiusY) {
        this(
                origin,
                radius,
                radiusY,
                false
        );
    }

    public ClaimBox(BlockPos origin, int radius, int radiusY, boolean noShift) {
        this(
                noShift ? createBoxNoShift(origin, radius, radiusY) : createBox(origin, radius, radiusY),
                AABB.encapsulatingFullBlocks(origin.offset(-radius, -radiusY, -radius), noShift ? origin.offset(radius, radiusY, radius) : origin.offset(radius + 1, radiusY + 1, radius + 1)),
                origin, radius, radiusY, noShift
        );
    }

    private static com.jamieswhiteshirt.rtree3i.Box createBox(BlockPos origin, int radius, int radiusY) {
        BlockPos lower = origin.offset(-radius, -radiusY, -radius);
        BlockPos upper = origin.offset(radius + 1, radiusY + 1, radius + 1);
        return com.jamieswhiteshirt.rtree3i.Box.create(lower.getX(), lower.getY(), lower.getZ(), upper.getX(), upper.getY(), upper.getZ());
    }

    private static com.jamieswhiteshirt.rtree3i.Box createBoxNoShift(BlockPos origin, int radius, int radiusY) {
        BlockPos lower = origin.offset(-radius, -radiusY, -radius);
        BlockPos upper = origin.offset(radius, radiusY, radius);
        return com.jamieswhiteshirt.rtree3i.Box.create(lower.getX(), lower.getY(), lower.getZ(), upper.getX(), upper.getY(), upper.getZ());
    }

    public com.jamieswhiteshirt.rtree3i.Box toBox() {
        return this.rtree3iBox;
    }

    public BlockPos getOrigin() {
        return this.origin;
    }

    public int getRadius() {
        return this.radius;
    }

    public int getX() {
        return this.radius;
    }

    public int getY() {
        return this.radiusY;
    }

    public int getZ() {
        return this.radius;
    }

    public static ClaimBox readNbt(CompoundTag tag, int i) {
        BlockPos originPos = BlockPos.of(tag.getLong("OriginPos"));
        var radius = tag.getInt("Radius");
        var height = tag.contains("Height") ? tag.getInt("Height") : radius;
        if (radius > 0 && height > 0) {
            return new ClaimBox(originPos, radius, height, tag.getBoolean("NoShift"));
        }
        return EMPTY;
    }

    public Tag toNbt() {
        CompoundTag boxTag = new CompoundTag();

        boxTag.putLong("OriginPos", this.getOrigin().asLong());
        boxTag.putInt("Radius", this.getRadius());
        boxTag.putInt("Height", this.getY());
        boxTag.putBoolean("NoShift", this.noShift());

        return boxTag;
    }
}
