package alteran.dimensions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;

public class VoidChunkGenerator extends ChunkGenerator {

  protected final DimensionSettings settings;

  public static final Codec<VoidChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(VoidChunkGenerator::getBiomeRegistry), DimensionSettings.SETTINGS_CODEC.fieldOf("settings").forGetter(VoidChunkGenerator::getSettingsxd)).apply(instance, VoidChunkGenerator::new));

  public VoidChunkGenerator(MinecraftServer server, DimensionSettings settings) {
    this(server.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), settings);
  }

  public VoidChunkGenerator(Registry<Biome> registry, DimensionSettings settings) {
    super(new VoidBiomeProvider(registry), new DimensionStructuresSettings(false));
    this.settings = settings;
  }

  public Registry<Biome> getBiomeRegistry() {
    return ((VoidBiomeProvider) biomeSource).getBiomeRegistry();
    //    return ((RFTBiomeProvider)biomeProvider).getBiomeRegistry();

  }

  @Override
  protected Codec<? extends ChunkGenerator> codec() {
    return CODEC;
  }

  public DimensionSettings getSettingsxd() {
    return settings;
  }

  @Override
  public ChunkGenerator withSeed(long p_230349_1_) {
    return new VoidChunkGenerator(getBiomeRegistry(), getSettingsxd());
  }

  @Override
  public void buildSurfaceAndBedrock(WorldGenRegion p_225551_1_, IChunk p_225551_2_) {

  }

  @Override
  public void fillFromNoise(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_) {

  }

  @Override
  public int getBaseHeight(int p_222529_1_, int p_222529_2_, Heightmap.Type p_222529_3_) {
    return 0;
  }

  @Override
  public IBlockReader getBaseColumn(int p_230348_1_, int p_230348_2_) {
    return new Blockreader(new BlockState[0]);
  }
}
