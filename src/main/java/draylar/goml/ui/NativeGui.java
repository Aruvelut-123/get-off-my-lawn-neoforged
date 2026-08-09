package draylar.goml.ui;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Server-driven GUI backed entirely by vanilla/NeoForge menu primitives.
 */
public class NativeGui {
    protected final ServerPlayer player;
    private final MenuType<?> type;
    private final int containerSize;
    private final List<GuiElement> elements;
    private Component title = Component.empty();
    private AbstractContainerMenu wrappedMenu;
    private boolean suppressRemovedCallback;
    private int nextSlot;

    public NativeGui(MenuType<?> type, ServerPlayer player) {
        this.type = type;
        this.player = player;
        this.containerSize = getContainerSize(type);
        this.elements = new ArrayList<>(Collections.nCopies(this.containerSize, null));
    }

    private static int getContainerSize(MenuType<?> type) {
        if (type == MenuType.HOPPER) {
            return 5;
        }
        if (type == MenuType.ANVIL) {
            return 3;
        }
        if (type == MenuType.GENERIC_9x1) return 9;
        if (type == MenuType.GENERIC_9x2) return 18;
        if (type == MenuType.GENERIC_9x3) return 27;
        if (type == MenuType.GENERIC_9x4) return 36;
        if (type == MenuType.GENERIC_9x5) return 45;
        if (type == MenuType.GENERIC_9x6) return 54;
        throw new IllegalArgumentException("Unsupported native menu type: " + type);
    }

    public void setTitle(Component title) {
        this.title = title;
    }

    protected final Component getTitle() {
        return this.title;
    }

    public void setSlot(int index, GuiElementBuilder builder) {
        this.setSlot(index, builder.build());
    }

    public void setSlot(int index, GuiElement element) {
        this.elements.set(index, element);
        this.updateOpenSlot(index, element);
    }

    public void setSlot(int index, Slot slot) {
        this.setSlot(index, new GuiElement(slot.getItem().copy(), GuiElement.EMPTY_CALLBACK));
    }

    public void addSlot(GuiElementBuilder builder) {
        this.addSlot(builder.build());
    }

    public void addSlot(GuiElement element) {
        while (this.nextSlot < this.containerSize && this.elements.get(this.nextSlot) != null) {
            this.nextSlot++;
        }
        if (this.nextSlot >= this.containerSize) {
            throw new IllegalStateException("No free GUI slots remain");
        }
        this.setSlot(this.nextSlot++, element);
    }

    public GuiElement getGuiElement(int index) {
        return this.elements.get(index);
    }

    public int getFirstEmptySlot() {
        for (int slot = 0; slot < this.containerSize; slot++) {
            if (this.elements.get(slot) == null) {
                return slot;
            }
        }
        return -1;
    }

    public boolean open() {
        if (this.isOpen()) {
            return false;
        }
        return this.player.openMenu(new SimpleMenuProvider((containerId, inventory, ignored) -> {
            NativeMenu menu = this.createMenu(containerId, inventory);
            this.wrappedMenu = menu;
            return menu;
        }, this.title)).isPresent();
    }

    protected NativeMenu createMenu(int containerId, Inventory inventory) {
        return new NativeMenu(this.type, containerId, inventory, this, this.containerSize, this.elements);
    }

    protected final void attachMenu(AbstractContainerMenu menu) {
        this.wrappedMenu = menu;
    }

    protected void updateOpenSlot(int index, GuiElement element) {
        if (this.wrappedMenu instanceof NativeMenu menu) {
            menu.setElement(index, element);
        } else if (this.wrappedMenu != null) {
            this.wrappedMenu.getSlot(index).set(element == null ? ItemStack.EMPTY : element.getItemStack().copy());
            this.wrappedMenu.broadcastChanges();
        }
    }

    public boolean isOpen() {
        return this.wrappedMenu != null && this.player.containerMenu == this.wrappedMenu;
    }

    public void close() {
        this.close(false);
    }

    /**
     * Closes the current screen, or invokes the close callback immediately when
     * another screen is expected to replace it.
     */
    public void close(boolean replacementExpected) {
        if (replacementExpected) {
            this.suppressRemovedCallback = true;
            this.onManualClose();
        } else if (this.isOpen()) {
            this.player.closeContainer();
        }
    }

    public void onManualClose() {
    }

    public void onTick() {
    }

    protected final void refreshAll() {
        if (this.wrappedMenu != null) {
            this.wrappedMenu.broadcastFullState();
        }
    }

    protected final void removed(AbstractContainerMenu menu) {
        if (this.wrappedMenu != menu) {
            return;
        }
        this.wrappedMenu = null;
        if (this.suppressRemovedCallback) {
            this.suppressRemovedCallback = false;
        } else {
            this.onManualClose();
        }
    }

    protected static class NativeMenu extends AbstractContainerMenu {
        private final NativeGui gui;
        private final SimpleContainer container;
        private final int guiSize;

        protected NativeMenu(MenuType<?> type, int containerId, Inventory inventory, NativeGui gui, int guiSize, List<GuiElement> elements) {
            super(type, containerId);
            this.gui = gui;
            this.guiSize = guiSize;
            this.container = new SimpleContainer(guiSize);
            for (int slot = 0; slot < guiSize; slot++) {
                GuiElement element = elements.get(slot);
                this.container.setItem(slot, element == null ? ItemStack.EMPTY : element.getItemStack().copy());
                this.addSlot(new DisplaySlot(this.container, slot));
            }
            this.addStandardInventorySlots(inventory, 8, type == MenuType.HOPPER ? 51 : 18 + (guiSize / 9) * 18 + 13);
        }

        private void setElement(int index, GuiElement element) {
            this.container.setItem(index, element == null ? ItemStack.EMPTY : element.getItemStack().copy());
            this.broadcastChanges();
        }

        @Override
        public void clicked(int slotIndex, int button, ContainerInput input, Player player) {
            if (slotIndex >= 0 && slotIndex < this.guiSize) {
                GuiElement element = this.gui.getGuiElement(slotIndex);
                if (element != null) {
                    element.click(slotIndex, button, input, this.gui);
                    this.setElement(slotIndex, this.gui.getGuiElement(slotIndex));
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
            super.removed(player);
            this.gui.removed(this);
        }
    }

    private static final class DisplaySlot extends Slot {
        private DisplaySlot(SimpleContainer container, int slot) {
            super(container, slot, 0, 0);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }
    }
}
