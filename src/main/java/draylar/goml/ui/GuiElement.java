package draylar.goml.ui;

import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

/** A display stack and its native menu click handler. */
public final class GuiElement {
    public static final ClickCallback EMPTY_CALLBACK = (slot, action, input, gui) -> {
    };

    private final ItemStack itemStack;
    private final ClickCallback callback;

    public GuiElement(ItemStack itemStack, ClickCallback callback) {
        this.itemStack = Objects.requireNonNull(itemStack);
        this.callback = Objects.requireNonNull(callback);
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    void click(int slot, int button, ContainerInput input, NativeGui gui) {
        this.callback.click(slot, ClickAction.from(button, input), input, gui);
    }

    @FunctionalInterface
    public interface ClickCallback {
        void click(int slot, ClickAction action, ContainerInput input, NativeGui gui);
    }

    public static final class ClickAction {
        public final boolean isLeft;
        public final boolean isRight;
        public final boolean shift;

        private ClickAction(boolean isLeft, boolean isRight, boolean shift) {
            this.isLeft = isLeft;
            this.isRight = isRight;
            this.shift = shift;
        }

        private static ClickAction from(int button, ContainerInput input) {
            boolean right = button == 1;
            return new ClickAction(!right, right, input == ContainerInput.QUICK_MOVE);
        }
    }
}
