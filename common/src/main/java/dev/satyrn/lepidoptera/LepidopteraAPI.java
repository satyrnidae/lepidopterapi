package dev.satyrn.lepidoptera;

import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.satyrn.lepidoptera.api.LepidopteraMod;
import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.compatibility.Compatibility;
import dev.satyrn.lepidoptera.api.compatibility.CompatibilityProviders;
import dev.satyrn.lepidoptera.api.config.serializers.CommentedYamlConfigSerializer;
import dev.satyrn.lepidoptera.api.config.sync.SyncedConfig;
import dev.satyrn.lepidoptera.api.entity.ApiEntityTags;
import dev.satyrn.lepidoptera.api.entity.HungryEntityRegistry;
import dev.satyrn.lepidoptera.api.item.ApiItemTags;
import dev.satyrn.lepidoptera.api.item.EquipmentRegistry;
import dev.satyrn.lepidoptera.api.lang.T9n;
import dev.satyrn.lepidoptera.config.LepidopteraConfig;
import dev.satyrn.lepidoptera.item.LepidopteraItems;
import dev.satyrn.lepidoptera.item.crafting.LepidopteraRecipeSerializers;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.util.Arrays;

import static net.minecraft.world.level.GameRules.BooleanValue;
import static net.minecraft.world.level.GameRules.Key;

@ApiStatus.Internal
@ModMeta(value = LepidopteraAPI.MOD_ID, name = "Lepidoptera API", semVer = "1.0.0-SNAPSHOT+1.21.1")
@CompatibilityProviders(
        client = { "dev.satyrn.lepidoptera.compat.accessories.AccessoriesClientCompatProvider" }
)
public class LepidopteraAPI implements LepidopteraMod {
    public static final String MOD_ID = "lepidoptera_api";
    /**
     * Condition ID for the alchemical alembic recipe load condition (all platforms).
     */
    public static final ResourceLocation ALCHEMICAL_ALEMBIC_RECIPES_CONDITION = ResourceLocation.fromNamespaceAndPath(
            MOD_ID, "alchemical_alembic_recipes");
    private static final Logger LOGGER = LogManager.getLogger();
    public static LepidopteraMod INSTANCE = new LepidopteraAPI();
    public static @Nullable Key<BooleanValue> RULE_ENTITY_STARVATION;
    public static @Nullable MinecraftServer currentServer = null;

    /**
     * Safe default — initialized in {@link #preInit()} after AutoConfig registers the config
     * class; replaced with a fully registered instance in {@link #init()}.
     */
    public static @Nullable SyncedConfig<LepidopteraConfig> SYNCED_CONFIG = null;

    @Contract(pure = true)
    private LepidopteraAPI() {
    }

    /**
     * Returns {@code true} if alchemical alembic breakable recipes should be loaded.
     */
    public static boolean alchemicalAlembicRecipesEnabled() {
        return SYNCED_CONFIG != null && SYNCED_CONFIG.get().enableAlchemicalAlembicRecipes;
    }

    public static void info(String message, Object... params) {
        LOGGER.info(message, params);
    }

    public static void debug(String message, Object... params) {
        LOGGER.debug(message, params);
    }

    public static void debug(String message, Throwable throwable) {
        LOGGER.debug("{}: {}\n\t{}", message, throwable.getMessage(), String.join("\n\t",
                Arrays.stream(throwable.getStackTrace()).map(StackTraceElement::toString).toList()));
    }

    public static void warn(String message, Object... params) {
        LOGGER.warn(message, params);
    }

    public static void error(String message, Object... params) {
        LOGGER.error(message, params);
    }

    public static void error(String message, Throwable e) {
        LOGGER.error(message, e);
    }

