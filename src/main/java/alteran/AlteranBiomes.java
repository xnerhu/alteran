package alteran;

import alteran.common.AlteranCommon;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AlteranBiomes {
	public static final DeferredRegister<Biome> BIOMES = DeferredRegister
		.create(ForgeRegistries.BIOMES, AlteranCommon.modId);

	public static RegistryKey<Biome> yellowStarSystem = register("yellow_star_system");

	private static RegistryKey<Biome> register(String name) {
		return RegistryKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(AlteranCommon.modId, name));
	}

	public static void registerToDictionary() {
		BiomeDictionary
			.addTypes(yellowStarSystem, BiomeDictionary.Type.VOID, BiomeDictionary.Type.COLD, BiomeDictionary.Type.DEAD,
				BiomeDictionary.Type.SPOOKY);
	}
}
