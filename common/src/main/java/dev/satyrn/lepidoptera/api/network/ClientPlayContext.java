package dev.satyrn.lepidoptera.api.network;

import org.jetbrains.annotations.ApiStatus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

/**
 * Context provided to S2C {@link PacketReceiver} instances and client-side
 * {@link PacketReadyCallback} instances.
 *
 * <p>Extends {@link PacketSender} so implementations can send C2S replies inline.</p>
 */
@ApiStatus.AvailableSince("0.4.0+1.19.2")
@Environment(EnvType.CLIENT)
public interface ClientPlayContext extends PacketSender {

    /**
     * @return the Minecraft client instance
     */
    Minecraft client();

    /**
     * @return the low-level packet listener for the current server connection
     */
    ClientPacketListener handler();
}
