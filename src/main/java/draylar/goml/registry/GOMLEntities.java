package draylar.goml.registry;

import draylar.goml.GetOffMyLawn;
import draylar.goml.block.entity.ClaimAnchorBlockEntity;
import draylar.goml.block.entity.ClaimAugmentBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

public class GOMLEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, GetOffMyLawn.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ClaimAnchorBlockEntity>> CLAIM_ANCHOR = register(
            "claim_anchor",
            () -> new BlockEntityType<>(
                    ClaimAnchorBlockEntity::new,
                    Set.copyOf(GOMLBlocks.ANCHORS)));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ClaimAugmentBlockEntity>> CLAIM_AUGMENT = register(
            "claim_augment",
            () -> new BlockEntityType<>(
                    ClaimAugmentBlockEntity::new,
                    Set.copyOf(GOMLBlocks.AUGMENTS)));

    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(
            String name,
            java.util.function.Supplier<BlockEntityType<T>> entity
    ) {
        return BLOCK_ENTITY_TYPES.register(name, entity);
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }

    private GOMLEntities() {
        // NO-OP
    }
}
