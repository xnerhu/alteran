package alteran;

import alteran.commands.AlteranCommands;
import alteran.common.AlteranCommon;
import alteran.components.space.world.SpaceBiomeProvider;
import alteran.components.space.world.SpaceChunkGenerator;
import alteran.dimensions.DimensionRegistry;
import alteran.network.AlteranNetwork;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
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

@Mod(AlteranCommon.modId)
public class Alteran {
	public Alteran() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;

		modBus.addListener(this::onCommonSetup);
		modBus.addListener(this::clientSetup);
		forgeBus.addListener(this::onRenderWorldLast);
		forgeBus.addListener(this::serverLoad);

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

		e.enqueueWork(() -> {
			AlteranBiomes.registerToDictionary();

			Registry.register(Registry.CHUNK_GENERATOR, DimensionRegistry.SPACE_SYSTEM, SpaceChunkGenerator.CODEC);
			Registry.register(Registry.BIOME_SOURCE, DimensionRegistry.BIOMES_ID, SpaceBiomeProvider.CODEC);
		});
	}

	public void clientSetup(FMLClientSetupEvent e) {
		DimensionRenderInfo.EFFECTS.put(AlteranSkyEffects.EFFECT_YELLOW_STAR_SYSTEM, new YellowStarSystemRenderInfo());
	}

	public void onRenderWorldLast(RenderWorldLastEvent event) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();

		MatrixStack ms = event.getMatrixStack();

		ms.pushPose();

		RenderSystem.disableFog();
		RenderSystem.disableAlphaTest();
		RenderSystem.disableTexture();
		RenderSystem.disableBlend();
		//		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

		Minecraft mc = Minecraft.getInstance();
		ClientPlayerEntity player = mc.player;

		double offsetX = player.xo + (player.getX() - player.xo) * (double) event.getPartialTicks();
		double offsetY = player.yo + (player.getY() - player.yo) * (double) event.getPartialTicks();
		double offsetZ = player.zo + (player.getZ() - player.zo) * (double) event.getPartialTicks();

		RenderSystem.translated(offsetX, offsetY, offsetZ);


		float BOX_RENDER_RANGE = 2f;
		float brightness = 1f;

		VertexFormat format = DefaultVertexFormats.POSITION_COLOR;

		// bottom
		//Minecraft.getInstance().getTextureManager().bindTexture(textureSkyBox[0]);
		bufferbuilder.begin(GL11.GL_QUADS, format);
		//		bufferbuilder.pos(-BOX_RENDER_RANGE, -BOX_RENDER_RANGE, -BOX_RENDER_RANGE).tex( 0.0F,  0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();
		//		bufferbuilder.pos(-BOX_RENDER_RANGE, -BOX_RENDER_RANGE,  BOX_RENDER_RANGE).tex( 0.0F, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		//		bufferbuilder.pos( BOX_RENDER_RANGE, -BOX_RENDER_RANGE,  BOX_RENDER_RANGE).tex(maxUV, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		//		bufferbuilder.pos( BOX_RENDER_RANGE, -BOX_RENDER_RANGE, -BOX_RENDER_RANGE).tex(maxUV,  0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();

		bufferbuilder.vertex(-BOX_RENDER_RANGE, -BOX_RENDER_RANGE, -BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(-BOX_RENDER_RANGE, -BOX_RENDER_RANGE, BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(BOX_RENDER_RANGE, -BOX_RENDER_RANGE, BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(BOX_RENDER_RANGE, -BOX_RENDER_RANGE, -BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();
		tessellator.end();

		// front
		//		if (textureSkyBox.length > 1) {
		//			Minecraft.getInstance().getTextureManager().bindTexture(textureSkyBox[1]);
		//		}
		bufferbuilder.begin(GL11.GL_QUADS, format);
		//		bufferbuilder.pos(-BOX_RENDER_RANGE,  BOX_RENDER_RANGE, -BOX_RENDER_RANGE).tex( 0.0F,  0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();
		//		bufferbuilder.pos(-BOX_RENDER_RANGE, -BOX_RENDER_RANGE, -BOX_RENDER_RANGE).tex( 0.0F, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		//		bufferbuilder.pos( BOX_RENDER_RANGE, -BOX_RENDER_RANGE, -BOX_RENDER_RANGE).tex(maxUV, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		//		bufferbuilder.pos( BOX_RENDER_RANGE,  BOX_RENDER_RANGE, -BOX_RENDER_RANGE).tex(maxUV,  0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();

		bufferbuilder.vertex(-BOX_RENDER_RANGE, BOX_RENDER_RANGE, -BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(-BOX_RENDER_RANGE, -BOX_RENDER_RANGE, -BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(BOX_RENDER_RANGE, -BOX_RENDER_RANGE, -BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(BOX_RENDER_RANGE, BOX_RENDER_RANGE, -BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();
		tessellator.end();

		// back
		//		if (textureSkyBox.length > 1) {
		//			Minecraft.getInstance().getTextureManager().bindTexture(textureSkyBox[2]);
		//		}
		bufferbuilder.begin(GL11.GL_QUADS, format);
		//		bufferbuilder.vertex( BOX_RENDER_RANGE,  BOX_RENDER_RANGE,  BOX_RENDER_RANGE).tex( 0.0F,  0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();
		//		bufferbuilder.vertex( BOX_RENDER_RANGE, -BOX_RENDER_RANGE,  BOX_RENDER_RANGE).tex( 0.0F, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		//		bufferbuilder.vertex(-BOX_RENDER_RANGE, -BOX_RENDER_RANGE,  BOX_RENDER_RANGE).tex(maxUV, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		//		bufferbuilder.vertex(-BOX_RENDER_RANGE,  BOX_RENDER_RANGE,  BOX_RENDER_RANGE).tex(maxUV,  0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();

		bufferbuilder.vertex(BOX_RENDER_RANGE, BOX_RENDER_RANGE, BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(BOX_RENDER_RANGE, -BOX_RENDER_RANGE, BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(-BOX_RENDER_RANGE, -BOX_RENDER_RANGE, BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(-BOX_RENDER_RANGE, BOX_RENDER_RANGE, BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();

		tessellator.end();

		// top
		//		if (textureSkyBox.length > 1) {
		//			Minecraft.getInstance().getTextureManager().bindTexture(textureSkyBox[3]);
		//		}
		bufferbuilder.begin(GL11.GL_QUADS, format);
		//		bufferbuilder.vertex(-BOX_RENDER_RANGE,  BOX_RENDER_RANGE, -BOX_RENDER_RANGE).tex( 0.0F, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		//		bufferbuilder.vertex( BOX_RENDER_RANGE,  BOX_RENDER_RANGE, -BOX_RENDER_RANGE).tex(maxUV, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		//		bufferbuilder.vertex( BOX_RENDER_RANGE,  BOX_RENDER_RANGE,  BOX_RENDER_RANGE).tex(maxUV,  0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();
		//		bufferbuilder.vertex(-BOX_RENDER_RANGE,  BOX_RENDER_RANGE,  BOX_RENDER_RANGE).tex( 0.0F,  0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();

		bufferbuilder.vertex(-BOX_RENDER_RANGE, BOX_RENDER_RANGE, -BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(BOX_RENDER_RANGE, BOX_RENDER_RANGE, -BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(BOX_RENDER_RANGE, BOX_RENDER_RANGE, BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(-BOX_RENDER_RANGE, BOX_RENDER_RANGE, BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();

		tessellator.end();

		// right
		//		if (textureSkyBox.length > 1) {
		//			Minecraft.getInstance().getTextureManager().bindTexture(textureSkyBox[4]);
		//		}
		bufferbuilder.begin(GL11.GL_QUADS, format);
		//		bufferbuilder.vertex( BOX_RENDER_RANGE,  BOX_RENDER_RANGE, -BOX_RENDER_RANGE).tex( 0.0F,  0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();
		//		bufferbuilder.vertex( BOX_RENDER_RANGE, -BOX_RENDER_RANGE, -BOX_RENDER_RANGE).tex( 0.0F, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		//		bufferbuilder.vertex( BOX_RENDER_RANGE, -BOX_RENDER_RANGE,  BOX_RENDER_RANGE).tex(maxUV, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		//		bufferbuilder.vertex( BOX_RENDER_RANGE,  BOX_RENDER_RANGE,  BOX_RENDER_RANGE).tex(maxUV,  0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();

		bufferbuilder.vertex(BOX_RENDER_RANGE, BOX_RENDER_RANGE, -BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(BOX_RENDER_RANGE, -BOX_RENDER_RANGE, -BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(BOX_RENDER_RANGE, -BOX_RENDER_RANGE, BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(BOX_RENDER_RANGE, BOX_RENDER_RANGE, BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();
		tessellator.end();

		// left
		//		if (textureSkyBox.length > 1) {
		//			Minecraft.getInstance().getTextureManager().bindTexture(textureSkyBox[5]);
		//		}
		bufferbuilder.begin(GL11.GL_QUADS, format);
		//		bufferbuilder.vertex(-BOX_RENDER_RANGE,  BOX_RENDER_RANGE,  BOX_RENDER_RANGE).tex( 0.0F,  0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();
		//		bufferbuilder.vertex(-BOX_RENDER_RANGE, -BOX_RENDER_RANGE,  BOX_RENDER_RANGE).tex( 0.0F, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		//		bufferbuilder.vertex(-BOX_RENDER_RANGE, -BOX_RENDER_RANGE, -BOX_RENDER_RANGE).tex(maxUV, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		//		bufferbuilder.vertex(-BOX_RENDER_RANGE,  BOX_RENDER_RANGE, -BOX_RENDER_RANGE).tex(maxUV,  0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();

		bufferbuilder.vertex(-BOX_RENDER_RANGE, BOX_RENDER_RANGE, BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(-BOX_RENDER_RANGE, -BOX_RENDER_RANGE, BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(-BOX_RENDER_RANGE, -BOX_RENDER_RANGE, -BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(-BOX_RENDER_RANGE, BOX_RENDER_RANGE, -BOX_RENDER_RANGE).color(brightness, brightness, brightness, 1.0F).endVertex();
		tessellator.end();

		ms.popPose();

		RenderSystem.enableTexture();
		RenderSystem.enableBlend();
		RenderSystem.enableAlphaTest();
		RenderSystem.enableFog();
	}

}

