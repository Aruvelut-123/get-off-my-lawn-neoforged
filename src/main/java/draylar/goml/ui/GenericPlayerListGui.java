package draylar.goml.ui;

import com.mojang.authlib.GameProfile;
import draylar.goml.registry.GOMLTextures;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.item.Items;

@ApiStatus.Internal
public abstract class GenericPlayerListGui extends PagedGui {
    public List<UUID> uuids = new ArrayList<>();

    public GenericPlayerListGui(ServerPlayer player, @Nullable Runnable onClose) {
        super(player, onClose);
    }

    @Override
    protected int getPageAmount() {
        return (this.getEntryCount()) / PAGE_SIZE;
    }

    protected int getEntryCount() {
        return this.uuids.size();
    }

    @Override
    protected DisplayElement getElement(int id) {
        return getPlayerElement(id);
    }

    protected DisplayElement getPlayerElement(int id) {
        if (this.uuids.size() > id) {
            return getPlayerElement(this.uuids.get(id));
        }

        return DisplayElement.empty();
    }

    protected DisplayElement getPlayerElement(UUID uuid) {
        var optional = this.player.level().getServer().services().nameToIdCache().get(uuid);
        var exist = optional.isPresent();
        var gameProfile = exist ? optional.get() : null;

        var builder = new GuiElementBuilder(exist ? Items.PLAYER_HEAD : Items.SKELETON_SKULL)
                .setName(Component.literal(exist ? gameProfile.name() : "<" + uuid.toString() + ">")
                        .withStyle(ChatFormatting.WHITE)
                ).hideDefaultTooltip();

        if (exist) {
            builder.setProfile(uuid);
        } else {
            builder.setProfileSkinTexture(GOMLTextures.GUI_QUESTION_MARK);
        }

        this.modifyBuilder(builder, optional, uuid);
        return DisplayElement.of(builder);
    }

    protected void modifyBuilder(GuiElementBuilder builder, Optional<NameAndId> optional, UUID uuid) {
    }


}
