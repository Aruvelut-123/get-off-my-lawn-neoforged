package draylar.goml.other;

import com.mojang.authlib.GameProfile;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

/**
 * Helper methods for handling NBT.
 */
public final class LegacyNbtHelper {
    private LegacyNbtHelper() {
    }

    @Nullable
    public static GameProfile toGameProfile(CompoundTag nbt) {
        UUID uUID = nbt.hasUUID("Id") ? nbt.getUUID("Id") : Util.NIL_UUID;
        String string = nbt.getString("Name");

        try {
            GameProfile gameProfile = new GameProfile(uUID, string);
            if (nbt.contains("Properties", Tag.TAG_COMPOUND)) {
                CompoundTag nbtCompound = nbt.getCompound("Properties");

                for (String string2 : nbtCompound.getAllKeys()) {
                    ListTag nbtList = nbtCompound.getList(string2, Tag.TAG_COMPOUND);

                    for (int i = 0; i < nbtList.size(); ++i) {
                        CompoundTag nbtCompound2 = nbtList.getCompound(i);
                        String string3 = nbtCompound2.getString("Value");
                        if (nbtCompound2.contains("Signature", Tag.TAG_STRING)) {
                            gameProfile.getProperties().put(string2, new com.mojang.authlib.properties.Property(string2, string3, nbtCompound2.getString("Signature")));
                        } else {
                            gameProfile.getProperties().put(string2, new com.mojang.authlib.properties.Property(string2, string3));
                        }
                    }
                }
            }

            return gameProfile;
        } catch (Throwable var11) {
            return null;
        }
    }

    public static CompoundTag writeGameProfile(CompoundTag nbt, GameProfile profile) {
        if (!profile.getName().isEmpty()) {
            nbt.putString("Name", profile.getName());
        }

        if (!profile.getId().equals(Util.NIL_UUID)) {
            nbt.putUUID("Id", profile.getId());
        }

        if (!profile.getProperties().isEmpty()) {
            CompoundTag nbtCompound = new CompoundTag();

            for (String string : profile.getProperties().keySet()) {
                ListTag nbtList = new ListTag();

                for (com.mojang.authlib.properties.Property property : profile.getProperties().get(string)) {
                    CompoundTag nbtCompound2 = new CompoundTag();
                    nbtCompound2.putString("Value", property.value());
                    String string2 = property.signature();
                    if (string2 != null) {
                        nbtCompound2.putString("Signature", string2);
                    }

                    nbtList.add(nbtCompound2);
                }

                nbtCompound.put(string, nbtList);
            }

            nbt.put("Properties", nbtCompound);
        }

        return nbt;
    }

    public static IntArrayTag fromUuid(UUID uuid) {
        return new IntArrayTag(UUIDUtil.uuidToIntArray(uuid));
    }

    public static UUID toUuid(Tag element) {
        if (element.getType() != IntArrayTag.TYPE) {
            throw new IllegalArgumentException(
                    "Expected UUID-Tag to be of type " + IntArrayTag.TYPE.getName() + ", but found " + element.getType().getName() + "."
            );
        } else {
            int[] is = ((IntArrayTag) element).getAsIntArray();
            if (is.length != 4) {
                throw new IllegalArgumentException("Expected UUID-Array to be of length 4, but found " + is.length + ".");
            } else {
                return UUIDUtil.uuidFromIntArray(is);
            }
        }
    }

    public static BlockPos toBlockPos(CompoundTag nbt) {
        return new BlockPos(nbt.getInt("X"), nbt.getInt("Y"), nbt.getInt("Z"));
    }

    public static CompoundTag fromBlockPos(BlockPos pos) {
        CompoundTag nbtCompound = new CompoundTag();
        nbtCompound.putInt("X", pos.getX());
        nbtCompound.putInt("Y", pos.getY());
        nbtCompound.putInt("Z", pos.getZ());
        return nbtCompound;
    }
}
