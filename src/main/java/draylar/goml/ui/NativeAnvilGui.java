package draylar.goml.ui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Native anvil menu that forwards vanilla rename input to the owning GUI. */
public class NativeAnvilGui extends NativeGui {
    private String input = "";

    public NativeAnvilGui(ServerPlayer player) {
        super(MenuType.ANVIL, player);
        this.setDefaultInputValue("");
    }

    @Override
    protected NativeMenu createMenu(int containerId, Inventory inventory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean open() {
        if (this.isOpen()) {
            return false;
        }
        return this.player.openMenu(new net.minecraft.world.SimpleMenuProvider((containerId, inventory, ignored) -> {
            NativeAnvilMenu menu = new NativeAnvilMenu(containerId, inventory, this);
            this.attachMenu(menu);
            return menu;
        }, this.getTitle())).isPresent();
    }

    public String getInput() {
        return this.input;
    }

    public void setDefaultInputValue(String value) {
        this.input = value;
        ItemStack stack = new ItemStack(Items.PAPER);
        stack.set(DataComponents.ITEM_NAME, Component.empty());
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(value));
        this.setSlot(0, new GuiElement(stack, GuiElement.EMPTY_CALLBACK));
    }

    public void onInput(String input) {
    }

    private void receiveInput(String input) {
        this.input = input;
        this.onInput(input);
        this.refreshAll();
    }

    private static final class NativeAnvilMenu extends AnvilMenu {
        private final NativeAnvilGui gui;

        private NativeAnvilMenu(int containerId, Inventory inventory, NativeAnvilGui gui) {
            super(containerId, inventory);
            this.gui = gui;
            for (int slot = 0; slot < 3; slot++) {
                GuiElement element = gui.getGuiElement(slot);
                this.getSlot(slot).set(element == null ? ItemStack.EMPTY : element.getItemStack().copy());
            }
        }

        @Override
        protected void createResultInternal() {
        }

        @Override
        public boolean setItemName(String name) {
            this.gui.receiveInput(name);
            return true;
        }

        @Override
        public void clicked(int slotIndex, int button, ContainerInput input, Player player) {
            if (slotIndex >= 0 && slotIndex < 3) {
                GuiElement element = this.gui.getGuiElement(slotIndex);
                if (element != null) {
                    element.click(slotIndex, button, input, this.gui);
                    GuiElement updated = this.gui.getGuiElement(slotIndex);
                    this.getSlot(slotIndex).set(updated == null ? ItemStack.EMPTY : updated.getItemStack().copy());
                    this.broadcastChanges();
                }
            }
        }

        @Override
        public ItemStack quickMoveStack(Player player, int slotIndex) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public void broadcastChanges() {
            this.gui.onTick();
            super.broadcastChanges();
        }

        @Override
        public void removed(Player player) {
            for (int slot = 0; slot < 3; slot++) {
                this.getSlot(slot).set(ItemStack.EMPTY);
            }
            super.removed(player);
            this.gui.removed(this);
        }
    }
}
