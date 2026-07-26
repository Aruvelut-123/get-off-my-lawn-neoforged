package draylar.goml.registry;

import static draylar.goml.GetOffMyLawn.id;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public final class GOMLTags {
    public static final TagKey<Block> ALLOWED_INTERACTIONS_BLOCKS = TagKey.create(Registries.BLOCK, id("allowed_interactions"));
    public static final TagKey<EntityType<?>> ALLOWED_INTERACTIONS_ENTITY = TagKey.create(Registries.ENTITY_TYPE, id("allowed_interactions"));

    private GOMLTags(){}
}
