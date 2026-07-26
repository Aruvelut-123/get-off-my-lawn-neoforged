package draylar.goml.api;

import net.minecraft.network.chat.Component;

public enum PermissionReason {
    BLOCK_PROTECTED(Component.translatable("text.goml.block_protected")),
    ENTITY_PROTECTED(Component.translatable("text.goml.entity_protected")),
    AREA_PROTECTED(Component.translatable("text.goml.area_protected"));

    private Component reason;

    PermissionReason(Component reason) {
        this.reason = reason;
    }

    public Component getReason() {
        return reason;
    }
}
