package draylar.goml.ui;

import com.mojang.authlib.GameProfile;
import draylar.goml.registry.GOMLTextures;
import eu.pb4.sgui.api.GuiHelpers;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import static draylar.goml.ui.PagedGui.playClickSound;

public class NamePlayerSelectorGui extends AnvilInputGui {
    private final Runnable regularClose;
    private final Consumer<GameProfile> playerConsumer;
    private final Predicate<GameProfile> shouldDisplay;
    private boolean ignore = false;
    private GameProfile selectedPlayer;
    private int tick;
    private String currentName;
    private int timer;

    public NamePlayerSelectorGui(ServerPlayer player, Predicate<GameProfile> shouldDisplay, Runnable regularClose, Consumer<GameProfile> playerConsumer) {
        super(player, false);
        this.regularClose = regularClose;
        this.playerConsumer = playerConsumer;
        this.shouldDisplay = shouldDisplay;
        this.updateIconInvalid();
        this.setTitle(Component.translatable("text.goml.gui.player_selector.input.title"));
        this.setSlot(2, new GuiElementBuilder(Items.STRUCTURE_VOID)
                .setName(Component.translatable("text.goml.gui.back").withStyle(ChatFormatting.RED))
                .hideDefaultTooltip().noDefaults()
                .setCallback((x, y, z) -> {
                    playClickSound(this.player);
                    this.close(true);
                }));
        this.open();
    }

    @Override
    public void onClose() {
        if (!ignore) {
            this.regularClose.run();
        }
    }

    @Override
    public void onTick() {
        this.tick++;

        if (this.timer-- == 0 && this.currentName != null) {
            this.updateIcon();
            this.player.server.getProfileCache().getAsync(this.currentName).thenAccept((profile) -> {
                this.player.server.execute(() -> {
                    if (profile.isPresent()) {
                        this.selectedPlayer = profile.get();
                        if (this.shouldDisplay.test(profile.get())) {
                            this.updateIcon();
                        } else {
                            this.updateIconInvalid();
                        }
                    } else {
                        this.updateIconInvalid();
                    }
                    this.updateBack();
                });
            });
        }
        this.updateBack();
    }

    @Override
    public void onInput(String input) {
        super.onInput(input);
        if (input.equals(this.currentName)) {
            return;
        }
        this.selectedPlayer = null;
        if (input.length() < 3 || input.length() > 16) {
            this.updateIconInvalid();
        } else {
            this.timer = 20;
            this.currentName = input;
            this.updateIcon();
        }
        this.updateBack();
    }

    private void updateIconInvalid() {
        this.setSlot(1, new GuiElementBuilder(Items.PLAYER_HEAD)
                .setName(Component.translatable("text.goml.gui.player_selector.input.invalid").withStyle(ChatFormatting.RED))
                .setSkullOwner(GOMLTextures.GUI_QUESTION_MARK)
                .setCallback((x, y, z) -> this.updateBack())
                .hideDefaultTooltip().noDefaults());
    }

    private void updateIcon() {
        var b = new GuiElementBuilder(Items.PLAYER_HEAD)
                .setName(Component.translatable("text.goml.gui.player_selector.input.select_player", this.selectedPlayer != null ? this.selectedPlayer.getName() : this.currentName).withStyle(ChatFormatting.WHITE));

        if (this.selectedPlayer != null) {
            b.setSkullOwner(this.selectedPlayer, null);
        } else {
            b.setSkullOwner(GOMLTextures.GUI_QUESTION_MARK);
        }

        this.setSlot(1, b
                .hideDefaultTooltip().noDefaults()
                .setCallback((x, y, z) -> {
                    if (this.selectedPlayer != null) {
                        playClickSound(this.player);
                        this.ignore = true;
                        this.close(true);
                        this.playerConsumer.accept(this.selectedPlayer);
                    } else {
                        this.updateBack();
                    }
                }));
    }

    private void updateBack() {
        if (this.screenHandler != null) {
            GuiHelpers.sendSlotUpdate(this.player, this.getSyncId(), 2, this.getSlot(2).getItemStackForDisplay(this));
        }
    }
}
