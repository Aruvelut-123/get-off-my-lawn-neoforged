package draylar.goml.other;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * Bridges the vanilla {@code Items} constant grouping change between
 * Minecraft 26.1 and 26.2.
 */
public final class VanillaItems {
    public static Item get(String path) {
        var item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("minecraft", path));
        if (item == null) {
            throw new IllegalStateException("Missing vanilla item minecraft:" + path);
        }
        return item;
    }

    private VanillaItems() {
    }
}