    @Override
    public void preInit() {
        debug("PRE-INIT: {} entered the pre-initialization phase.", ModHelper.friendlyName());

        // Register lepidoptera's own compat providers early so they can participate in
        // config registration and sync setup via onConfigRegister / onConfigSyncSetup.
        Compatibility.registerAll(LepidopteraAPI.class);

        AutoConfig.register(LepidopteraConfig.class, (def, cls) -> new CommentedYamlConfigSerializer<>(def, cls,
                CommentedYamlConfigSerializer.DEFAULT_LINE_LENGTH));
        LepidopteraItems.register();
        LepidopteraRecipeSerializers.register();

        // Safe default for any code that reads SYNCED_CONFIG between preInit() and init()
        // (e.g. data generation). Replaced with a fully registered instance in init().
        SYNCED_CONFIG = SyncedConfig.unregistered(AutoConfig.getConfigHolder(LepidopteraConfig.class),
                LepidopteraConfig.Codec.INSTANCE);

        debug("PRE-INIT: {} completed the pre-initialization phase.", ModHelper.friendlyName());
    }

    @Override
    public void init() {
        debug("INIT: {} entered the initialization phase.", ModHelper.friendlyName());

        RULE_ENTITY_STARVATION = GameRules.register("doAnimalStarvation", GameRules.Category.MOBS,
                GameRules.BooleanValue.create(false));

        final var holder = AutoConfig.getConfigHolder(LepidopteraConfig.class);

        SYNCED_CONFIG = SyncedConfig.builder(MOD_ID, LepidopteraConfig.Codec.INSTANCE, holder)
                .networkVersion(ModHelper.netVersion(), T9n.netMsg(ModHelper.metadata(), "versionMismatch"))
                .watchFile(Platform.getConfigFolder().resolve("lepidoptera/config.yaml"))
                .register()
                .onApply(cfg -> {
                    EquipmentRegistry.updateEquipment(EquipmentSlot.HEAD, LepidopteraItems.ALCHEMICAL_ALEMBIC.get(),
                            cfg.alchemicalAlembicCanShiftClick);
                    EquipmentRegistry.updateEquipment(EquipmentSlot.HEAD, LepidopteraItems.DEPLETED_ALEMBIC.get(),
                            cfg.alchemicalAlembicCanShiftClick);
                })
                .onClear(() -> {
                    final LepidopteraConfig localCfg = holder.getConfig();
                    EquipmentRegistry.updateEquipment(EquipmentSlot.HEAD, LepidopteraItems.ALCHEMICAL_ALEMBIC.get(),
                            localCfg.alchemicalAlembicCanShiftClick);
                    EquipmentRegistry.updateEquipment(EquipmentSlot.HEAD, LepidopteraItems.DEPLETED_ALEMBIC.get(),
                            localCfg.alchemicalAlembicCanShiftClick);
                });

        // Server-side equipment update on config reload — uses registerLoadListener (not
        // registerSaveListener) because it must also fire on external file edits, which only
        // trigger the load listener (via WatchService → holder.load()).
        holder.registerLoadListener((h, cfg) -> {
            EquipmentRegistry.updateEquipment(EquipmentSlot.HEAD, LepidopteraItems.ALCHEMICAL_ALEMBIC.get(),
                    cfg.alchemicalAlembicCanShiftClick);
            EquipmentRegistry.updateEquipment(EquipmentSlot.HEAD, LepidopteraItems.DEPLETED_ALEMBIC.get(),
                    cfg.alchemicalAlembicCanShiftClick);
            return net.minecraft.world.InteractionResult.SUCCESS;
        });

        debug("INIT: {} completed the initialization phase.", ModHelper.friendlyName());
    }

