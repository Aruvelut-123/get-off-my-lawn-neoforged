package draylar.goml.block.augment;

import draylar.goml.api.Claim;
import draylar.goml.block.ClaimAugmentBlock;
import draylar.goml.block.SelectiveClaimAugmentBlock;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class AngelicAuraAugmentBlock extends SelectiveClaimAugmentBlock {

    public AngelicAuraAugmentBlock(Properties settings, String texture) {
        super("angelic_aura", settings, texture);
    }

    @Override
    public void playerTick(Claim claim, Player player) {
        if (player.tickCount % 80 == 0 && canApply(claim, player)) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 0, true, false));
        }
    }

    @Override
    public boolean ticks() {
        return true;
    }
}
