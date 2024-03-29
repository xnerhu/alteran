package alteran.components.space.worldgen;

import alteran.components.space.SpaceSystemSettings;
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
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;

public class ExampleChunkGenerator extends ChunkGenerator {
  public static final Codec<ExampleChunkGenerator> CODEC = RegistryLookupCodec.create(Registry.BIOME_REGISTRY).xmap(ExampleChunkGenerator::new, ExampleChunkGenerator::getBiomesRegistry).codec();

  private final Registry<Biome> biomes;

  public ExampleChunkGenerator(Registry<Biome> registry) {
    super(new SingleBiomeProvider(registry.getOrThrow(Biomes.PLAINS)), new DimensionStructuresSettings(false));
    this.biomes = registry;
  }

  @Override
  protected Codec<? extends ChunkGenerator> codec() {
    return CODEC;
  }

  public Registry<Biome> getBiomesRegistry() {
    return this.biomes;
  }

  @Override
  public ChunkGenerator withSeed(long seed) {
    return this;
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
