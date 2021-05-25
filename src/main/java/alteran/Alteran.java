package alteran;

import alteran.capabilities.PlayerDataCapability;
import alteran.commands.AlteranCommands;
import alteran.common.AlteranCommon;
import alteran.components.dimensions.DimensionData;
import alteran.components.dimensions.DimensionId;
import alteran.components.space.world.SpaceBiomeProvider;
import alteran.components.space.world.SpaceChunkGenerator;
import alteran.dimensions.DimensionRegistry;
import alteran.loader.model.ModelLoader;
import alteran.loader.model.OBJModel;
import alteran.network.AlteranNetwork;
import alteran.utils.ReflectionUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.chunk.listener.IChunkStatusListenerFactory;
import net.minecraft.world.storage.PlayerData;
import net.minecraft.world.storage.SaveFormat;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.function.Function;

@Mod(AlteranCommon.modId)
public class Alteran {
  public static boolean GRAVITY_DISABLED = true;

  public Alteran() {
    IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    IEventBus forgeBus = MinecraftForge.EVENT_BUS;

    modBus.addListener(this::onCommonSetup);
    modBus.addListener(this::clientSetup);
    forgeBus.addListener(this::serverLoad);
    forgeBus.addListener(this::onLivingTick);
    forgeBus.addListener(this::onLivingUpdate);
    forgeBus.addGenericListener(Entity.class, this::attachPlayerCap);

    DeferredRegister<?>[] registers = {AlteranBiomes.BIOMES, AlteranSurfaceBuilders.SURFACE_BUILDERS};

    for (DeferredRegister<?> register : registers) {
      register.register(modBus);
    }
  }


  public void serverLoad(RegisterCommandsEvent event) {
    AlteranCommands.register(event.getDispatcher());
  }

  public void onCommonSetup(FMLCommonSetupEvent e) {
    AlteranNetwork.registerMessages("alteran");
    CapabilityManager.INSTANCE.register(PlayerDataCapability.class, new PlayerDataCapability.Storage(), () -> new PlayerDataCapability(null));

    e.enqueueWork(() -> {
      AlteranBiomes.registerToDictionary();

      Registry.register(Registry.CHUNK_GENERATOR, DimensionRegistry.SPACE_SYSTEM, SpaceChunkGenerator.CODEC);
      Registry.register(Registry.BIOME_SOURCE, DimensionRegistry.BIOMES_ID, SpaceBiomeProvider.CODEC);

    });
  }

  public void clientSetup(FMLClientSetupEvent e) {
    try {
      ModelLoader.reloadModels();

      Field field = DimensionRenderInfo.class.getField("EFFECTS");
      field.setAccessible(true);
      Object2ObjectMap<ResourceLocation, DimensionRenderInfo> effects = (Object2ObjectMap<ResourceLocation, DimensionRenderInfo>) field.get(null);
      effects.put(AlteranSkyEffects.EFFECT_YELLOW_STAR_SYSTEM, new YellowStarSystemRenderInfo());
    } catch (IllegalAccessException | NoSuchFieldException | IOException x) {
      x.printStackTrace();
    }
  }


  public void onLivingTick(LivingEvent.LivingUpdateEvent e) {
    LivingEntity entity = e.getEntityLiving();

    if (!GRAVITY_DISABLED && entity instanceof PlayerEntity) {
      DimensionId dimId = DimensionId.fromResourceLocation(new ResourceLocation(AlteranCommon.modId, "xd"));

      if (!dimId.sameDimension(entity.level)) {
        return;
      }

      PlayerDataCapability data = entity.getCapability(AlteranCapabilities.PLAYER_DATA).orElse(null);

      if (entity.isShiftKeyDown()) {
        data.momentum = new Vector3d(0, 0, 0);
        return;
      }

      Vector3d momentum = data.momentum;

      if (momentum.x != 0 || momentum.y != 0 || momentum.z != 0) {
        double x = momentum.x + (1 - Math.abs(entity.xxa)) * -Math.signum(entity.xxa);
        double z = momentum.z + (1 - Math.abs(entity.zza)) * -Math.signum(entity.zza);

        entity.setDeltaMovement(x, momentum.y, z);
      }
    }
  }

  public void onLivingUpdate(LivingEvent.LivingUpdateEvent e) {
    LivingEntity entity = e.getEntityLiving();

    if (entity instanceof PlayerEntity) {
      PlayerEntity player = (PlayerEntity) entity;

      DimensionId dimId = DimensionId.fromResourceLocation(new ResourceLocation(AlteranCommon.modId, "xd"));

      if (!dimId.sameDimension(entity.level)) {
        return;
      }


      PlayerDataCapability data = entity.getCapability(AlteranCapabilities.PLAYER_DATA).orElse(null);

      if (!entity.isShiftKeyDown() && !entity.isOnGround() && (data.momentum.x == 0 && data.momentum.y == 0 && data.momentum.z == 0)) {
        Vector3d forward = entity.getForward();
        double speed = entity.getSpeed();

        data.momentum = new Vector3d(speed * forward.x, 0.25 * speed * forward.y, speed * forward.z);
      } else if (entity.isOnGround()) {
        data.momentum = new Vector3d(0, 0, 0);
      }
    }
  }

  public void attachPlayerCap(AttachCapabilitiesEvent<Entity> e) {
    Entity obj = e.getObject();

    if (obj instanceof PlayerEntity)
      e.addCapability(new ResourceLocation(AlteranCommon.modId, "player_data"), new PlayerDataCapability.Provider(new PlayerDataCapability((PlayerEntity) obj)));
  }
}

