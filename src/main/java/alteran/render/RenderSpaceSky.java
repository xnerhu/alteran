package alteran.render;


import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.ISkyRenderHandler;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;

public class RenderSpaceSky implements ISkyRenderHandler {

	private static RenderSpaceSky INSTANCE = null;

	// player distance for transitions
	private static final double PLANET_FAR = 1786.0D;
	private static final double PLANET_APPROACHING = 512.0D;
	private static final double PLANET_ORBIT = 128.0D;

	// render distance for objects
	private static final double BOX_RENDER_RANGE = 100.0D;

	// call lists
	private static VertexBuffer vboStars;
	private static final VertexFormat vertexFormatStars = DefaultVertexFormats.POSITION_COLOR;
	private static float starBrightness = 0.0F;
	private static final float ALPHA_TOLERANCE = 1.0F / 256.0F;

	private static final VertexFormat vertexFormatPlanes = DefaultVertexFormats.POSITION_COLOR;
	//	private static final VertexBuffer vboUpperPlane;
	//	private static final VertexBuffer vboLowerPlane;

	static {
		//		// pre-generate sky objects
		//		final Tessellator tessellator = Tessellator.getInstance();
		//		final BufferBuilder vertexBuffer = tessellator.getBuilder();
		//
		//		vboUpperPlane = new VertexBuffer(vertexFormatPlanes);
		//		final int stepSize = 64;
		//		final int nbSteps = 256 / stepSize + 2;
		//		float y = 16.0F;
		//		for (int x = -stepSize * nbSteps; x <= stepSize * nbSteps; x += stepSize) {
		//			for (int z = -stepSize * nbSteps; z <= stepSize * nbSteps; z += stepSize) {
		//				vertexBuffer.begin(GL11.GL_QUADS, vertexFormatPlanes);
		//				vertexBuffer.vertex(x, y, z).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
		//				vertexBuffer.vertex(x + stepSize, y, z).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
		//				vertexBuffer.vertex(x + stepSize, y, z + stepSize).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
		//				vertexBuffer.vertex(x, y, z + stepSize).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
		//			}
		//		}
		//		vertexBuffer.end();
		//		vboUpperPlane.upload(vertexBuffer);
		//
		//		vboLowerPlane = new VertexBuffer(vertexFormatPlanes);
		//		y = -16.0F;
		//		vertexBuffer.begin(GL11.GL_QUADS, vertexFormatPlanes);
		//		for (int x = -stepSize * nbSteps; x <= stepSize * nbSteps; x += stepSize) {
		//			for (int z = -stepSize * nbSteps; z <= stepSize * nbSteps; z += stepSize) {
		//				vertexBuffer.vertex(x + stepSize, y, z).color(0.30F, 0.30F, 0.30F, 1.00F).endVertex();
		//				vertexBuffer.vertex(x, y, z).color(0.30F, 0.30F, 0.30F, 1.00F).endVertex();
		//				vertexBuffer.vertex(x, y, z + stepSize).color(0.30F, 0.30F, 0.30F, 1.00F).endVertex();
		//				vertexBuffer.vertex(x + stepSize, y, z + stepSize).color(0.30F, 0.30F, 0.30F, 1.00F).endVertex();
		//			}
		//		}
		//		vertexBuffer.end();
		//		vboUpperPlane.upload(vertexBuffer);
	}

	// private final ResourceLocation textureStar = new ResourceLocation("warpdrive:textures/celestial/star_yellow.png");
	// private final ResourceLocation texturePlanet = new ResourceLocation("warpdrive:textures/celestial/planet_green.png");

