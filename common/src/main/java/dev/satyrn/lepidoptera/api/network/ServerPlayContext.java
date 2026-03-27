package dev.satyrn.lepidoptera.api.network;

import org.jetbrains.annotations.ApiStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

/**
 * Context provided to C2S {@link PacketReceiver} instances and server-side
 * {@link PacketReadyCallback} instances.
 *
 * <p>Extends {@link PacketSender} so implementations can send S2C replies inline.</p>
 */
@ApiStatus.AvailableSince("0.4.0+1.19.2")
public interface ServerPlayContext extends PacketSender {

    /**
     * @return the server instance
     */
    MinecraftServer server();

    /**
     * @return the player whose connection triggered this event
     */
    ServerPlayer player();

    /**
     * @return the low-level packet listener for this player's connection
     */
    ServerGamePacketListenerImpl handler();
}
