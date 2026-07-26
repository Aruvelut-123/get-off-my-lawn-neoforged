package draylar.goml.mixin.augment;

import draylar.goml.api.Claim;
import draylar.goml.api.ClaimUtils;
import draylar.goml.block.entity.ClaimAnchorBlockEntity;
import draylar.goml.registry.GOMLBlocks;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Villager.class)
public abstract class VillagerEntityMixin extends LivingEntity {

    private VillagerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if(damageSource.getEntity() instanceof Monster) {
            boolean b = ClaimUtils.getClaimsAt(level(), blockPosition()).anyMatch(claim -> claim.getValue().hasAugment(GOMLBlocks.VILLAGE_CORE.getFirst()));

            if(b) return true;
        }

        return super.isInvulnerableTo(damageSource);
    }
}
