package draylar.goml.cca;

import com.jamieswhiteshirt.rtree3i.ConfigurationBuilder;
import com.jamieswhiteshirt.rtree3i.RTreeMap;
import draylar.goml.api.Claim;
import draylar.goml.api.ClaimBox;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jetbrains.annotations.Nullable;

public class WorldClaimComponent implements ClaimComponent, ValueIOSerializable {

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

    @Override
    public void deserialize(ValueInput view) {
        this.claims = RTreeMap.create(new ConfigurationBuilder().star().build(), ClaimBox::rtree3iBox);
        var world = this.world.dimension().identifier();

        var version = view.getIntOr("Version", 0);
        var nbtList = view.childrenListOrEmpty("Claims");

        if (version == 0) {
            nbtList.forEach(child -> {
                ClaimBox box = boxFromTag(child.childOrEmpty("Box"));
                if (box != null) {
                    Claim claimInfo = Claim.readData(this.world.getServer(), child.childOrEmpty("Info"), version);
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
                Claim claimInfo = Claim.readData(this.world.getServer(), child, version);
                claimInfo.internal_setWorld(world);
                if (this.world instanceof ServerLevel world1) {
                    claimInfo.internal_updateChunkCount(world1);
                }
                claimInfo.internal_enableUpdates();
                add(claimInfo);
            });
        }
    }

    @Override
    public void serialize(ValueOutput view) {
        var nbtListClaims = view.childrenList("Claims");
        view.putInt("Version", 1);
        claims.values().forEach(claim -> claim.writeData(nbtListClaims.addChild()));
    }

    @Nullable
    @Deprecated
    public ClaimBox boxFromTag(ValueInput tag) {
        return ClaimBox.readData(tag, 0);
    }
}
