package draylar.goml.mixin;

import draylar.goml.api.ClaimUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * UseBlockCallback doesn't seem to cover buckets well.
 * This mixin serves as an extra protection layer against client desync when using buckets in claims you don't own.
 */
@Mixin(BucketItem.class)
public class BucketItemMixin extends Item {

    @Shadow @Final private Fluid content;

    public BucketItemMixin(Properties settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "use", cancellable = true)
    private void goml_preventBucketUsageInClaims(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (world.isClientSide()) {
            return;
        }

        HitResult hitResult = getPlayerPOVHitResult(world, user, this.content == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
        if (!ClaimUtils.canModify(world, ((BlockHitResult) hitResult).getBlockPos(), user)) {
            user.displayClientMessage(Component.literal("This block is protected by a claim."), true);
            cir.setReturnValue(InteractionResultHolder.fail(user.getItemInHand(hand)));
        }
    }
}
