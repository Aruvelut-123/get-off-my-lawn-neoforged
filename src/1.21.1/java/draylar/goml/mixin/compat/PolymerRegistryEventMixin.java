package draylar.goml.mixin.compat;

import it.unimi.dsi.fastutil.Hash;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Avoids initializing Polymer Common's colored in-game icon while Minecraft's
 * built-in registries are still being constructed.
 *
 * <p>KubeJS transforms {@code Style.withColor}; triggering that path during
 * Polymer registry bookkeeping initializes KubeJS codecs before
 * {@code BuiltInRegistries.ENTITY_TYPE} exists. This local strategy is
 * behaviorally identical to Polymer's shared identity strategy and has no
 * Minecraft bootstrap dependencies.</p>
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
