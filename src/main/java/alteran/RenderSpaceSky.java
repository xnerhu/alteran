package alteran;


import java.util.Random;

import alteran.common.AlteranCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.ISkyRenderHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

//@OnlyIn(Dist.CLIENT)
public class RenderSpaceSky implements ISkyRenderHandler {
	private final VertexFormat skyVertexFormat = DefaultVertexFormats.POSITION;

	private static final ResourceLocation NEBULA_1 = new ResourceLocation(AlteranCommon.modId, "textures/sky/nebula_2.png");
	private static final ResourceLocation NEBULA_2 = new ResourceLocation(AlteranCommon.modId, "textures/sky/nebula_3.png");

	private static VertexBuffer nebulas1;
	private static VertexBuffer nebulas2;
	private VertexBuffer starVertexBuffer;

	public RenderSpaceSky() {
		generateStarData(1500);
		BufferBuilder buffer = Tessellator.getInstance().getBuilder();

		nebulas1 = buildBufferFarFog(buffer, nebulas1, 40, 60, 40, 11515);
		nebulas2 = buildBufferFarFog(buffer, nebulas2, 40, 60, 30, 14151);
	}


	private void makeFarFog(BufferBuilder buffer, double minSize, double maxSize, int count, long seed) {
		Random random = new Random(seed);
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);

		for (int i = 0; i < count; ++i) {
			double posX = random.nextDouble() * 2.0 - 1.0;
			double posY = random.nextDouble() - 0.5;
			double posZ = random.nextDouble() * 2.0 - 1.0;
			double size = MathHelper.nextDouble(random, minSize, maxSize);
			double length = posX * posX + posY * posY + posZ * posZ;
			double distance = 2.0;
			double delta = 1.0 / count;
			if (length < 1.0 && length > 0.001) {
				length = distance / Math.sqrt(length);
				size *= distance;
				distance -= delta;
				posX *= length;
				posY *= length;
				posZ *= length;
				double j = posX * 100.0;
				double k = posY * 100.0;
				double l = posZ * 100.0;
				double m = Math.atan2(posX, posZ);
				double n = Math.sin(m);
				double o = Math.cos(m);
				double p = Math.atan2(Math.sqrt(posX * posX + posZ * posZ), posY);
				double q = Math.sin(p);
				double r = Math.cos(p);
				double s = random.nextDouble() * Math.PI * 2.0;
				double t = Math.sin(s);
				double u = Math.cos(s);

				int pos = 0;
				for (int v = 0; v < 4; ++v) {
					double x = (double) ((v & 2) - 1) * size;
					double y = (double) ((v + 1 & 2) - 1) * size;
					double aa = x * u - y * t;
					double ab = y * u + x * t;
					double ad = aa * q + 0.0 * r;
					double ae = 0.0 * q - aa * r;
					double af = ae * n - ab * o;
					double ah = ab * n + ae * o;
					float texU = (pos >> 1) & 1;
					float texV = ((pos + 1) >> 1) & 1;
					pos++;
					buffer.vertex(j + af, k + ad, l + ah).uv(texU, texV).endVertex();
				}
			}
		}
	}

	private VertexBuffer buildBufferFarFog(BufferBuilder bufferBuilder, VertexBuffer buffer, double minSize, double maxSize, int count, long seed) {
		if (buffer != null) {
			buffer.close();
		}

		buffer = new VertexBuffer(DefaultVertexFormats.POSITION_TEX);
		makeFarFog(bufferBuilder, minSize, maxSize, count, seed);
		bufferBuilder.end();
		buffer.upload(bufferBuilder);

		return buffer;
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
	public void render(int ticks, float partialTicks, MatrixStack ms, ClientWorld world, Minecraft mc) {
		RenderSystem.disableAlphaTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(false);

		ms.pushPose();
		renderStarData(starVertexBuffer, ms, skyVertexFormat);
		ms.popPose();

		float blindA = 1f;

		float blind02 = blindA * 0.2f;

		float time = (ticks % 360000) * 0.000017453292F;

		TextureManager textureManager = mc.getTextureManager();

		ms.pushPose();
		ms.last().normal().mul(new Quaternion(0, -time, 0, false));
		textureManager.bind(NEBULA_1);
		renderBuffer(ms, nebulas1, DefaultVertexFormats.POSITION_TEX, 0.7f, 1f, 0.7f, blind02);
		ms.popPose();

		ms.pushPose();
		ms.last().normal().mul(new Quaternion(0, -(time * 2), 0, false));
		textureManager.bind(NEBULA_2);
		renderBuffer(ms, nebulas2, DefaultVertexFormats.POSITION_TEX, 0, 0.7f, 1f, blind02);
		ms.popPose();

		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();
	}

	private void renderBuffer(MatrixStack matrixStackIn, VertexBuffer buffer, VertexFormat format, float r, float g, float b, float a) {
		RenderSystem.color4f(r, g, b, a);
		buffer.bind();
		format.setupBufferState(0L);
		buffer.draw(matrixStackIn.last().pose(), 7);
		VertexBuffer.unbind();
		format.clearBufferState();
	}

	private static void renderStarData(VertexBuffer starVBO, MatrixStack matrixStack, VertexFormat vertexFormat) {
		RenderSystem.disableTexture();
		float brightness = 1f;
		RenderSystem.color4f(brightness, brightness, brightness, brightness);
		starVBO.bind();
		vertexFormat.setupBufferState(0L);
		starVBO.draw(matrixStack.last().pose(), 7);
		VertexBuffer.unbind();
		vertexFormat.clearBufferState();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableTexture();
	}

	private static float randBetween(float min, float max) {
		Random r = new Random();
		return r.nextFloat() * (max - min) + min;
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