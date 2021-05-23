package alteran;

import alteran.common.AlteranCommon;
import alteran.components.space.world.SpaceSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AlteranSurfaceBuilders {
  public static final DeferredRegister<SurfaceBuilder<?>> SURFACE_BUILDERS = DeferredRegister.create(ForgeRegistries.SURFACE_BUILDERS, AlteranCommon.modId);

  public static final RegistryObject<SurfaceBuilder<SurfaceBuilderConfig>> SPACE_SURFACE_BUILDER = SURFACE_BUILDERS.register("space_surface_builder", () -> new SpaceSurfaceBuilder(SurfaceBuilderConfig.CODEC));
}
