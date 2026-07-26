package draylar.goml.block.augment;

import draylar.goml.api.Claim;
import draylar.goml.block.ClaimAugmentBlock;
import draylar.goml.block.SelectiveClaimAugmentBlock;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class LakeSpiritGraceAugmentBlock extends SelectiveClaimAugmentBlock {

    public LakeSpiritGraceAugmentBlock(Properties settings, String texture) {
        super("lake_spirit", settings, texture);
    }

    @Override
    public void playerTick(Claim claim, Player player) {
        if (this.canApply(claim, player)) {
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 5, 0, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 5, 0, true, false));
        }
    }

    @Override
    public boolean ticks() {
        return true;
    }
}
