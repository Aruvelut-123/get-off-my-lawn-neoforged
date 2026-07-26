package draylar.goml.registry;

import draylar.goml.GetOffMyLawn;
import draylar.goml.cca.ClaimComponent;
import draylar.goml.cca.WorldClaimComponent;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * NeoForge data-attachment replacement for the former Cardinal Components
 * level component. The attachment keeps the existing ValueInput/ValueOutput
 * format so worlds can be migrated without rewriting claim serialization.
 */
public final class GOMLAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, GetOffMyLawn.MOD_ID);

    private static final DeferredHolder<AttachmentType<?>, AttachmentType<WorldClaimComponent>> CLAIMS =
            ATTACHMENTS.register("claims", () -> AttachmentType.serializable(holder ->
                    new WorldClaimComponent((Level) holder)).build());

    public static ClaimComponent get(Level level) {
        return level.getData(CLAIMS);
    }

    public static void register(IEventBus eventBus) {
        ATTACHMENTS.register(eventBus);
    }

    private GOMLAttachments() {
    }
}
