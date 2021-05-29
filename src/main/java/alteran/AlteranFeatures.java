package alteran;

import alteran.common.AlteranCommon;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AlteranFeatures {
	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister
		.create(ForgeRegistries.FEATURES, AlteranCommon.modId);

	public static final RegistryObject<Feature<NoFeatureConfig>> ASTEROID_FEATURE = FEATURES
		.register("asteroid", () -> new AsteroidFeature(NoFeatureConfig.CODEC));
}
