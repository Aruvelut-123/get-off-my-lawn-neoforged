package draylar.goml.item;

import eu.pb4.polymer.core.api.block.PolymerHeadBlock;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ToggleableBlockItem extends TooltippedBlockItem {
    private final BooleanSupplier isEnabled;

    public <T extends Block & PolymerHeadBlock> ToggleableBlockItem(T block, Properties settings, int lines, BooleanSupplier isEnabled) {
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
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        if (isEnabled.getAsBoolean()) {
            super.appendHoverText(stack, context, displayComponent, textConsumer, type);
        } else {
            textConsumer.accept(Component.translatable(String.format("text.goml.disabled_augment")).withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        }
    }
}
