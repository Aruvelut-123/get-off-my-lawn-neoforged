package draylar.goml.api;

import draylar.goml.other.LegacyNbtHelper;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public record DataKey<T>(ResourceLocation key, T defaultValue, Function<T, Tag> serializer, Function<Tag, T> deserializer, @Nullable Supplier<T> defaultSupplier) {
    public DataKey(ResourceLocation key, T defaultValue, Function<T, Tag> serializer, Function<Tag, T> deserializer) {
        this(key, defaultValue, serializer, deserializer, () -> defaultValue);
    }

    private static final Map<ResourceLocation, DataKey<?>> REGISTRY = new HashMap<>();

    public DataKey {
        if (REGISTRY.containsKey(key)) {
            throw new RuntimeException("Duplicate key " + key + "! You can't register the same key twice!");
        }

        REGISTRY.put(key, this);
    }

    public static <T, C extends Collection<T>> DataKey<C> ofCollection(ResourceLocation key, Supplier<C> collectionCreator, Function<T, Tag> serializer, Function<Tag, T> deserializer) {
        return new DataKey<>(key, collectionCreator.get(), (list) -> {
            var nbt = new ListTag();

            for (var i : list) {
                if (i != null) {
                    nbt.add(serializer.apply(i));
                }
            }

            return nbt;
        }, (nbt) -> {
            var list = collectionCreator.get();

            if (nbt instanceof CollectionTag<?> nbtList)
            for (var i : nbtList) {
                if (i != null) {
                    list.add(deserializer.apply(i));
                }
            }

            return list;
        }, collectionCreator);
    }

    public static DataKey<String> ofString(ResourceLocation key, String defaultValue) {
        return new DataKey<>(key, defaultValue, (s) -> StringTag.valueOf(s), (nbt) -> nbt instanceof StringTag nbtString ? nbtString.getAsString() : defaultValue);
    }

    public static DataKey<Boolean> ofBoolean(ResourceLocation key, boolean defaultValue) {
        return new DataKey<>(key, defaultValue, (i) -> ByteTag.valueOf(i), (nbt) -> nbt instanceof NumericTag nbtNumber ? nbtNumber.getAsByte() > 0 : defaultValue);
    }

    public static DataKey<Integer> ofInt(ResourceLocation key, int defaultValue) {
        return new DataKey<>(key, defaultValue, (i) -> IntTag.valueOf(i), (nbt) -> nbt instanceof NumericTag nbtNumber ? nbtNumber.getAsInt() : defaultValue);
    }

    public static DataKey<UUID> ofUuid(ResourceLocation key) {
        return new DataKey<>(key, Util.NIL_UUID, (i) -> LegacyNbtHelper.fromUuid(i), (nbt) -> LegacyNbtHelper.toUuid(nbt));
    }

    public static DataKey<Set<UUID>> ofUuidSet(ResourceLocation key) {
        return ofCollection(key, HashSet::new, (i) -> LegacyNbtHelper.fromUuid(i), (nbt) -> LegacyNbtHelper.toUuid(nbt));
    }

    public static DataKey<Double> ofDouble(ResourceLocation key, double defaultValue) {
        return new DataKey<>(key, defaultValue, (i) -> DoubleTag.valueOf(i), (nbt) -> nbt instanceof NumericTag nbtNumber ? nbtNumber.getAsDouble() : defaultValue);
    }

    public static DataKey<BlockPos> ofPos(ResourceLocation key) {
        return new DataKey<>(key, null, (i) -> LegacyNbtHelper.fromBlockPos(i), (nbt) -> nbt instanceof CompoundTag compound ? LegacyNbtHelper.toBlockPos(compound) : null);
    }

    @Nullable
    public static DataKey<?> getKey(ResourceLocation key) {
        return REGISTRY.get(key);
    }

    public static Collection<ResourceLocation> keys() {
        return REGISTRY.keySet();
    }

    public static <T extends Enum<T>> DataKey<T> ofEnum(ResourceLocation key, Class<T> tClass, T defaultValue) {
        return new DataKey<>(key, defaultValue, (i) -> StringTag.valueOf(i.name()), (nbt) -> {
            var value = nbt instanceof StringTag string ? Enum.valueOf(tClass, string.getAsString()) : null;
            return value != null ? value : defaultValue;
        });
    }
}
