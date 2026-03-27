package dev.satyrn.lepidoptera.neoforge;

import com.mojang.serialization.MapCodec;
import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.network.PacketChannels;
import dev.satyrn.lepidoptera.api.network.PacketReadyCallback;
import dev.satyrn.lepidoptera.api.network.ServerPlayContext;
import dev.satyrn.lepidoptera.item.LepidopteraItems;
import dev.satyrn.lepidoptera.neoforge.condition.AlchemicalAlembicRecipesCondition;
import dev.satyrn.lepidoptera.neoforge.data.provider.client.lang.LepidopteraEnUSLanguageProvider;
import dev.satyrn.lepidoptera.neoforge.data.provider.client.lang.LepidopteraFrCALanguageProvider;
import dev.satyrn.lepidoptera.neoforge.data.provider.client.lang.LepidopteraFrFRLanguageProvider;
import dev.satyrn.lepidoptera.neoforge.data.provider.client.lang.LepidopteraTokLanguageProvider;
import dev.satyrn.lepidoptera.neoforge.data.provider.server.recipe.LepidopteraRecipeProvider;
import dev.satyrn.lepidoptera.neoforge.data.provider.server.tags.LepidopteraEntityTypeTags;
import dev.satyrn.lepidoptera.neoforge.data.provider.server.tags.LepidopteraItemTags;
import dev.satyrn.lepidoptera.neoforge.network.NeoForgePacketChannelsImpl;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

import static dev.satyrn.lepidoptera.LepidopteraAPI.MOD_ID;
import static dev.satyrn.lepidoptera.LepidopteraAPI.info;

@Mod(MOD_ID)
@ApiStatus.Internal
public class LepidopteraNeo {

    private static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS = DeferredRegister.create(
            NeoForgeRegistries.Keys.CONDITION_CODECS, MOD_ID);

    static {
        CONDITION_CODECS.register("alchemical_alembic_recipes", () -> AlchemicalAlembicRecipesCondition.CODEC);
    }

    public LepidopteraNeo(final IEventBus modEventBus) {
        info("Initializing Lepidoptera API for NeoForge");

        CONDITION_CODECS.register(modEventBus);

        // Inject NeoForge networking impl before init() so dependent mods' @Mod constructors
        // that run after this one will have their channel registrations queued.
        // The impl self-subscribes to RegisterPayloadHandlersEvent on the mod event bus.
        NeoForgePacketChannelsImpl neoForgeImpl = new NeoForgePacketChannelsImpl();
        neoForgeImpl.register(modEventBus);
        PacketChannels.setImpl(neoForgeImpl);

        LepidopteraAPI.INSTANCE.preInit();
        LepidopteraAPI.INSTANCE.init();

        modEventBus.addListener(this::onGatherData);

        // Alchemical Alembic burn time - fires on the game event bus when fuel is queried.
        NeoForge.EVENT_BUS.addListener(LepidopteraNeo::onFurnaceFuelBurnTime);

        NeoForge.EVENT_BUS.addListener((ServerStartedEvent e) -> LepidopteraAPI.INSTANCE.serverStarted(e.getServer()));
        NeoForge.EVENT_BUS.addListener((ServerStoppingEvent e) -> LepidopteraAPI.INSTANCE.serverStopped());
        NeoForge.EVENT_BUS.addListener((TagsUpdatedEvent e) -> {
            if (e.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) {
                LepidopteraAPI.INSTANCE.onTagsLoaded(e.getRegistryAccess());
            }
        });

        // Server ready callbacks - PlayerLoggedInEvent fires on the game (NeoForge) event bus.
        NeoForge.EVENT_BUS.addListener(LepidopteraNeo::onPlayerLoggedIn);

        info("Lepidoptera API for NeoForge initialized.");
    }

    private void onGatherData(GatherDataEvent event) {
        event.createProvider(LepidopteraRecipeProvider::new);
        event.createProvider((arg, completableFuture) -> new LepidopteraEnUSLanguageProvider(arg));
        event.createProvider((arg, completableFuture) -> new LepidopteraFrFRLanguageProvider(arg));
        event.createProvider((arg, completableFuture) -> new LepidopteraFrCALanguageProvider(arg));
        event.createProvider((arg, completableFuture) -> new LepidopteraEnUSLanguageProvider(arg, "en_ca"));
        event.createProvider((arg, completableFuture) -> new LepidopteraEnUSLanguageProvider(arg, "en_gb"));
        event.createProvider((arg, completableFuture) -> new LepidopteraTokLanguageProvider(arg));
        event.createProvider((arg, completableFuture) -> new LepidopteraEntityTypeTags(arg, completableFuture,
                event.getExistingFileHelper()));
        event.createProvider((arg, completableFuture) -> new LepidopteraItemTags(arg, completableFuture,
                event.getExistingFileHelper()));
    }


    private static void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        if (event.getItemStack().getItem() == LepidopteraItems.ALCHEMICAL_ALEMBIC.get()) {
            event.setBurnTime(200);
        }
    }

    private static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (PacketChannels.SERVER_READY_CALLBACKS.isEmpty()) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        NeoForgePacketChannelsImpl.NeoForgeServerPlayContext ctx = new NeoForgePacketChannelsImpl.NeoForgeServerPlayContext(
                serverPlayer);
        boolean hasAnyChannel = PacketChannels.CLIENT_CHANNELS.stream().anyMatch(ctx::canSend);
        if (!hasAnyChannel) {
            return;
        }
        for (PacketReadyCallback<ServerPlayContext> callback : PacketChannels.SERVER_READY_CALLBACKS) {
            callback.onReady(ctx);
        }
    }
}