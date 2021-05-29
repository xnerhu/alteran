package alteran;

import alteran.common.AlteranCommon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureSpreadConfig;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.*;

public class AlteranConfiguredFeatures {
	public static final ConfiguredFeature<?, ?> ASTEROID_COMMONXd = AlteranFeatures.ASTEROID_FEATURE.get()
		.configured(IFeatureConfig.NONE)
		.decorated(Placement.RANGE.configured(new TopSolidRangeConfig(128, 40, 72)).squared().chance(100));

	//  public static final ConfiguredFeature<?, ?> ASTEROID_COMMON = AlteranFeatures.ASTEROID_FEATURE.get().configured(IFeatureConfig.NONE).chance(1000).decorated(Placement.RANGE_BIASED.configured(new TopSolidRangeConfig(180, 45, 70))).decorated(Placement.COUNT_NOISE.configured(new NoiseDependant(-0.8D, 5, 8)));
	//  public static final ConfiguredFeature<?, ?> ASTEROID_COMMON = AlteranFeatures.ASTEROID_FEATURE.get().configured(IFeatureConfig.NONE).squared().decorated(Placement.COUNT_NOISE_BIASED.configured(new TopSolidWithNoiseConfig(4, 1000, -0.5))); //.chance(5); //.squared();
	public static final ConfiguredFeature<?, ?> ASTEROID_COMMON = AlteranFeatures.ASTEROID_FEATURE.get()
		.configured(IFeatureConfig.NONE).squared()
		.decorated(Placement.RANGE_VERY_BIASED.configured(new TopSolidRangeConfig(200, 40, 60))).decorated(
			Placement.COUNT_NOISE_BIASED
				.configured(new TopSolidWithNoiseConfig(1, 1400, -0.7))); //.range(80).chance(2); //.chance(5); //.squared();


	public static void register() {
		Registry<ConfiguredFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_FEATURE;

		Registry.register(registry, new ResourceLocation(AlteranCommon.modId, "asteroid"), ASTEROID_COMMON);
	}
}
