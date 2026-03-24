package dev.satyrn.lepidoptera.api.accessors.item;

import dev.satyrn.lepidoptera.api.annotations.Api;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Mixin accessor exposing the private static {@code play} method of {@link InstrumentItem}.
 *
 * <p>Allows mods to trigger goat horn playback programmatically without subclassing
 * {@code InstrumentItem}.</p>
 */
@Mixin(InstrumentItem.class)
@Api
public interface InstrumentItemAccessor {
    /**
     * Invokes {@code InstrumentItem.play(Level, Player, Instrument)} to play the given
     * instrument sound at the player's position.
     *
     * @param level      the world in which the sound plays
     * @param player     the player playing the instrument
     * @param instrument the instrument definition (sound event + range)
     */
    @Api @Invoker
    static void invokePlay(Level level, Player player, Instrument instrument) {
        throw new AssertionError();
    }
}
