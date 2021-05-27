package alteran;


import java.util.ArrayList;
import java.util.Random;

import alteran.common.AlteranCommon;
import alteran.loader.model.ModelLoader;
import alteran.loader.model.OBJModel;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.serialization.ListBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.*;
import net.minecraftforge.client.ISkyRenderHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;

//@OnlyIn(Dist.CLIENT)
public class RenderSpaceSky implements ISkyRenderHandler {
	private static RenderSpaceSky INSTANCE;

	private static final VertexFormat skyVertexFormat = DefaultVertexFormats.POSITION_COLOR;

	private static final ResourceLocation NEBULA_1 = new ResourceLocation(AlteranCommon.modId, "textures/sky/nebula_2.png");
	private static final ResourceLocation NEBULA_2 = new ResourceLocation(AlteranCommon.modId, "textures/sky/nebula_3.png");

	public static RenderSpaceSky getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new RenderSpaceSky();
		}
		return INSTANCE;
	}

	private static VertexBuffer nebulas1;
	private static VertexBuffer nebulas2;
	private VertexBuffer starVertexBuffer;

	private final ArrayList<AVector3f> lightSources = new ArrayList<>();

	private boolean shouldReload = false;

	public RenderSpaceSky() {
		init();
	}

	public void reload() {
		this.shouldReload = true;
	}

	public void init() {
		lightSources.clear();

		addLightSource(new AVector3f(0, 0, 1f));
		//addLightSource(new AVector3f(0, 100f, 100f));

		generateStarData(15000);
		BufferBuilder buffer = Tessellator.getInstance().getBuilder();

		nebulas1 = buildBufferFarFog(buffer, nebulas1, 40, 60, 50, new Random().nextInt());
		nebulas2 = buildBufferFarFog(buffer, nebulas2, 40, 60, 40, new Random().nextInt());
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

	private void addLightSource(AVector3f source) {
		lightSources.add(source);
	}

	@Override
	public void render(int ticks, float partialTicks, MatrixStack ms, ClientWorld world, Minecraft mc) {
		if (shouldReload) {
			init();
			shouldReload = false;
		}

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
		renderBuffer(ms, nebulas2, DefaultVertexFormats.POSITION_TEX, 0, 0.8f, 1f, blind02);
		ms.popPose();

		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();

		// TODO(sentialx): sort order by distance

		renderStar(ms, "textures/celestial/sun2.png", new AVector3f(0, 0, 100), 100, new AVector3f(1f, 1f, 1f), 1f);
		renderStar(ms, "textures/celestial/ring.png", new AVector3f(0, 0, 0), 4.15f, new AVector3f(0.3f, 0.5f, 1f), 0.9f);
		renderPlanet(ms, 0, 0, 0, 5, false, true, "textures/celestial/earth2.jpg");


	}

	public static Quaternion lookAt(AVector3f sourcePoint, AVector3f destPoint) {
		AVector3f forwardVector = destPoint.substract(sourcePoint).normalize();

		float dot = AVector3f.forward.dot(forwardVector);

		if (Math.abs(dot - (-1.0f)) < 0.000001f) {
			return new Quaternion(3.1415926535897932f, AVector3f.up.x, AVector3f.up.y, AVector3f.up.z);
		}
		if (Math.abs(dot - (1.0f)) < 0.000001f) {
			return new Quaternion(0, 0, 0, 0);
		}

		float rotAngle = (float) Math.acos(dot);
		AVector3f rotAxis = AVector3f.forward.cross(forwardVector).normalize();
		return createFromAxisAngle(rotAxis, rotAngle);
	}

	// just in case you need that function also
	public static Quaternion createFromAxisAngle(AVector3f axis, float angle) {
		float halfAngle = angle * .5f;
		float s = (float) Math.sin(halfAngle);
		return new Quaternion((float) Math.cos(halfAngle), axis.x * s, axis.y * s, axis.z * s);
	}

	private float calcPointLight(AVector3f lightSource, AVector3f normal, AVector3f fragPos) {
		AVector3f lightDir = lightSource.substract(fragPos).normalize();

		// diffuse shading
		float diff = Math.max(normal.dot(lightDir), 0.0f);

		// attenuation
		float constant = 1f;
		float linear = 0.09f;
		float quadratic = 0.032f;
		float distance = lightSource.substract(fragPos).length();
		float attenuation = 1.0f / (constant + linear * distance + quadratic * (distance * distance));

		// combine results
		float ambientLight = 0.05f;
		float diffuseLight = 0.6f;

		float ambient = ambientLight;
		float diffuse = diffuseLight * diff;

		ambient *= attenuation;
		diffuse *= attenuation;

		return ambient + diffuse;
	}

	public void renderStar(MatrixStack ms, String texture, AVector3f pos, float scale, AVector3f color, float alpha) {
		RenderSystem.disableFog();
		RenderSystem.disableAlphaTest();
		RenderSystem.enableBlend();
		RenderSystem.enableTexture();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		Minecraft mc = Minecraft.getInstance();

		ms.pushPose();

		AVector3f v = new AVector3f(mc.gameRenderer.getMainCamera().getPosition());
		AVector3f p = new AVector3f((pos.x - v.x) / scale, (pos.y - v.y) / scale, (pos.z - v.z) / scale);

		ms.translate(p.x, p.y, p.z);

		float size = 1f;

		Matrix4f matrix = ms.last().pose();

		float b = 1f;
		Quaternion q = lookAt(v, p);

		ms.mulPose(q);

		VertexFormat format = DefaultVertexFormats.POSITION_TEX_COLOR;
		Minecraft.getInstance().textureManager.bind(new ResourceLocation(AlteranCommon.modId, texture));
		bufferbuilder.begin(GL11.GL_QUADS, format);
		bufferbuilder.vertex(matrix, -size, size, 0).uv(0f, 0f).color(color.x, color.y, color.z, alpha).endVertex();
		bufferbuilder.vertex(matrix, size, size, 0).uv(1f, 0f).color(color.x, color.y, color.z, alpha).endVertex();
		bufferbuilder.vertex(matrix, size, -size, 0).uv(1f, 1f).color(color.x, color.y, color.z, alpha).endVertex();
		bufferbuilder.vertex(matrix, -size, -size, 0).uv(0f, 1f).color(color.x, color.y, color.z, alpha).endVertex();
		tessellator.end();

		ms.popPose();

		RenderSystem.enableTexture();
		RenderSystem.enableBlend();
		RenderSystem.enableAlphaTest();
		RenderSystem.enableFog();
	}

	private void renderPlanet(MatrixStack ms, float x, float y, float z, float scale, boolean isEmittingLight, boolean hasTexture, String texture) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		Minecraft mc = Minecraft.getInstance();

		RenderSystem.disableFog();
		RenderSystem.disableAlphaTest();
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);


		ClientPlayerEntity player = mc.player;

		ms.pushPose();

		Matrix4f matrix = ms.last().pose();

		Vector3d v = mc.gameRenderer.getMainCamera().getPosition();

		ms.translate((x - v.x) / scale, (y - v.y) / scale, (z - v.z) / scale);

		RenderSystem.disableBlend();

		Minecraft.getInstance().textureManager.bind(new ResourceLocation(AlteranCommon.modId, texture));
		OBJModel model = ModelLoader.getModel(ModelLoader.getModelResource("sphere.obj"));

		VertexFormat format = hasTexture ? DefaultVertexFormats.POSITION_TEX_COLOR : DefaultVertexFormats.POSITION_COLOR;

		if (!hasTexture) {
			RenderSystem.disableTexture();
		}

		for (int i = 0; i < model.vertices.length / 9; i++) {
			float x1 = model.vertices[i * 9];
			float y1 = model.vertices[i * 9 + 1];
			float z1 = model.vertices[i * 9 + 2];

			AVector3f v1 = new AVector3f(x1, y1, z1);

			float x2 = model.vertices[i * 9 + 3];
			float y2 = model.vertices[i * 9 + 4];
			float z2 = model.vertices[i * 9 + 5];

			AVector3f v2 = new AVector3f(x2, y2, z2);

			float x3 = model.vertices[i * 9 + 6];
			float y3 = model.vertices[i * 9 + 7];
			float z3 = model.vertices[i * 9 + 8];

			AVector3f v3 = new AVector3f(x3, y3, z3);

			float texU1 = model.textureCoords[i * 6];
			float texV1 = model.textureCoords[i * 6 + 1];

			float texU2 = model.textureCoords[i * 6 + 2];
			float texV2 = model.textureCoords[i * 6 + 3];

			float texU3 = model.textureCoords[i * 6 + 4];
			float texV3 = model.textureCoords[i * 6 + 5];

			float brightness;

			if (isEmittingLight) {
				brightness = 1;
			} else {
				AVector3f normal = v2.substract(v1).cross(v3.substract(v1)).normalize();

				//				for (AVector3f lightSource : lightSources) {
				//					brightness += calcPointLight(lightSource, normal, v1);
				//				}

				brightness = Math.min(Math.max(normal.dot(lightSources.get(0)), 0), 1f);
			}

			bufferbuilder.begin(GL11.GL_POLYGON, format);
			bufferbuilder.vertex(matrix, x1, y1, z1).uv(texU1, texV1).color(brightness, brightness, brightness, 1f).endVertex();
			bufferbuilder.vertex(matrix, x2, y2, z2).uv(texU2, texV2).color(brightness, brightness, brightness, 1f).endVertex();
			bufferbuilder.vertex(matrix, x3, y3, z3).uv(texU3, texV3).color(brightness, brightness, brightness, 1f).endVertex();
			tessellator.end();
		}


		ms.popPose();

		RenderSystem.enableTexture();
		RenderSystem.enableBlend();
		RenderSystem.enableAlphaTest();
		RenderSystem.enableFog();
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
		float brightness = 0.9f;
		//RenderSystem.color4f(brightness, brightness, brightness, brightness);
		starVBO.bind();
		vertexFormat.setupBufferState(0L);
		starVBO.draw(matrixStack.last().pose(), 7);
		VertexBuffer.unbind();
		vertexFormat.clearBufferState();
		//RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableTexture();
	}

	private static float randBetween(float min, float max) {
		Random r = new Random();
		return r.nextFloat() * (max - min) + min;
	}

	private static void uploadStarData(BufferBuilder bufferBuilder, int numberOfStars) {
		Random random = new Random();
		bufferBuilder.begin(7, skyVertexFormat);

		for (int i = 0; i < numberOfStars; ++i) {
			// probably frequency
			double c0 = 2f;
			double c1 = 0.8f;
			double d0 = random.nextFloat() * c0 - c1;
			double d1 = random.nextFloat() * c0 - c1;
			double d2 = random.nextFloat() * c0 - c1;
			//size
			double max = 0.2f;
			double min = 0.05f;
			double d3 = random.nextFloat() * (max - min) + min;
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
					bufferBuilder.vertex(d5 + d25, d6 + d23, d7 + d26).color(randBetween(0.6f, 1f), randBetween(0.6f, 1f), randBetween(0.6f, 1f), randBetween(0.7f, 1f)).endVertex();
				}
			}
		}
	}

}