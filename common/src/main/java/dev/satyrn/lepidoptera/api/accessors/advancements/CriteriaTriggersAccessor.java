package dev.satyrn.lepidoptera.api.accessors.advancements;

import dev.satyrn.lepidoptera.api.annotations.Api;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;

/**
 * Invoker mixin for {@link CriteriaTriggers}.
 *
 * @author Isabel Maskrey
 * @since 0.4.0+1.19.2
 */
@Api(value = "0.4.0+1.19.2", minecraft = "1.21.1")
@Mixin(CriteriaTriggers.class)
public interface CriteriaTriggersAccessor {

    /**
     * Calls {@code Criteria.register(Criterion)}.
     *
     * @param object The object to register.
     * @param <T>    The criterion type.
     *
     * @return The registered criterion.
     *
     * @since 0.4.0+1.19.2
     */
    @Api(value = "0.4.0+1.19.2", minecraft = "1.21.1")
    @Contract("_, !null -> !null; _, null -> null")
    static @Invoker <T extends CriterionTrigger<?>> T callRegister(final String string, final @Nullable T object) {
        // noinspection Contract - Static invoker mixin; this code is never called.
        throw new AssertionError();
    }
}
