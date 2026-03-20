package dev.satyrn.lepidoptera;

import dev.satyrn.lepidoptera.annotations.ModMeta;
import dev.satyrn.lepidoptera.api.LepidopteraMod;
import dev.satyrn.lepidoptera.util.ModHelper;
import dev.satyrn.lepidoptera.util.NotInitializable;
import net.minecraft.world.level.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

import static net.minecraft.world.level.GameRules.BooleanValue;
import static net.minecraft.world.level.GameRules.Key;

@ModMeta(value = LepidopteraAPI.MOD_ID, name = "Lepidoptera API", semVer = "1.0.0-SNAPSHOT+1.21.1")
public class LepidopteraAPI implements LepidopteraMod {
    public static final String MOD_ID = "lepidoptera_api";
    private static final Logger LOGGER = LogManager.getLogger();

    public static LepidopteraMod INSTANCE = new LepidopteraAPI();
    public static @Nullable Key<BooleanValue> RULE_ENTITY_STARVATION;

    /**
     * Prevent class initialization
     */
    private LepidopteraAPI() {}

    @Override
    public void init() {
        debug("INIT: {} entered the initialization phase.", ModHelper.friendlyName());

        RULE_ENTITY_STARVATION = GameRules.register("doAnimalStarvation", GameRules.Category.MOBS,
                GameRules.BooleanValue.create(false));

        debug("INIT: {} Completed the initialization phase", ModHelper.friendlyName());
    }

    public static void info(String message, Object... params) {
        LOGGER.info(message, params);
    }

    public static void debug(String message, Object... params) {
        LOGGER.debug(message, params);
    }

    public static void warn(String message, Object... params) {
        LOGGER.warn(message, params);
    }

    public static void error(String message, Throwable e) {
        LOGGER.error(message, e);
    }
}
