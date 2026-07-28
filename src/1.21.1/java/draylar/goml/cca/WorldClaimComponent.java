package draylar.goml.cca;

import com.jamieswhiteshirt.rtree3i.ConfigurationBuilder;
import com.jamieswhiteshirt.rtree3i.RTreeMap;
import draylar.goml.api.Claim;
import draylar.goml.api.ClaimBox;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

public class WorldClaimComponent implements ClaimComponent, INBTSerializable<CompoundTag> {

    private RTreeMap<ClaimBox, Claim> claims = RTreeMap.create(new ConfigurationBuilder().star().build(), ClaimBox::toBox);
    private final Level world;

    public WorldClaimComponent(Level world) {
        this.world = world;
    }

    @Override
    public RTreeMap<ClaimBox, Claim> getClaims() {
        return claims;
    }

    @Override
    public void add(Claim info) {
        this.claims = this.claims.put(info.getClaimBox(), info);
    }

    @Override
    public void remove(Claim info) {
        this.claims = this.claims.remove(info.getClaimBox());
    }

    private void readFromNbt(CompoundTag tag, HolderLookup.Provider lookup) {
        this.claims = RTreeMap.create(new ConfigurationBuilder().star().build(), ClaimBox::rtree3iBox);
        var world = this.world.dimension().location();

        var version = tag.getInt("Version");
        ListTag nbtList = tag.getList("Claims", Tag.TAG_COMPOUND);

        if (version == 0) {
            nbtList.forEach(child -> {
                CompoundTag childCompound = (CompoundTag) child;
                ClaimBox box = boxFromTag((CompoundTag) childCompound.get("Box"));
                if (box != null) {
                    Claim claimInfo = Claim.fromNbt(this.world.getServer(), (CompoundTag) childCompound.get("Info"), version);
                    claimInfo.internal_setWorld(world);
                    claimInfo.internal_setClaimBox(box);
                    if (this.world instanceof ServerLevel world1) {
                        claimInfo.internal_updateChunkCount(world1);
                    }
                    claimInfo.internal_enableUpdates();
                    add(claimInfo);
                }
            });
        } else {
            nbtList.forEach(child -> {
                Claim claimInfo = Claim.fromNbt(this.world.getServer(), (CompoundTag) child, version);
                claimInfo.internal_setWorld(world);
                if (this.world instanceof ServerLevel world1) {
                    claimInfo.internal_updateChunkCount(world1);
                }
                claimInfo.internal_enableUpdates();
                add(claimInfo);
            });
        }
    }

    private void writeToNbt(CompoundTag tag, HolderLookup.Provider lookup) {
        ListTag nbtListClaims = new ListTag();
        tag.putInt("Version", 1);

        claims.values().forEach(claim -> nbtListClaims.add(claim.asNbt()));

        tag.put("Claims", nbtListClaims);
    }

    @Nullable
    @Deprecated
    public ClaimBox boxFromTag(CompoundTag tag) {
        return ClaimBox.readNbt(tag, 0);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider lookup) {
        var tag = new CompoundTag();
        writeToNbt(tag, lookup);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider lookup, CompoundTag tag) {
        readFromNbt(tag, lookup);
    }
}
