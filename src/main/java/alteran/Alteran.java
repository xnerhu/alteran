package alteran;

import alteran.commands.AlteranCommands;
import alteran.common.AlteranCommon;
import alteran.components.space.world.SpaceBiomeProvider;
import alteran.components.space.world.SpaceChunkGenerator;
import alteran.dimensions.DimensionRegistry;
import alteran.network.AlteranNetwork;
import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

@Mod(AlteranCommon.modId)
public class Alteran {
  public Alteran() {
    IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    IEventBus forgeBus = MinecraftForge.EVENT_BUS;

    modBus.addListener(this::onCommonSetup);
    modBus.addListener(this::onClientSetup);
    forgeBus.addListener(this::serverLoad);

    DeferredRegister<?>[] registers = {AlteranBiomes.BIOMES, AlteranSurfaceBuilders.SURFACE_BUILDERS};

    for (DeferredRegister<?> register : registers) {
      register.register(modBus);
    }
  }

  public void onCommonSetup(FMLCommonSetupEvent e) {
    AlteranNetwork.registerMessages("alteran");

    e.enqueueWork(() -> {
      AlteranBiomes.registerToDictionary();

      Registry.register(Registry.CHUNK_GENERATOR, DimensionRegistry.SPACE_SYSTEM, SpaceChunkGenerator.CODEC);
      Registry.register(Registry.BIOME_SOURCE, DimensionRegistry.BIOMES_ID, SpaceBiomeProvider.CODEC);
    });
  }

  public void onClientSetup(FMLClientSetupEvent e) {
    RenderSpaceSky.onInit();

    DimensionRenderInfo.EFFECTS.put(AlteranSkyEffects.EFFECT_YELLOW_STAR_SYSTEM, new YellowStarSystemRenderInfo());
  }

  public void serverLoad(RegisterCommandsEvent event) {
    AlteranCommands.register(event.getDispatcher());
  }
}
