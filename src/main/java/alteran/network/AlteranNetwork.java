package alteran.network;

import alteran.common.AlteranCommon;
import alteran.network.packets.PacketDimensionUpdate;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class AlteranNetwork {
  private static SimpleChannel instance;

  private static int packetId = 0;

  private static int id() {
    return packetId++;
  }

  public static void registerMessages(String name) {
    SimpleChannel channel = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(AlteranCommon.modId, name)).networkProtocolVersion(() -> "1.0").clientAcceptedVersions(s -> true).serverAcceptedVersions(s -> true).simpleChannel();

    instance = channel;

    channel.registerMessage(id(), PacketDimensionUpdate.class, PacketDimensionUpdate::toBytes, PacketDimensionUpdate::new, PacketDimensionUpdate::handle);
  }

  public static SimpleChannel get() {
    return instance;
  }
}
