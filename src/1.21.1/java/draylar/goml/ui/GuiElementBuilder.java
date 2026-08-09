package draylar.goml.ui;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class GuiElementBuilder {
    private ItemStack stack;
    private final List<Component> lore = new ArrayList<>();
    private GuiElement.ClickCallback callback = GuiElement.EMPTY_CALLBACK;

    public GuiElementBuilder() {
        this(Items.STONE);
    }

    public GuiElementBuilder(Item item) {
        this.stack = new ItemStack(item);
    }

    public GuiElementBuilder(ItemStack stack) {
        this.stack = stack.copy();
        ItemLore existingLore = this.stack.get(DataComponents.LORE);
        if (existingLore != null) {
            this.lore.addAll(existingLore.lines());
        }
    }

    public static GuiElementBuilder from(ItemStack stack) {
        return new GuiElementBuilder(stack);
    }

    public GuiElementBuilder setItem(Item item) {
        ItemStack replacement = new ItemStack(item, this.stack.getCount());
        replacement.applyComponents(this.stack.getComponents());
        this.stack = replacement;
        return this;
    }

    public GuiElementBuilder setName(Component name) {
        this.stack.set(DataComponents.CUSTOM_NAME, name);
        return this;
    }

    public GuiElementBuilder setLore(List<Component> lines) {
        this.lore.clear();
        this.lore.addAll(lines);
        return this;
    }

    public GuiElementBuilder addLoreLine(Component line) {
        this.lore.add(line);
        return this;
    }

    public GuiElementBuilder hideDefaultTooltip() {
        return this;
    }

    public GuiElementBuilder noDefaults() {
        return this;
    }

    public GuiElementBuilder setProfile(UUID uuid) {
        this.stack.set(DataComponents.PROFILE, new ResolvableProfile(Optional.empty(), Optional.of(uuid), new PropertyMap()));
        return this;
    }

    public GuiElementBuilder setSkullOwner(GameProfile profile, Object ignored) {
        this.stack.set(DataComponents.PROFILE, new ResolvableProfile(profile));
        return this;
    }

    public GuiElementBuilder setSkullOwner(String texture) {
        return this.setProfileSkinTexture(texture);
    }

    public GuiElementBuilder setProfileSkinTexture(String texture) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", texture));
        this.stack.set(DataComponents.PROFILE, new ResolvableProfile(profile));
        return this;
    }

    public GuiElementBuilder setCallback(Runnable callback) {
        this.callback = (slot, action, input, gui) -> callback.run();
        return this;
    }

    public GuiElementBuilder setCallback(GuiElement.ClickCallback callback) {
        this.callback = callback;
        return this;
    }

    public GuiElementBuilder setCallback(LegacyClickCallback callback) {
        this.callback = (slot, action, input, gui) -> callback.click(slot, action, input);
        return this;
    }

    public GuiElement build() {
        ItemStack result = this.stack.copy();
        if (!this.lore.isEmpty()) {
            result.set(DataComponents.LORE, new ItemLore(List.copyOf(this.lore)));
        }
        return new GuiElement(result, this.callback);
    }

    @FunctionalInterface
    public interface LegacyClickCallback {
        void click(int slot, GuiElement.ClickAction action, net.minecraft.world.inventory.ClickType input);
    }
}
