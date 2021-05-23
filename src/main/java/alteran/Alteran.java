package alteran;

import alteran.commands.AlteranCommands;
import alteran.common.AlteranCommon;
import alteran.network.AlteranNetwork;
import alteran.render.RenderSpaceSky;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.ICloudRenderHandler;
import net.minecraftforge.client.ISkyRenderHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;

@Mod(AlteranCommon.modId)
public class Alteran {
	public Alteran() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;

		modBus.addListener(this::onCommonSetup);
		modBus.addListener(this::clientSetup);
		forgeBus.addListener(this::serverLoad);
	}

	public void onCommonSetup(FMLCommonSetupEvent e) {
		AlteranNetwork.registerMessages("alteran");
	}

	public void serverLoad(RegisterCommandsEvent event) {
		AlteranCommands.register(event.getDispatcher());
	}

	public void clientSetup(FMLClientSetupEvent event) {
		DimensionRenderInfo.EFFECTS.put(new ResourceLocation(AlteranCommon.modId, "aha"),
			// cloudHeight, alternate sky color, fog type, render sky, diffuse lighting
			new DimensionRenderInfo(-1, true, DimensionRenderInfo.FogType.NORMAL, false, false) {
				@Override
				public Vector3d getBrightnessDependentFogColor(Vector3d fogColor, float partialTicks) {
					return new Vector3d(0, 0, 0);
				}

				@Override
				public boolean isFoggyAt(int posX, int posY) {
					return false;
				}

				@Override
				public ISkyRenderHandler getSkyRenderHandler() {
					return new RenderSpaceSky();
				}

				@Override
				public ICloudRenderHandler getCloudRenderHandler() {
					return (int ticks, float partialTicks, MatrixStack matrixStack, ClientWorld world, Minecraft mc, double viewEntityX, double viewEntityY, double viewEntityZ) -> {

					};
				}

				@Override
				public float[] getSunriseColor(float p_230492_1_, float p_230492_2_) {
					return null;
				}
			});


	}
}
