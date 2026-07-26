package draylar.goml.mixin;

import net.neoforged.fml.ModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class GOMLMixinPlugin implements IMixinConfigPlugin {

    public static final boolean BLAST_LOADED = ModList.get().isLoaded("blast");
    public static final boolean AE2_LOADED = ModList.get().isLoaded("appliedenergistics2");
    public static final boolean BOTANIA_LOADED = ModList.get().isLoaded("botania");

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (targetClassName.startsWith("Blast")) {
            return BLAST_LOADED;
        } else if (targetClassName.startsWith("AE2")) {
            return AE2_LOADED;
        } else if (targetClassName.startsWith("Botania")) {
            return BOTANIA_LOADED;
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
