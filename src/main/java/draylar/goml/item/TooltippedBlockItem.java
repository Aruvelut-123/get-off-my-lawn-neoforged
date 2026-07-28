package draylar.goml.item;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

public class TooltippedBlockItem extends BlockItem {

    private final int lines;

    public TooltippedBlockItem(Block block, Properties settings, int lines) {
        super(block, settings);
        this.lines = lines;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        this.addLines(textConsumer);
    }

    public void addLines(Consumer<Component> textConsumer) {
        for (int i = 1; i <= lines; i++) {
            textConsumer.accept(Component.translatable(String.format("%s.description.%d", getDescriptionId(), i)).withStyle(ChatFormatting.GRAY));
        }
    }
}
