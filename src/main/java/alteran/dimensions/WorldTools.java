package alteran.dimensions;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class WorldTools {
  public WorldTools() {
  }

  public static boolean isLoaded(World world, BlockPos pos) {
    return world != null && pos != null ? world.isLoaded(pos) : false;
  }

  public static ServerWorld getOverworld() {
    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
    return server.getLevel(World.OVERWORLD);
  }

  public static ServerWorld getOverworld(World world) {
    MinecraftServer server = world.getServer();
    return server.getLevel(World.OVERWORLD);
  }

  public static ServerWorld loadWorld(DimensionId type) {
    ServerWorld world = getWorld(type);
    return world == null ? type.loadWorld() : world;
  }

  public static ServerWorld getWorld(DimensionId type) {
    return type.getWorld();
  }

  public static ServerWorld getWorld(World world, DimensionId type) {
    return type.loadWorld(world);
  }
}
