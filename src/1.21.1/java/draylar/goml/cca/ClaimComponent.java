package draylar.goml.cca;

import com.jamieswhiteshirt.rtree3i.RTreeMap;
import draylar.goml.api.Claim;
import draylar.goml.api.ClaimBox;

public interface ClaimComponent {
    RTreeMap<ClaimBox, Claim> getClaims();
    void add(Claim info);
    void remove(Claim info);
}
