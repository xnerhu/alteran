package alteran.components.space;

import alteran.common.AlteranCommon;
import alteran.components.dimensions.DimensionBuilder;
import alteran.components.dimensions.DimensionData;
import alteran.components.dimensions.DimensionId;
import alteran.components.dimensions.DimensionStorage;
import alteran.components.space.world.SpaceChunkGenerator;
import alteran.dimensions.DimensionRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class SpaceSystemManager {
  private static final SpaceSystemManager instance = new SpaceSystemManager();

  public static SpaceSystemManager get() {
    return instance;
  }

  // Returns null on success, otherwise an error string
  public String createDimension(World world, String name, long seed) {
    DimensionId id = DimensionId.fromResourceLocation(new ResourceLocation(AlteranCommon.modId, name));

    if (id.loadWorld(world) != null) {
      return "Dimension already exists!";
    }
    //    DimensionDescriptor descriptor = new DimensionDescriptor();

    createWorld(world, name, seed/*, descriptor*/);

    return null;
  }

  public ServerWorld createWorld(World world, String name, long seed/*, DimensionDescriptor descriptor*/) {
    ResourceLocation id = new ResourceLocation(AlteranCommon.modId, name);

    DimensionStorage mgr = DimensionStorage.get(world);

    DimensionData data = mgr.getData(id);

    if (data != null) {
      AlteranCommon.logger.error("There is already a dimension with this id: " + name + ", " + data.getId());
      throw new RuntimeException("There is already a dimension with this id: " + name);
    }

    RegistryKey<World> key = RegistryKey.create(Registry.DIMENSION_REGISTRY, id);
    SpaceSystemSettings settings = new SpaceSystemSettings(seed, "xdd");

    DimensionType type = world.getServer().registryAccess().registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY).get(DimensionRegistry.YELLOW_STAR_SYSTEM);

    ServerWorld result = DimensionBuilder.getOrCreateWorld(world.getServer(), key, (server, registryKey) -> new Dimension(() -> type, new SpaceChunkGenerator(server, settings))); /* new ExampleChunkGenerator(server.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY)))*/

    data = new DimensionData(id);
    mgr.register(data);

    return result;
  }

  public World getDimWorld(String name) {
    ResourceLocation id = new ResourceLocation(name);
    DimensionId type = DimensionId.fromResourceLocation(id);
    ServerWorld world = type.getWorld();
    AlteranCommon.logger.error("NAME: " + name + ", ID: " + id.toString(), ", TYPE: " + type.toString() + ", WORLD: " + (world == null));

    if (world == null) {
      if (!name.contains(":")) {
        id = new ResourceLocation(AlteranCommon.modId, name);
        type = DimensionId.fromResourceLocation(id);

        AlteranCommon.logger.error("NAME: " + name + ", ID: " + id.toString(), ", TYPE: " + type.toString() + ", WORLD: " + (world == null));
        return type.getWorld();
      }
    }
    return world;
  }

  public boolean isEntityInSpace(Entity entity) {
    final DimensionId dimId = DimensionId.fromResourceLocation(new ResourceLocation(AlteranCommon.modId, "xd"));

    return dimId.sameDimension(entity.level);
  }
}
