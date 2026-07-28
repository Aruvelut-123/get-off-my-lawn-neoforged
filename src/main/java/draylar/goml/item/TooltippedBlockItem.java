package draylar.goml.item;

import eu.pb4.polymer.core.api.block.PolymerHeadBlock;
import eu.pb4.polymer.core.api.item.PolymerHeadBlockItem;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

public class TooltippedBlockItem extends PolymerHeadBlockItem {

    private final int lines;

    public <T extends Block & PolymerHeadBlock> TooltippedBlockItem(T block, Properties settings, int lines) {
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
