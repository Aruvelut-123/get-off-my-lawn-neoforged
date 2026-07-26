package draylar.goml.mixin.compat;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Compatibility bridge for SGUI 1.6.1.
 *
 * <p>SGUI is published in Fabric's intermediary namespace. Its packet sender
 * calls are inherited from a parent listener, which prevents Tiny Remapper
 * from resolving {@code method_14364} while producing the NeoForge jar. Keep
 * the intermediary entry point available and delegate it to Mojang's mapped
 * packet sender.</p>
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class SguiPacketSenderMixin {

    public void method_14364(Packet<?> packet) {
        ((ServerGamePacketListenerImpl) (Object) this).send(packet);
    }
}