	public static RenderSpaceSky getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new RenderSpaceSky();
		}
		return INSTANCE;
	}

	@Override
	public void render(final int ticks, final float partialTicks, @Nonnull final MatrixStack matrixStack,
										 @Nonnull final ClientWorld world, @Nonnull final Minecraft mc) {
		assert mc.player != null;
		final Vector3d vec3Player = mc.player.getEyePosition(partialTicks);

		final Tessellator tessellator = Tessellator.getInstance();

		RenderSystem.disableTexture();
		RenderSystem.depthMask(false);

		// draw sky box
		//		if (celestialObject != null
		//			&& celestialObject.boxTextures != null
		//			&& celestialObject.boxTextures.length > 0) {
		//			renderSkyBox(tessellator, celestialObject.boxTextures, celestialObject.boxBrightness, celestialObject.boxRepeat);
		//		}

		// compute global alpha
		final float alphaBase = 1.0F; // - world.getRainStrength(partialTicks);

		// draw stars
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		RenderSystem.disableAlphaTest();
		float starBrightness = 0.2F;
		starBrightness = world.getStarBrightness(partialTicks);
		renderStars_cached(matrixStack, alphaBase * 1);

		// enable texture with alpha blending
		RenderSystem.enableTexture();
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.disableAlphaTest();

		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();
		RenderSystem.enableFog();

		RenderSystem.enableTexture();
		RenderSystem.depthMask(true);
	}

	// renderSkyBox is loosely inspired by vanilla sky rendering in The End dimension (RenderGlobal::renderSkyEnd)
	private static void renderSkyBox(@Nonnull final Tessellator tessellator, @Nonnull final ResourceLocation[] textureSkyBox, final float brightness,
																	 final int countTextureRepeat) {
		final BufferBuilder bufferbuilder = tessellator.getBuilder();
		final float maxUV = countTextureRepeat * 1.0F;

		RenderSystem.disableFog();
		RenderSystem.disableAlphaTest();
		RenderSystem.enableTexture();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);

		// bottom
		Minecraft.getInstance().getTextureManager().bind(textureSkyBox[0]);
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
		bufferbuilder.vertex(-BOX_RENDER_RANGE, -BOX_RENDER_RANGE, -BOX_RENDER_RANGE).uv(0.0F, 0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(-BOX_RENDER_RANGE, -BOX_RENDER_RANGE, BOX_RENDER_RANGE).uv(0.0F, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(BOX_RENDER_RANGE, -BOX_RENDER_RANGE, BOX_RENDER_RANGE).uv(maxUV, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(BOX_RENDER_RANGE, -BOX_RENDER_RANGE, -BOX_RENDER_RANGE).uv(maxUV, 0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();
		tessellator.end();

		// front
		if (textureSkyBox.length > 1) {
			Minecraft.getInstance().getTextureManager().bind(textureSkyBox[1]);
		}
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
		bufferbuilder.vertex(-BOX_RENDER_RANGE, BOX_RENDER_RANGE, -BOX_RENDER_RANGE).uv(0.0F, 0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(-BOX_RENDER_RANGE, -BOX_RENDER_RANGE, -BOX_RENDER_RANGE).uv(0.0F, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(BOX_RENDER_RANGE, -BOX_RENDER_RANGE, -BOX_RENDER_RANGE).uv(maxUV, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(BOX_RENDER_RANGE, BOX_RENDER_RANGE, -BOX_RENDER_RANGE).uv(maxUV, 0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();
		tessellator.end();

		// back
		if (textureSkyBox.length > 1) {
			Minecraft.getInstance().getTextureManager().bind(textureSkyBox[2]);
		}
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
		bufferbuilder.vertex(BOX_RENDER_RANGE, BOX_RENDER_RANGE, BOX_RENDER_RANGE).uv(0.0F, 0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(BOX_RENDER_RANGE, -BOX_RENDER_RANGE, BOX_RENDER_RANGE).uv(0.0F, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(-BOX_RENDER_RANGE, -BOX_RENDER_RANGE, BOX_RENDER_RANGE).uv(maxUV, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(-BOX_RENDER_RANGE, BOX_RENDER_RANGE, BOX_RENDER_RANGE).uv(maxUV, 0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();
		tessellator.end();

		// top
		if (textureSkyBox.length > 1) {
			Minecraft.getInstance().getTextureManager().bind(textureSkyBox[3]);
		}
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
		bufferbuilder.vertex(-BOX_RENDER_RANGE, BOX_RENDER_RANGE, -BOX_RENDER_RANGE).uv(0.0F, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(BOX_RENDER_RANGE, BOX_RENDER_RANGE, -BOX_RENDER_RANGE).uv(maxUV, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(BOX_RENDER_RANGE, BOX_RENDER_RANGE, BOX_RENDER_RANGE).uv(maxUV, 0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(-BOX_RENDER_RANGE, BOX_RENDER_RANGE, BOX_RENDER_RANGE).uv(0.0F, 0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();
		tessellator.end();

		// right
		if (textureSkyBox.length > 1) {
			Minecraft.getInstance().getTextureManager().bind(textureSkyBox[4]);
		}
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
		bufferbuilder.vertex(BOX_RENDER_RANGE, BOX_RENDER_RANGE, -BOX_RENDER_RANGE).uv(0.0F, 0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(BOX_RENDER_RANGE, -BOX_RENDER_RANGE, -BOX_RENDER_RANGE).uv(0.0F, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(BOX_RENDER_RANGE, -BOX_RENDER_RANGE, BOX_RENDER_RANGE).uv(maxUV, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(BOX_RENDER_RANGE, BOX_RENDER_RANGE, BOX_RENDER_RANGE).uv(maxUV, 0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();
		tessellator.end();

		// left
		if (textureSkyBox.length > 1) {
			Minecraft.getInstance().getTextureManager().bind(textureSkyBox[5]);
		}
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
		bufferbuilder.vertex(-BOX_RENDER_RANGE, BOX_RENDER_RANGE, BOX_RENDER_RANGE).uv(0.0F, 0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(-BOX_RENDER_RANGE, -BOX_RENDER_RANGE, BOX_RENDER_RANGE).uv(0.0F, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(-BOX_RENDER_RANGE, -BOX_RENDER_RANGE, -BOX_RENDER_RANGE).uv(maxUV, maxUV).color(brightness, brightness, brightness, 1.0F).endVertex();
		bufferbuilder.vertex(-BOX_RENDER_RANGE, BOX_RENDER_RANGE, -BOX_RENDER_RANGE).uv(maxUV, 0.0F).color(brightness, brightness, brightness, 1.0F).endVertex();
		tessellator.end();

		RenderSystem.enableTexture();
		RenderSystem.enableAlphaTest();
	}

	private BufferBuilder renderStars_direct(final float brightness) {
		final Random rand = new Random(10842L);
		final boolean hasMoreStars = rand.nextBoolean() || rand.nextBoolean();
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder vertexBuffer = tessellator.getBuilder();

		final double renderRangeMax = 10.0D;
		for (int indexStars = 0; indexStars < (hasMoreStars ? 20000 : 2000); indexStars++) {
			double randomX;
			double randomY;
			double randomZ;
			double randomLength;
			do {
				randomX = rand.nextDouble() * 2.0D - 1.0D;
				randomY = rand.nextDouble() * 2.0D - 1.0D;
				randomZ = rand.nextDouble() * 2.0D - 1.0D;
				randomLength = randomX * randomX + randomY * randomY + randomZ * randomZ;
			} while (randomLength >= 1.0D || randomLength <= 0.90D);

			final double renderSize = 0.020F + 0.0025F * Math.log(1.1D - rand.nextDouble());

			// forcing Z-order
			randomLength = 1.0D / Math.sqrt(randomLength);
			randomX *= randomLength;
			randomY *= randomLength;
			randomZ *= randomLength;

			// scaling
			final double x0 = randomX * renderRangeMax;
			final double y0 = randomY * renderRangeMax;
			final double z0 = randomZ * renderRangeMax;

			// angles
			final double angleH = Math.atan2(randomX, randomZ);
			final double angleV = Math.atan2(Math.sqrt(randomX * randomX + randomZ * randomZ), randomY);
			final double angleS = rand.nextDouble() * Math.PI * 2.0D;

			// colorization
			final int rgb = getStarColorRGB(rand);
			final float fRed = brightness * ((rgb >> 16) & 0xFF) / 255.0F;
			final float fGreen = brightness * ((rgb >> 8) & 0xFF) / 255.0F;
			final float fBlue = brightness * (rgb & 0xFF) / 255.0F;
			final float fAlpha = 1.0F;

			// pre-computations
			final double sinH = Math.sin(angleH);
			final double cosH = Math.cos(angleH);
			final double sinV = Math.sin(angleV);
			final double cosV = Math.cos(angleV);
			final double sinS = Math.sin(angleS);
			final double cosS = Math.cos(angleS);

			vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			for (int indexVertex = 0; indexVertex < 4; indexVertex++) {
				final double valZero = 0.0D;
				final double offset1 = ((indexVertex & 2) - 1) * renderSize;
				final double offset2 = ((indexVertex + 1 & 2) - 1) * renderSize;
				final double valV = offset1 * cosS - offset2 * sinS;
				final double valH = offset2 * cosS + offset1 * sinS;
				final double y1 = valV * sinV + valZero * cosV;
				final double valD = valZero * sinV - valV * cosV;
				final double x1 = valD * sinH - valH * cosH;
				final double z1 = valH * sinH + valD * cosH;
				vertexBuffer.vertex(x0 + x1, y0 + y1, z0 + z1).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			}
			tessellator.end();
		}

		return vertexBuffer;
	}

	private void renderStars_cached(@Nonnull final MatrixStack matrixStack, final float brightness) {
		if (Math.abs(starBrightness - brightness) > ALPHA_TOLERANCE) {
			starBrightness = brightness;
			RenderSystem.pushMatrix();
			final Tessellator tessellator = Tessellator.getInstance();
			renderStars_direct(brightness);
			final BufferBuilder vertexBuffer = tessellator.getBuilder();
			vboStars = new VertexBuffer(DefaultVertexFormats.POSITION_COLOR);
			matrixStack.pushPose();
			vboStars.upload(vertexBuffer);
			RenderSystem.popMatrix();
		}
		vboStars.bind();
		vertexFormatStars.setupBufferState(0L);
		vboStars.draw(matrixStack.last().pose(), 7);
		VertexBuffer.unbind();
		vertexFormatStars.clearBufferState();
	}

	// colorization loosely inspired from Hertzsprung-Russell diagram
	// (we're using it for non-star objects too, so yeah...)
	private static int getStarColorRGB(@Nonnull final Random rand) {
		final double colorType = rand.nextDouble();
		final float hue;
		final float saturation;
		float brightness = 1.0F - 0.8F * rand.nextFloat();  // distance effect

		if (colorType <= 0.08D) {// 8% light blue (young star)
			hue = 0.48F + 0.08F * rand.nextFloat();
			saturation = 0.18F + 0.22F * rand.nextFloat();

		} else if (colorType <= 0.24D) {// 22% pure white (early age)
			hue = 0.126F + 0.040F * rand.nextFloat();
			saturation = 0.00F + 0.15F * rand.nextFloat();
			brightness *= 0.95F;

		} else if (colorType <= 0.45D) {// 21% yellow white
			hue = 0.126F + 0.040F * rand.nextFloat();
			saturation = 0.15F + 0.15F * rand.nextFloat();
			brightness *= 0.90F;

		} else if (colorType <= 0.67D) {// 22% yellow
			hue = 0.126F + 0.040F * rand.nextFloat();
			saturation = 0.80F + 0.15F * rand.nextFloat();
			if (rand.nextInt(3) == 1) {// yellow giant
				brightness *= 0.90F;
			} else {
				brightness *= 0.85F;
			}

		} else if (colorType <= 0.92D) {// 25% orange
			hue = 0.055F + 0.055F * rand.nextFloat();
			saturation = 0.85F + 0.15F * rand.nextFloat();
			if (rand.nextInt(3) == 1) {// (orange giant)
				brightness *= 0.90F;
			} else {
				brightness *= 0.80F;
			}

		} else {// red (mostly giants)
			hue = 0.95F + 0.05F * rand.nextFloat();
			if (rand.nextInt(3) == 1) {// (red giant)
				saturation = 0.80F + 0.20F * rand.nextFloat();
				brightness *= 0.95F;
			} else {
				saturation = 0.70F + 0.20F * rand.nextFloat();
				brightness *= 0.65F;
			}
		}
		return Color.HSBtoRGB(hue, saturation, brightness);
	}
}