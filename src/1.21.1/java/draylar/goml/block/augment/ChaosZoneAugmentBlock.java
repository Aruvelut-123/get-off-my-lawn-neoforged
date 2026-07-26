package draylar.goml.block.augment;

import draylar.goml.api.Claim;
import draylar.goml.block.ClaimAugmentBlock;
import draylar.goml.block.SelectiveClaimAugmentBlock;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class ChaosZoneAugmentBlock extends SelectiveClaimAugmentBlock {

    public ChaosZoneAugmentBlock(Properties settings, String texture) {
        super("chaos_zone", settings, texture);
    }

    @Override
    public boolean ticks() {
        return true;
    }

    @Override
    public void playerTick(Claim claim, Player player) {
        if (canApply(claim, player)) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 5, 0, true, false));
        }
    }
}
