package alteran.dimensions;

import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Objects;

public class DimensionId {
  private final RegistryKey<World> id;
  private static final Lazy<DimensionId> OVERWORLD = Lazy.of(() -> {
    return new DimensionId(World.OVERWORLD);
  });
  private static final Lazy<DimensionId> NETHER = Lazy.of(() -> {
    return new DimensionId(World.NETHER);
  });
  private static final Lazy<DimensionId> END = Lazy.of(() -> {
    return new DimensionId(World.END);
  });

  private DimensionId(RegistryKey<World> id) {
    this.id = id;
  }

  public static DimensionId overworld() {
    return (DimensionId) OVERWORLD.get();
  }

  public static DimensionId nether() {
    return (DimensionId) NETHER.get();
  }

  public static DimensionId end() {
    return (DimensionId) END.get();
  }

  public static DimensionId fromId(RegistryKey<World> id) {
    return new DimensionId(id);
  }

  public static DimensionId fromPacket(PacketBuffer buf) {
    RegistryKey<World> key = RegistryKey.create(Registry.DIMENSION_REGISTRY, buf.readResourceLocation());
    return new DimensionId(key);
  }

  public static DimensionId fromWorld(World world) {
    return new DimensionId(world.dimension());
  }

  public static DimensionId fromResourceLocation(ResourceLocation location) {
    RegistryKey<World> key = RegistryKey.create(Registry.DIMENSION_REGISTRY, location);
    return new DimensionId(key);
  }

  public RegistryKey<World> getId() {
    return this.id;
  }

  public ResourceLocation getRegistryName() {
    return this.id.location();
  }

  public String getName() {
    return this.id.location().getPath();
  }

  public boolean isOverworld() {
    return this.id.equals(World.OVERWORLD);
  }

  public void toBytes(PacketBuffer buf) {
    buf.writeResourceLocation(this.id.location());
  }

  public ServerWorld loadWorld() {
    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
    return server.getLevel(this.id);
  }

  public ServerWorld getWorld() {
    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
    return server.getLevel(this.id);
  }

  public ServerWorld loadWorld(World otherWorld) {
    return otherWorld.getServer().getLevel(this.id);
  }

  public static boolean sameDimension(World world1, World world2) {
    return world1.dimension().equals(world2.dimension());
  }

  public boolean sameDimension(World world) {
    return this.id.equals(world.dimension());
  }

  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o != null && this.getClass() == o.getClass()) {
      DimensionId that = (DimensionId) o;
      return Objects.equals(this.id, that.id);
    } else {
      return false;
    }
  }

  public int hashCode() {
    return Objects.hash(new Object[]{this.id});
  }
}