    @Override
    public void postInit() {
        debug("POST-INIT: {} entered the post-initialization phase.", ModHelper.friendlyName());

        LepidopteraConfig cfg = AutoConfig.getConfigHolder(LepidopteraConfig.class).getConfig();
        EquipmentRegistry.registerEquipment(EquipmentSlot.HEAD, LepidopteraItems.ALCHEMICAL_ALEMBIC.get(),
                cfg.alchemicalAlembicCanShiftClick);
        EquipmentRegistry.registerEquipment(EquipmentSlot.HEAD, LepidopteraItems.DEPLETED_ALEMBIC.get(),
                cfg.alchemicalAlembicCanShiftClick);

        HungryEntityRegistry.register(ApiEntityTags.TICKS_FOOD);
        EquipmentRegistry.registerEquipment(EquipmentSlot.FEET, ApiItemTags.FEET_EQUIPMENT_SHIFTABLE, true);
        EquipmentRegistry.registerEquipment(EquipmentSlot.LEGS, ApiItemTags.LEGS_EQUIPMENT_SHIFTABLE, true);
        EquipmentRegistry.registerEquipment(EquipmentSlot.CHEST, ApiItemTags.CHEST_EQUIPMENT_SHIFTABLE, true);
        EquipmentRegistry.registerEquipment(EquipmentSlot.HEAD, ApiItemTags.HEAD_EQUIPMENT_SHIFTABLE, true);
        EquipmentRegistry.registerEquipment(EquipmentSlot.FEET, ApiItemTags.FEET_EQUIPMENT);
        EquipmentRegistry.registerEquipment(EquipmentSlot.LEGS, ApiItemTags.LEGS_EQUIPMENT);
        EquipmentRegistry.registerEquipment(EquipmentSlot.CHEST, ApiItemTags.CHEST_EQUIPMENT);
        EquipmentRegistry.registerEquipment(EquipmentSlot.HEAD, ApiItemTags.HEAD_EQUIPMENT);
        EquipmentRegistry.registerEquipment(EquipmentSlot.BODY, ApiItemTags.BODY_EQUIPMENT);

        HungryEntityRegistry.protect(ApiEntityTags.TICKS_FOOD);
        EquipmentRegistry.protect(ApiItemTags.FEET_EQUIPMENT, ApiItemTags.LEGS_EQUIPMENT, ApiItemTags.CHEST_EQUIPMENT,
                ApiItemTags.HEAD_EQUIPMENT, ApiItemTags.BODY_EQUIPMENT, ApiItemTags.FEET_EQUIPMENT_SHIFTABLE,
                ApiItemTags.LEGS_EQUIPMENT_SHIFTABLE, ApiItemTags.CHEST_EQUIPMENT_SHIFTABLE,
                ApiItemTags.HEAD_EQUIPMENT_SHIFTABLE);

        if (SYNCED_CONFIG != null && SYNCED_CONFIG.get().showAlembicInCreativeTabs) {
            //noinspection UnstableApiUsage
            CreativeTabRegistry.appendStack(CreativeModeTabs.TOOLS_AND_UTILITIES,
                    () -> new ItemStack(LepidopteraItems.DEPLETED_ALEMBIC.get()));
        }

        debug("POST-INIT: Executing compatibility handler init.");
        Compatibility.preInit();
        Compatibility.init();
        Compatibility.postInit();
        debug("POST-INIT: Compatibility handler init completed.");

        debug("POST-INIT: {} completed the post-initialization phase.", ModHelper.friendlyName());
    }

    @Override
    public void serverStarted(MinecraftServer server) {
        currentServer = server;
        if (SYNCED_CONFIG != null) {
            SYNCED_CONFIG.serverStarted(server);
        }
        Compatibility.serverStarted(server);
    }

    @Override
    public void serverStopped() {
        Compatibility.serverStopped();
        if (SYNCED_CONFIG != null) {
            SYNCED_CONFIG.serverStopped();
        }
        currentServer = null;
    }

    @Override
    public void onTagsLoaded(final RegistryAccess registryAccess) {
        HungryEntityRegistry.onTagsLoaded(registryAccess);
        EquipmentRegistry.onTagsLoaded(registryAccess);
        Compatibility.tagsLoaded();
    }
}
