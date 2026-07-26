package draylar.goml.registry;

import com.mojang.datafixers.util.Pair;
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

    public static final Pair<ClaimAnchorBlock, Item> MAKESHIFT_CLAIM_ANCHOR = register("makeshift_claim_anchor", () -> GetOffMyLawn.CONFIG.makeshiftRadius, 10, GOMLTextures.MAKESHIFT_CLAIM_ANCHOR);
    public static final Pair<ClaimAnchorBlock, Item> REINFORCED_CLAIM_ANCHOR = register("reinforced_claim_anchor", () -> GetOffMyLawn.CONFIG.reinforcedRadius, 10, GOMLTextures.REINFORCED_CLAIM_ANCHOR);
    public static final Pair<ClaimAnchorBlock, Item> GLISTENING_CLAIM_ANCHOR = register("glistening_claim_anchor", () -> GetOffMyLawn.CONFIG.glisteningRadius, 15, GOMLTextures.GLISTENING_CLAIM_ANCHOR);
    public static final Pair<ClaimAnchorBlock, Item> CRYSTAL_CLAIM_ANCHOR = register("crystal_claim_anchor", () -> GetOffMyLawn.CONFIG.crystalRadius, 20, GOMLTextures.CRYSTAL_CLAIM_ANCHOR);
    public static final Pair<ClaimAnchorBlock, Item> EMERADIC_CLAIM_ANCHOR = register("emeradic_claim_anchor", () -> GetOffMyLawn.CONFIG.emeradicRadius, 20, GOMLTextures.EMERADIC_CLAIM_ANCHOR);
    public static final Pair<ClaimAnchorBlock, Item> WITHERED_CLAIM_ANCHOR = register("withered_claim_anchor", () -> GetOffMyLawn.CONFIG.witheredRadius, 25, GOMLTextures.WITHERED_CLAIM_ANCHOR);
    public static final Pair<ClaimAnchorBlock, Item> ADMIN_CLAIM_ANCHOR = register("admin_claim_anchor", () -> -1, -1, GOMLTextures.ADMIN_CLAIM_ANCHOR);

    public static final Pair<ClaimAugmentBlock, Item> ENDER_BINDING = register("ender_binding", (s) -> new EnderBindingAugmentBlock(s.destroyTime(10).explosionResistance(3600000.0F), GOMLTextures.ENDER_BINDING), 2);
    public static final Pair<ClaimAugmentBlock, Item> LAKE_SPIRIT_GRACE = register("lake_spirit_grace",(s) ->  new LakeSpiritGraceAugmentBlock(s.destroyTime(10).explosionResistance(3600000.0F), GOMLTextures.LAKE_SPIRIT_GRACE), 2);
    public static final Pair<ClaimAugmentBlock, Item> ANGELIC_AURA = register("angelic_aura", (s) -> new AngelicAuraAugmentBlock(s.destroyTime(10).explosionResistance(3600000.0F), GOMLTextures.ANGELIC_AURA), 2);
    public static final Pair<HeavenWingsAugmentBlock, Item> HEAVEN_WINGS = register("heaven_wings", (s) -> new HeavenWingsAugmentBlock(s.destroyTime(10).explosionResistance(3600000.0F), GOMLTextures.HEAVEN_WINGS), 2);
    public static final Pair<ClaimAugmentBlock, Item> VILLAGE_CORE = register("village_core",(s) ->  new ClaimAugmentBlock(s.destroyTime(10).explosionResistance(3600000.0F), GOMLTextures.VILLAGE_CORE), 2);
    public static final Pair<ClaimAugmentBlock, Item> WITHERING_SEAL = register("withering_seal", (s) -> new WitheringSealAugmentBlock(s.destroyTime(10).explosionResistance(3600000.0F), GOMLTextures.WITHERING_SEAL), 2);
    public static final Pair<ClaimAugmentBlock, Item> CHAOS_ZONE = register("chaos_zone", (s) -> new ChaosZoneAugmentBlock(s.destroyTime(10).explosionResistance(3600000.0F), GOMLTextures.CHAOS_ZONE), 2);
    public static final Pair<ClaimAugmentBlock, Item> GREETER = register("greeter", (s) -> new GreeterAugmentBlock(s.destroyTime(10).explosionResistance(3600000.0F), GOMLTextures.GREETER), 2);
    public static final Pair<SelectiveClaimAugmentBlock, Item> PVP_ARENA = register("pvp_arena", (s) -> new SelectiveClaimAugmentBlock("pvp_arena", s.destroyTime(10).explosionResistance(3600000.0F), GOMLTextures.PVP_ARENA), 2);
    public static final Pair<ClaimAugmentBlock, Item> EXPLOSION_CONTROLLER = register("explosion_controller", (s) -> new ExplosionControllerAugmentBlock(s.destroyTime(10).explosionResistance(3600000.0F), GOMLTextures.EXPLOSION_CONTROLLER), 2);

    public static final Pair<ClaimAugmentBlock, Item> FORCE_FIELD = register("force_field", (s) -> new ForceFieldAugmentBlock(s.destroyTime(10).explosionResistance(3600000.0F), GOMLTextures.FORCE_FIELD), 2);

    private static Pair<ClaimAnchorBlock, Item> register(String name, IntSupplier radius, float hardness, String texture) {
        var id = GetOffMyLawn.id(name);
        var claimAnchorBlock = new ClaimAnchorBlock(
                BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, id)).strength(hardness, 3600000.0F),
                radius,
                texture
        );
        BLOCKS.register(name, () -> claimAnchorBlock);

        var registeredItem = new ClaimAnchorBlockItem(claimAnchorBlock,
                new Item.Properties().setId(ResourceKey.create(Registries.ITEM, GetOffMyLawn.id(name))).useBlockDescriptionPrefix(), 0);
        ITEMS.register(name, () -> registeredItem);
        ANCHORS.add(claimAnchorBlock);
        return Pair.of(claimAnchorBlock, registeredItem);
    }

    private static <T extends ClaimAugmentBlock> Pair<T, Item> register(String name, Function<BlockBehaviour.Properties, T> augment) {
        return register(name, augment, 0);
    }


    private static <T extends ClaimAugmentBlock> Pair<T, Item> register(String name, Function<BlockBehaviour.Properties, T> augment, int tooltipLines) {
        var id = GetOffMyLawn.id(name);
        T registered = augment.apply(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, id)));
        BLOCKS.register(name, () -> registered);
        BooleanSupplier check = () -> GetOffMyLawn.CONFIG.enabledAugments.getOrDefault(registered, true);

        registered.setEnabledCheck(check);

        Item registeredItem = new ToggleableBlockItem(registered,
                new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)).useBlockDescriptionPrefix(), tooltipLines, check);
        ITEMS.register(name, () -> registeredItem);
        AUGMENTS.add(registered);

        GOMLAugments.register(id, registered);

        return Pair.of(registered, registeredItem);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }

    private GOMLBlocks() {
        // NO-OP
    }
}
