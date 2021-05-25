package alteran;

import alteran.capabilities.PlayerDataCapability;
import net.minecraft.world.storage.PlayerData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class AlteranCapabilities {
  @CapabilityInject(PlayerDataCapability.class)
  public static final Capability<PlayerDataCapability> PLAYER_DATA = null;
}
