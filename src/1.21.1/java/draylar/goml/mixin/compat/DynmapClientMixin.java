package draylar.goml.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "org.dynmap.Client", remap = false)
public abstract class DynmapClientMixin {
    @Inject(method = "sanitizeHTML", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private static void goml_modifySanitize(String html, CallbackInfoReturnable<String> cir) {
        // Would be better to modify the sanitizer to include needed style, but OWASP is a bother.
        cir.setReturnValue(html);
    }
}
