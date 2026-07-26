package draylar.goml.registry;

import draylar.goml.GetOffMyLawn;
import draylar.goml.block.ClaimAnchorBlock;
import draylar.goml.block.ClaimAugmentBlock;
import draylar.goml.block.SelectiveClaimAugmentBlock;
import draylar.goml.block.augment.*;
import draylar.goml.item.ClaimAnchorBlockItem;
import draylar.goml.item.ToggleableBlockItem;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class GOMLBlocks {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, GetOffMyLawn.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, GetOffMyLawn.MOD_ID);

    public static final List<ClaimAnchorBlock> ANCHORS = new ArrayList<>();
    public static final List<ClaimAugmentBlock> AUGMENTS = new ArrayList<>();

    public static final RegisteredPair<ClaimAnchorBlock> MAKESHIFT_CLAIM_ANCHOR = register("makeshift_claim_anchor", () -> GetOffMyLawn.CONFIG.makeshiftRadius, 10, GOMLTextures.MAKESHIFT_CLAIM_ANCHOR);
    public static final RegisteredPair<ClaimAnchorBlock> REINFORCED_CLAIM_ANCHOR = register("reinforced_claim_anchor", () -> GetOffMyLawn.CONFIG.reinforcedRadius, 10, GOMLTextures.REINFORCED_CLAIM_ANCHOR);
    public static final RegisteredPair<ClaimAnchorBlock> GLISTENING_CLAIM_ANCHOR = register("glistening_claim_anchor", () -> GetOffMyLawn.CONFIG.glisteningRadius, 15, GOMLTextures.GLISTENING_CLAIM_ANCHOR);
    public static final RegisteredPair<ClaimAnchorBlock> CRYSTAL_CLAIM_ANCHOR = register("crystal_claim_anchor", () -> GetOffMyLawn.CONFIG.crystalRadius, 20, GOMLTextures.CRYSTAL_CLAIM_ANCHOR);
    public static final RegisteredPair<ClaimAnchorBlock> EMERADIC_CLAIM_ANCHOR = register("emeradic_claim_anchor", () -> GetOffMyLawn.CONFIG.emeradicRadius, 20, GOMLTextures.EMERADIC_CLAIM_ANCHOR);
    public static final RegisteredPair<ClaimAnchorBlock> WITHERED_CLAIM_ANCHOR = register("withered_claim_anchor", () -> GetOffMyLawn.CONFIG.witheredRadius, 25, GOMLTextures.WITHERED_CLAIM_ANCHOR);
    public static final RegisteredPair<ClaimAnchorBlock> ADMIN_CLAIM_ANCHOR = register("admin_claim_anchor", () -> -1, -1, GOMLTextures.ADMIN_CLAIM_ANCHOR);

    public static final RegisteredPair<ClaimAugmentBlock> ENDER_BINDING = register("ender_binding", (s) -> new EnderBindingAugmentBlock(s.destroyTime(10).explosionResistance(3600000.0F), GOMLTextures.ENDER_BINDING), 2);
    public static final RegisteredPair<ClaimAugmentBlock> LAKE_SPIRIT_GRACE = register("lake_spirit_grace",(s) ->  new LakeSpiritGraceAugmentBlock(s.destroyTime(10).explosionResistance(3600000.0F), GOMLTextures.LAKE_SPIRIT_GRACE), 2);
    public static final RegisteredPair<ClaimAugmentBlock> ANGELIC_AURA = register("angelic_aura", (s) -> new AngelicAuraAugmentBlock(s.destroyTime(10).explosionResistance(3600000.0F), GOMLTextures.ANGELIC_AURA), 2);
    public static final RegisteredPair<HeavenWingsAugmentBlock> HEAVEN_WINGS = register("heaven_wings", (s) -> new HeavenWingsAugmentBlock(s.destroyTime(10).explosionResistance(3600000.0F), GOMLTextures.HEAVEN_WINGS), 2);
    public static final RegisteredPair<ClaimAugmentBlock> VILLAGE_CORE = register("village_core",(s) ->  new ClaimAugmentBlock(s.destroyTime(10).explosionResistance(3600000.0F), GOMLTextures.VILLAGE_CORE), 2);
    public static final RegisteredPair<ClaimAugmentBlock> WITHERING_SEAL = register("withering_seal", (s) -> new WitheringSealAugmentBlock(s.destroyTime(10).explosionResistance(3600000.0F), GOMLTextures.WITHERING_SEAL), 2);
    public static final RegisteredPair<ClaimAugmentBlock> CHAOS_ZONE = register("chaos_zone", (s) -> new ChaosZoneAugmentBlock(s.destroyTime(10).explosionResistance(3600000.0F), GOMLTextures.CHAOS_ZONE), 2);
    public static final RegisteredPair<ClaimAugmentBlock> GREETER = register("greeter", (s) -> new GreeterAugmentBlock(s.destroyTime(10).explosionResistance(3600000.0F), GOMLTextures.GREETER), 2);
    public static final RegisteredPair<SelectiveClaimAugmentBlock> PVP_ARENA = register("pvp_arena", (s) -> new SelectiveClaimAugmentBlock("pvp_arena", s.destroyTime(10).explosionResistance(3600000.0F), GOMLTextures.PVP_ARENA), 2);
    public static final RegisteredPair<ClaimAugmentBlock> EXPLOSION_CONTROLLER = register("explosion_controller", (s) -> new ExplosionControllerAugmentBlock(s.destroyTime(10).explosionResistance(3600000.0F), GOMLTextures.EXPLOSION_CONTROLLER), 2);

    public static final RegisteredPair<ClaimAugmentBlock> FORCE_FIELD = register("force_field", (s) -> new ForceFieldAugmentBlock(s.destroyTime(10).explosionResistance(3600000.0F), GOMLTextures.FORCE_FIELD), 2);

    private static RegisteredPair<ClaimAnchorBlock> register(String name, IntSupplier radius, float hardness, String texture) {
        var id = GetOffMyLawn.id(name);
        var block = BLOCKS.register(name, () -> {
            var value = new ClaimAnchorBlock(
                    BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, id)).strength(hardness, 3600000.0F),
                    radius,
                    texture
            );
            ANCHORS.add(value);
            return value;
        });
        var item = ITEMS.register(name, () -> new ClaimAnchorBlockItem(block.get(),
                new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)).useBlockDescriptionPrefix(), 0));
        return new RegisteredPair<>(block, item);
    }

    private static <T extends ClaimAugmentBlock> RegisteredPair<T> register(String name, Function<BlockBehaviour.Properties, T> augment) {
        return register(name, augment, 0);
    }


    private static <T extends ClaimAugmentBlock> RegisteredPair<T> register(String name, Function<BlockBehaviour.Properties, T> augment, int tooltipLines) {
        var id = GetOffMyLawn.id(name);
        var block = BLOCKS.register(name, () -> {
            T value = augment.apply(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, id)));
            value.setEnabledCheck(() -> GetOffMyLawn.CONFIG.enabledAugments.getOrDefault(value, true));
            AUGMENTS.add(value);
            GOMLAugments.register(id, value);
            return value;
        });
        var item = ITEMS.register(name, () -> {
            T value = block.get();
            BooleanSupplier check = () -> GetOffMyLawn.CONFIG.enabledAugments.getOrDefault(value, true);
            return new ToggleableBlockItem(value,
                    new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)).useBlockDescriptionPrefix(),
                    tooltipLines,
                    check);
        });
        return new RegisteredPair<>(block, item);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }

    private GOMLBlocks() {
        // NO-OP
    }

    public record RegisteredPair<B extends Block>(Supplier<B> block, Supplier<? extends Item> item) {
        public B getFirst() {
            return block.get();
        }

        public Item getSecond() {
            return item.get();
        }
    }
}
