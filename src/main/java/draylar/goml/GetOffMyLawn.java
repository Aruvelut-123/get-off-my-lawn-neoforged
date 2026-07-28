package draylar.goml;

import draylar.goml.api.Claim;
import draylar.goml.api.GomlProtectionProvider;
import draylar.goml.cca.ClaimComponent;
import draylar.goml.compat.ArgonautsCompat;
import draylar.goml.compat.webmap.WebmapCompat;
import draylar.goml.block.augment.HeavenWingsAugmentBlock;
import draylar.goml.config.GOMLConfig;
import draylar.goml.other.CardboardWarning;
import draylar.goml.other.ClaimCommand;
import draylar.goml.other.PlaceholdersReg;
import draylar.goml.other.PermissionBridge;
import draylar.goml.other.VanillaTeamGroups;
import draylar.goml.registry.GOMLAttachments;
import draylar.goml.registry.GOMLBlocks;
import draylar.goml.registry.GOMLEntities;
import draylar.goml.registry.GOMLItems;
import eu.pb4.common.protection.api.CommonProtection;
import eu.pb4.polymer.core.api.item.PolymerCreativeModeTabUtils;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mod(GetOffMyLawn.MOD_ID)
public final class GetOffMyLawn {
    public static final String MOD_ID = "goml";
    public static final ClaimAccess CLAIM = level -> {
        if (level instanceof Level concreteLevel) {
            return GOMLAttachments.get(concreteLevel);
        }
        throw new IllegalArgumentException("Claims are only available on loaded levels");
    };
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final List<Runnable> NEXT_TICK_TASK = new ArrayList<>();
    public static GOMLConfig CONFIG = new GOMLConfig();

    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final CreativeModeTab GROUP = CreativeModeTab.builder(null, -1)
            .title(Component.translatable("itemGroup.goml.group"))
            .icon(() -> new ItemStack(GOMLBlocks.WITHERED_CLAIM_ANCHOR.getSecond()))
            .displayItems((ctx, output) -> {
                GOMLBlocks.ANCHORS.forEach(output::accept);
                GOMLBlocks.AUGMENTS.forEach(output::accept);
                GOMLItems.BASE_ITEMS.forEach(output::accept);
            })
            .build();

    static {
        CREATIVE_TABS.register("group", () -> GROUP);
    }

    public GetOffMyLawn(IEventBus modEventBus) {
        GOMLBlocks.register(modEventBus);
        GOMLItems.register(modEventBus);
        GOMLEntities.register(modEventBus);
        GOMLAttachments.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        modEventBus.addListener(this::onCommonSetup);

        EventHandlers.register();
        ClaimCommand.register();
        PermissionBridge.register();
        HeavenWingsAugmentBlock.registerEvents();
        PlaceholdersReg.init();
        VanillaTeamGroups.init();

        CommonProtection.register(id("claim_protection"), GomlProtectionProvider.INSTANCE);

        NeoForge.EVENT_BUS.addListener(this::onServerStarting);
        NeoForge.EVENT_BUS.addListener(this::onServerStarted);
        NeoForge.EVENT_BUS.addListener(this::onServerStopped);
        NeoForge.EVENT_BUS.addListener(this::onLevelTick);
        NeoForge.EVENT_BUS.addListener(this::onServerTick);
        NeoForge.EVENT_BUS.addListener(this::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(this::onChunkUnload);

        CardboardWarning.checkAndAnnounce();
        if (ModList.get().isLoaded("argonauts")) {
            ArgonautsCompat.init();
        }
    }

    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(MOD_ID, name);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            GOMLEntities.registerPolymerBlockEntities();
            PolymerCreativeModeTabUtils.registerPolymerCreativeModeTab(id("group"), GROUP);
        });
    }

    private void onServerStarting(ServerStartingEvent event) {
        CardboardWarning.checkAndAnnounce();
        CONFIG = GOMLConfig.loadOrCreateConfig();
    }

    private void onServerStarted(ServerStartedEvent event) {
        WebmapCompat.init(event.getServer());
    }

    private void onServerStopped(ServerStoppedEvent event) {
        NEXT_TICK_TASK.clear();
    }

    private void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level) {
            CLAIM.get(level).getClaims().values().forEach(claim -> claim.tick(level));
        }
    }

    private void onServerTick(ServerTickEvent.Pre event) {
        var tasks = List.copyOf(NEXT_TICK_TASK);
        NEXT_TICK_TASK.clear();
        tasks.forEach(Runnable::run);
    }

    private void onChunkLoad(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            onChunkEvent(level, event.getChunk(), Claim::internal_incrementChunks);
        }
    }

    private void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            onChunkEvent(level, event.getChunk(), Claim::internal_decrementChunks);
        }
    }

    private static void onChunkEvent(ServerLevel world, LevelChunk chunk, Consumer<Claim> chunkHandler) {
        CLAIM.get(world).getClaims().entries().filter(entry -> {
            var box = entry.getKey().toBox();
            var minX = SectionPos.blockToSectionCoord(box.x1());
            var minZ = SectionPos.blockToSectionCoord(box.z1());
            var maxX = SectionPos.blockToSectionCoord(box.x2());
            var maxZ = SectionPos.blockToSectionCoord(box.z2());
            return minX <= chunk.getPos().x()
                    && maxX >= chunk.getPos().x()
                    && minZ <= chunk.getPos().z()
                    && maxZ >= chunk.getPos().z();
        }).forEach(entry -> chunkHandler.accept(entry.getValue()));
    }

    @FunctionalInterface
    public interface ClaimAccess {
        ClaimComponent get(net.minecraft.world.level.LevelReader level);
    }
}
