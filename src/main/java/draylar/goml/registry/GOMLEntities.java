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
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

public class GOMLEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, GetOffMyLawn.MOD_ID);

    public static final BlockEntityType<ClaimAnchorBlockEntity> CLAIM_ANCHOR = register(
            "claim_anchor",
            new BlockEntityType<>(
                    ClaimAnchorBlockEntity::new,
                    Set.copyOf(GOMLBlocks.ANCHORS)));

    public static final BlockEntityType<ClaimAugmentBlockEntity> CLAIM_AUGMENT = register(
            "claim_augment",
            new BlockEntityType<>(
                    ClaimAugmentBlockEntity::new,
                    Set.copyOf(GOMLBlocks.AUGMENTS)));

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> entity) {
        BLOCK_ENTITY_TYPES.register(name, () -> entity);
        return entity;
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }

    private GOMLEntities() {
        // NO-OP
    }
}
