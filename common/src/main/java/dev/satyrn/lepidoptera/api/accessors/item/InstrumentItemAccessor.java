package dev.satyrn.lepidoptera.api.accessors.item;

import dev.satyrn.lepidoptera.api.annotations.Api;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Mixin accessor exposing the private static {@code play} method of {@link InstrumentItem}.
 *
 * <p>Allows mods to trigger goat horn playback programmatically without subclassing
 * {@code InstrumentItem}.</p>
 *
 * @since 0.4.0+1.19.2
 */
@Api(value = "0.4.0+1.19.2", minecraft = "1.21.1")
@Mixin(InstrumentItem.class)
public interface InstrumentItemAccessor {

    /**
     * Invokes {@code InstrumentItem.play(Level, Player, Instrument)} to play the given
     * instrument sound at the player's position.
     *
     * @param level      the world in which the sound plays
     * @param player     the player playing the instrument
     * @param instrument the instrument definition (sound event + range)
     *
     * @since 0.4.0+1.19.2
     */
    @Api(value = "0.4.0+1.19.2", minecraft = "1.21.1")
    @Contract("_, _, _ -> _")
    static @Invoker void invokePlay(Level level, Player player, Instrument instrument) {
        // noinspection Contract - Static mixin invoker inherits contract of callee
        throw new AssertionError();
    }
}
