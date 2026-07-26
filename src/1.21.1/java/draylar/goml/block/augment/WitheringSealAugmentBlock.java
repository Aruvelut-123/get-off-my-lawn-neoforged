package draylar.goml.block.augment;

import draylar.goml.api.Claim;
import draylar.goml.block.ClaimAugmentBlock;
import draylar.goml.block.SelectiveClaimAugmentBlock;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class WitheringSealAugmentBlock extends SelectiveClaimAugmentBlock {

    public WitheringSealAugmentBlock(Properties settings, String texture) {
        super("withering_seal", settings, texture);
    }

    @Override
    public boolean ticks() {
        return true;
    }

    @Override
    public void playerTick(Claim claim, Player player) {
        if (canApply(claim, player)) {
            player.removeEffect(MobEffects.WITHER);
        }
    }
}
