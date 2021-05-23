package alteran.components.space.world;

import alteran.AlteranBiomes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.provider.BiomeProvider;

import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class SpaceBiomeProvider extends BiomeProvider {
  public static final Codec<SpaceBiomeProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(SpaceBiomeProvider::getBiomesRegistry)).apply(instance, SpaceBiomeProvider::new));

  private final Registry<Biome> biomesRegistry;

  public SpaceBiomeProvider(Registry<Biome> registry) {
    super(getBiomes(registry));
    this.biomesRegistry = registry;
  }

  @Override
  public BiomeProvider withSeed(long p_230320_1_) {
    return new SpaceBiomeProvider(biomesRegistry);
  }

  @Override
  protected Codec<? extends BiomeProvider> codec() {
    return CODEC;
  }

  private static java.util.List<Biome> getBiomes(Registry<Biome> registry) {
    List<Biome> biomes = new ArrayList<>();
    biomes.add(registry.get(AlteranBiomes.yellowStarSystem));
    return biomes;
  }

  public Registry<Biome> getBiomesRegistry() {
    return biomesRegistry;
  }

  @Override
  public Biome getNoiseBiome(int p_225526_1_, int p_225526_2_, int p_225526_3_) {
    return this.possibleBiomes.get(0);
  }
}
