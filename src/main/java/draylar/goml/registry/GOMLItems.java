package draylar.goml.registry;

import draylar.goml.GetOffMyLawn;
import draylar.goml.block.ClaimAnchorBlock;
import draylar.goml.item.GogglesItem;
import draylar.goml.item.UpgradeKitItem;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class GOMLItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, GetOffMyLawn.MOD_ID);
    public static List<Item> BASE_ITEMS = new ArrayList<>();

    public static final Item REINFORCED_UPGRADE_KIT = registerUpgradeKit("reinforced_upgrade_kit", GOMLBlocks.MAKESHIFT_CLAIM_ANCHOR.getFirst(), GOMLBlocks.REINFORCED_CLAIM_ANCHOR.getFirst(), Items.IRON_INGOT);
    public static final Item GLISTENING_UPGRADE_KIT = registerUpgradeKit("glistening_upgrade_kit", GOMLBlocks.REINFORCED_CLAIM_ANCHOR.getFirst(), GOMLBlocks.GLISTENING_CLAIM_ANCHOR.getFirst(), Items.GOLD_INGOT);
    public static final Item CRYSTAL_UPGRADE_KIT = registerUpgradeKit("crystal_upgrade_kit", GOMLBlocks.GLISTENING_CLAIM_ANCHOR.getFirst(), GOMLBlocks.CRYSTAL_CLAIM_ANCHOR.getFirst(), Items.DIAMOND);
    public static final Item EMERADIC_UPGRADE_KIT = registerUpgradeKit("emeradic_upgrade_kit", GOMLBlocks.CRYSTAL_CLAIM_ANCHOR.getFirst(), GOMLBlocks.EMERADIC_CLAIM_ANCHOR.getFirst(), Items.EMERALD);
    public static final Item WITHERED_UPGRADE_KIT = registerUpgradeKit("withered_upgrade_kit", GOMLBlocks.EMERADIC_CLAIM_ANCHOR.getFirst(), GOMLBlocks.WITHERED_CLAIM_ANCHOR.getFirst(), Items.NETHER_STAR);

    public static final Item GOGGLES = register("goggles", GogglesItem::new);

    private static UpgradeKitItem registerUpgradeKit(String name, ClaimAnchorBlock from, ClaimAnchorBlock to, Item item) {
        return register(name, (s) -> new UpgradeKitItem(s, from, to, item));
    }

    private static <T extends Item> T register(String name, Function<Item.Properties, T> item) {
        var id = GetOffMyLawn.id(name);
        var value = item.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)));
        BASE_ITEMS.add(value);
        ITEMS.register(name, () -> value);
        return value;
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    private GOMLItems() {
        // NO-OP
    }
}
