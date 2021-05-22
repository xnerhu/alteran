package alteran.dimensions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.layer.LayerUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class VoidBiomeProvider extends BiomeProvider {
  public static final Codec<VoidBiomeProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(VoidBiomeProvider::getBiomeRegistry)

  ).apply(instance, VoidBiomeProvider::new));

  private final Registry<Biome> biomeRegistry;
  private final List<Biome> biomes;

  public VoidBiomeProvider(Registry<Biome> biomeRegistry) {
    super(getBiomes(biomeRegistry));
    this.biomes = getBiomes(biomeRegistry);
    this.biomeRegistry = biomeRegistry;
  }

  private static java.util.List<Biome> getBiomes(Registry<Biome> biomeRegistry) {
    List<Biome> biomes = new ArrayList<>();
    //    biomes = settings.getCompiledDescriptor().getBiomes()
    //      .stream().map(biomeRegistry::getOrDefault).collect(Collectors.toList());
    //    if (biomes.isEmpty()) {
    //      biomes.add(biomeRegistry.getOrDefault(Biomes.PLAINS.getLocation()));
    //    }
    biomes.add(biomeRegistry.get(Biomes.PLAINS.location()));
    return biomes;
  }

  @Override
  protected Codec<? extends BiomeProvider> codec() {
    return CODEC;
  }

  public Registry<Biome> getBiomeRegistry() {
    return biomeRegistry;
  }

  @Override
  public BiomeProvider withSeed(long p_230320_1_) {
    return new VoidBiomeProvider(getBiomeRegistry());
  }

  @Override
  public Biome getNoiseBiome(int p_225526_1_, int p_225526_2_, int p_225526_3_) {
    return biomes.get(0);
  }


}
