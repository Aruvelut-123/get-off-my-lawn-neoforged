package draylar.goml.api.group;

import com.mojang.authlib.GameProfile;
import draylar.goml.api.Claim;
import java.util.List;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface PlayerGroup {
    Component selfDisplayName();
    Component fullDisplayName();
    Key getKey();
    ItemStack icon();
    PlayerGroupProvider provider();
    boolean isPartOf(UUID uuid);
    List<Member> getMembers();
    boolean canSave();

    boolean addClaim(Claim claim);
    boolean removeClaim(Claim claim);

    record Member(GameProfile profile, String role) {}

    record Key(String providerId, String groupId) {
        public String compact() {
            return providerId + ":" + groupId;
        }
        public static final Key EMPTY = new Key("", "");

        public static Key of(String value) {
            var split = value.split(":", 2);
            if (split.length < 2) {
                return EMPTY;
            }
            return new Key(split[0], split[1]);
        }

        @Override
        public String toString() {
            return compact();
        }
    }
}
