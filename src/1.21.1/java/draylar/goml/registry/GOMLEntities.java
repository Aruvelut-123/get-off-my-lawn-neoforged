package draylar.goml.registry;

import draylar.goml.GetOffMyLawn;
import draylar.goml.block.entity.ClaimAnchorBlockEntity;
import draylar.goml.block.entity.ClaimAugmentBlockEntity;
import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Set;

public class GOMLEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, GetOffMyLawn.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ClaimAnchorBlockEntity>> CLAIM_ANCHOR = register(
            "claim_anchor",
            () -> new BlockEntityType<>(
                    ClaimAnchorBlockEntity::new,
                    Set.copyOf(GOMLBlocks.ANCHORS),
                    null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ClaimAugmentBlockEntity>> CLAIM_AUGMENT = register(
            "claim_augment",
            () -> new BlockEntityType<>(
                    ClaimAugmentBlockEntity::new,
                    Set.copyOf(GOMLBlocks.AUGMENTS),
                    null));

    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(
            String name,
            java.util.function.Supplier<BlockEntityType<T>> entity
    ) {
        return BLOCK_ENTITY_TYPES.register(name, entity);
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
        eventBus.addListener(GOMLEntities::onRegister);
    }

    private static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.BLOCK_ENTITY_TYPE)) {
            PolymerBlockUtils.registerBlockEntity(CLAIM_ANCHOR.get(), CLAIM_AUGMENT.get());
        }
    }

    private GOMLEntities() {
        // NO-OP
    }
}
