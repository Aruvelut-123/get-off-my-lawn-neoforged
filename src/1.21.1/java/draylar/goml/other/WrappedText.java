package draylar.goml.other;

import eu.pb4.placeholders.api.TextParserUtils;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public record WrappedText(Component text, TextNode node, String input) {
    public static WrappedText of(String input) {
        var val = TextParserUtils.formatNodes(input);
        return new WrappedText(val.toText(null, true), val, input);
    }

    public static WrappedText ofSafe(String input) {
        var val = TextParserUtils.formatNodesSafe(input);
        return new WrappedText(val.toText(null, true), val, input);
    }

    public MutableComponent mutableText() {
        return this.text.copy();
    }
}
