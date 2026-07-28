package draylar.goml.registry;

import draylar.goml.GetOffMyLawn;
import draylar.goml.block.ClaimAnchorBlock;
import draylar.goml.item.GogglesItem;
import draylar.goml.item.UpgradeKitItem;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class GOMLItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, GetOffMyLawn.MOD_ID);
    public static List<Item> BASE_ITEMS = new ArrayList<>();

    public static final DeferredHolder<Item, UpgradeKitItem> REINFORCED_UPGRADE_KIT = registerUpgradeKit("reinforced_upgrade_kit", GOMLBlocks.MAKESHIFT_CLAIM_ANCHOR, GOMLBlocks.REINFORCED_CLAIM_ANCHOR);
    public static final DeferredHolder<Item, UpgradeKitItem> GLISTENING_UPGRADE_KIT = registerUpgradeKit("glistening_upgrade_kit", GOMLBlocks.REINFORCED_CLAIM_ANCHOR, GOMLBlocks.GLISTENING_CLAIM_ANCHOR);
    public static final DeferredHolder<Item, UpgradeKitItem> CRYSTAL_UPGRADE_KIT = registerUpgradeKit("crystal_upgrade_kit", GOMLBlocks.GLISTENING_CLAIM_ANCHOR, GOMLBlocks.CRYSTAL_CLAIM_ANCHOR);
    public static final DeferredHolder<Item, UpgradeKitItem> EMERADIC_UPGRADE_KIT = registerUpgradeKit("emeradic_upgrade_kit", GOMLBlocks.CRYSTAL_CLAIM_ANCHOR, GOMLBlocks.EMERADIC_CLAIM_ANCHOR);
    public static final DeferredHolder<Item, UpgradeKitItem> WITHERED_UPGRADE_KIT = registerUpgradeKit("withered_upgrade_kit", GOMLBlocks.EMERADIC_CLAIM_ANCHOR, GOMLBlocks.WITHERED_CLAIM_ANCHOR);

    public static final DeferredHolder<Item, GogglesItem> GOGGLES = register("goggles", GogglesItem::new);

    private static DeferredHolder<Item, UpgradeKitItem> registerUpgradeKit(
            String name,
            GOMLBlocks.RegisteredPair<ClaimAnchorBlock> from,
            GOMLBlocks.RegisteredPair<ClaimAnchorBlock> to
    ) {
        return register(name, (s) -> new UpgradeKitItem(s, from.getFirst(), to.getFirst()));
    }

    private static <T extends Item> DeferredHolder<Item, T> register(String name, Function<Item.Properties, T> item) {
        return ITEMS.register(name, () -> {
            var value = item.apply(new Item.Properties());
            BASE_ITEMS.add(value);
            return value;
        });
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    private GOMLItems() {
        // NO-OP
    }
}
