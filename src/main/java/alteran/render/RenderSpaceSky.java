package alteran.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.ISkyRenderHandler;

import java.util.Random;

public class RenderSpaceSky implements ISkyRenderHandler {
	private final VertexFormat skyVertexFormat = DefaultVertexFormats.POSITION;

	private VertexBuffer starVertexBuffer;

	private Vector3f axis;

	private static RenderSpaceSky INSTANCE = null;

	public static RenderSpaceSky getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new RenderSpaceSky();
		}
		return INSTANCE;
	}

	public RenderSpaceSky() {
		generateStarData(15000);
		Random random = new Random(203484);
		axis = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
		axis.normalize();
	}

	private void generateStarData(int numberOfStars) {
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuilder();

		if (this.starVertexBuffer != null) {
			this.starVertexBuffer.close();
			starVertexBuffer = null;
		}

		this.starVertexBuffer = new VertexBuffer(skyVertexFormat);
		uploadStarData(bufferBuilder, numberOfStars);
		bufferBuilder.end();
		this.starVertexBuffer.upload(bufferBuilder);
	}

	@Override
	public void render(int ticks, float partialTicks, MatrixStack matrixStack, ClientWorld world, Minecraft mc) {
		RenderSystem.disableAlphaTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(false);
		//mc.getTextureManager().bindTexture(END_SKY_TEXTURES);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();

		matrixStack.pushPose();
		renderStarData(starVertexBuffer, matrixStack, skyVertexFormat);
		matrixStack.popPose();

		for (int i = 0; i < 6; ++i) {
			matrixStack.pushPose();
			if (i == 1) {
				matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
			}

			if (i == 2) {
				matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
			}

			if (i == 3) {
				matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
			}

			if (i == 4) {
				matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
			}

			if (i == 5) {
				matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
			}

			Matrix4f matrix4f = matrixStack.last().pose();
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
			bufferbuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).color(0, 0, 0, 255).endVertex();
			bufferbuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).color(0, 0, 0, 255).endVertex();
			bufferbuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).color(0, 0, 0, 255).endVertex();
			bufferbuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).color(0, 0, 0, 255).endVertex();
			tessellator.end();
			matrixStack.popPose();
		}

		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();
	}


	private static void renderStarData(VertexBuffer starVBO, MatrixStack matrixStack, VertexFormat vertexFormat) {
		float brightness = 1.0F;
		RenderSystem.color4f(brightness, brightness, brightness, brightness);
		starVBO.bind();
		vertexFormat.setupBufferState(0L);
		starVBO.draw(matrixStack.last().pose(), 7);
		VertexBuffer.unbind();
		vertexFormat.clearBufferState();
	}

	private static void uploadStarData(BufferBuilder bufferBuilder, int numberOfStars) {
		Random random = new Random(10842L);
		bufferBuilder.begin(7, DefaultVertexFormats.POSITION);

		for (int i = 0; i < numberOfStars; ++i) {
			double d0 = random.nextFloat() * 2.0F - 1.0F;
			double d1 = random.nextFloat() * 2.0F - 1.0F;
			double d2 = random.nextFloat() * 2.0F - 1.0F;
			double d3 = 0.15F + random.nextFloat() * 0.1F;
			double d4 = d0 * d0 + d1 * d1 + d2 * d2;
			if (d4 < 1.0D && d4 > 0.01D) {
				d4 = 1.0D / Math.sqrt(d4);
				d0 = d0 * d4;
				d1 = d1 * d4;
				d2 = d2 * d4;
				double d5 = d0 * 100.0D;
				double d6 = d1 * 100.0D;
				double d7 = d2 * 100.0D;
				double d8 = Math.atan2(d0, d2);
				double d9 = Math.sin(d8);
				double d10 = Math.cos(d8);
				double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
				double d12 = Math.sin(d11);
				double d13 = Math.cos(d11);
				double d14 = random.nextDouble() * Math.PI * 2.0D;
				double d15 = Math.sin(d14);
				double d16 = Math.cos(d14);

				for (int j = 0; j < 4; ++j) {
					double d18 = (double) ((j & 2) - 1) * d3;
					double d19 = (double) ((j + 1 & 2) - 1) * d3;
					double d21 = d18 * d16 - d19 * d15;
					double d22 = d19 * d16 + d18 * d15;
					double d23 = d21 * d12 + 0.0D * d13;
					double d24 = 0.0D * d12 - d21 * d13;
					double d25 = d24 * d9 - d22 * d10;
					double d26 = d22 * d9 + d24 * d10;
					bufferBuilder.vertex(d5 + d25, d6 + d23, d7 + d26).endVertex();
				}
			}
		}
	}
}
