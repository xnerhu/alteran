package alteran.dimensions;

import alteran.common.AlteranCommon;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.server.ServerWorld;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class DimensionManager {
  private static final DimensionManager instance = new DimensionManager();

  public static DimensionManager get() {
    return instance;
  }

  // Returns null on success, otherwise an error string
  public String createDimension(World world, String name, long seed) {
    DimensionId id = DimensionId.fromResourceLocation(new ResourceLocation(AlteranCommon.modId, name));
    AlteranCommon.logger.info("Xddd");
    if (id.loadWorld(world) != null) {
      return "Dimension already exists!";
    }

    //    DimensionDescriptor descriptor = new DimensionDescriptor();
    //    if (!filename.endsWith(".json")) {
    //      filename += ".json";
    //    }
    //    try (InputStream inputstream = RFToolsDim.class.getResourceAsStream("/data/rftoolsdim/rftdim/" + filename)) {
    //      try (BufferedReader br = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8))) {
    //        JsonParser parser = new JsonParser();
    //        JsonElement element = parser.parse(br);
    //        descriptor.read(element.getAsJsonArray());
    //      }
    //    } catch (IOException ex) {
    //      throw new UncheckedIOException(ex);
    //    }

    DimensionDescriptor descriptor = new DimensionDescriptor();

    createWorld(world, name, seed, descriptor);

    return null;
  }

  public ServerWorld createWorld(World world, String name, long seed, DimensionDescriptor descriptor) {
    ResourceLocation id = new ResourceLocation(AlteranCommon.modId, name);

    PersistantDimensionManager mgr = PersistantDimensionManager.get(world);

    DimensionData data = mgr.getData(id);

    if (data != null) {
      AlteranCommon.logger.error("There is already a dimension with this id: " + name);
      throw new RuntimeException("There is already a dimension with this id: " + name);
    }

    data = mgr.getData(descriptor);
    if (data != null) {
      AlteranCommon.logger.error("There is already a dimension with this descriptor: " + name);
      throw new RuntimeException("There is already a dimension with this descriptor: " + name);
    }

    RegistryKey<World> key = RegistryKey.create(Registry.DIMENSION_REGISTRY, id);
    DimensionSettings settings = new DimensionSettings(seed, descriptor.compact());

    DimensionType type = world.getServer().registryAccess().registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY).get(DimensionRegistry.FIXED_DAY_ID);

    AlteranCommon.logger.info("type " + (type == null));
    AlteranCommon.logger.info("key" + (key == null));


    //    ServerWorld result = DimensionHelper.getOrCreateWorld(world.getServer(), key, (server, registryKey) -> new Dimension(() -> type, new FlatChunkGenerator(FlatGenerationSettings.getDefault(server.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY)))));
    ServerWorld result = DimensionHelper.getOrCreateWorld(world.getServer(), key, (server, registryKey) -> new Dimension(() -> type, new VoidChunkGenerator(server, settings)));

    data = new DimensionData(id, descriptor);
    mgr.register(data);

    return result;
  }

  public World getDimWorld(String name) {
    ResourceLocation id = new ResourceLocation(name);
    DimensionId type = DimensionId.fromResourceLocation(id);
    ServerWorld world = type.getWorld();
    if (world == null) {
      if (!name.contains(":")) {
        id = new ResourceLocation(AlteranCommon.modId, name);
        type = DimensionId.fromResourceLocation(id);
        return type.getWorld();
      }
    }
    return world;
  }
}
