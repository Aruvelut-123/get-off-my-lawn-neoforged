package draylar.goml.mixin.compat;

import it.unimi.dsi.fastutil.Hash;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Keeps Polymer registry bookkeeping independent from Polymer Common's
 * heavyweight static initializer.
 *
 * <p>Polymer initializes this event map while Minecraft's built-in registries
 * are still being constructed. KubeJS transforms {@code Style.withColor}, which
 * is used by Polymer Common's in-game icon initializer, and consequently tries
 * to initialize KubeJS codecs before {@code BuiltInRegistries.ENTITY_TYPE}
 * exists. Supplying the same identity strategy locally avoids that unrelated
 * initializer without changing Polymer registry behavior.</p>
 */
@Pseudo
@Mixin(targets = "eu.pb4.polymer.core.impl.ImplPolymerRegistryEvent", remap = false)
public class PolymerRegistryEventMixin {

    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "FIELD",
                    target = "Leu/pb4/polymer/common/impl/CommonImplUtils;"
                            + "IDENTITY_HASH:Lit/unimi/dsi/fastutil/Hash$Strategy;"
            ),
            require = 0,
            remap = false
    )
    private static Hash.Strategy<Object> goml$avoidEarlyPolymerCommonInitialization() {
        return new Hash.Strategy<>() {
            @Override
            public int hashCode(Object value) {
                return System.identityHashCode(value);
            }

            @Override
            public boolean equals(Object first, Object second) {
                return first == second;
            }
        };
    }
}
