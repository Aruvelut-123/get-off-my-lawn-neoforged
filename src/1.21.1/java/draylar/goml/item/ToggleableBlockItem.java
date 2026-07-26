package draylar.goml.item;

import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ToggleableBlockItem extends TooltippedBlockItem {
    private final BooleanSupplier isEnabled;

    public ToggleableBlockItem(Block block, Properties settings, int lines, BooleanSupplier isEnabled) {
        super(block, settings, lines);
        this.isEnabled = isEnabled;
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        return isEnabled.getAsBoolean()
                ? super.canPlace(context, state)
                : false;
    }

    public boolean isEnabled() {
        return this.isEnabled.getAsBoolean();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        if (isEnabled.getAsBoolean()) {
            super.appendHoverText(stack, context, tooltip, type);
        } else {
            tooltip.add(Component.translatable("text.goml.disabled_augment").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        }
    }
}
