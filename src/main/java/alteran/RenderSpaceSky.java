package alteran;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.Random;

import alteran.common.AlteranCommon;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ISkyRenderHandler;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;

//@OnlyIn(Dist.CLIENT)
public class RenderSpaceSky implements ISkyRenderHandler {
  public static final ResourceLocation SKY_TEXTURE = new ResourceLocation(AlteranCommon.modId, "textures/celestial/custom_sky.png");

  private static final ResourceLocation NEBULA_1 = new ResourceLocation(AlteranCommon.modId, "textures/sky/nebula_2.png");
  private static final ResourceLocation NEBULA_2 = new ResourceLocation(AlteranCommon.modId, "textures/sky/nebula_3.png");
  private static final ResourceLocation HORIZON = new ResourceLocation(AlteranCommon.modId, "textures/sky/nebula_1.png");
  private static final ResourceLocation FOG = new ResourceLocation(AlteranCommon.modId, "textures/sky/fog.png");
  private static final ResourceLocation STARS = new ResourceLocation(AlteranCommon.modId, "textures/sky/stars.png");

  private static VertexBuffer stars1;
  private static VertexBuffer stars2;
  private static VertexBuffer stars3;
  private static VertexBuffer stars4;
  private static VertexBuffer nebulas1;
  private static VertexBuffer nebulas2;
  private static VertexBuffer horizon;
  private static VertexBuffer fog;
  private static Vector3f axis1;
  private static Vector3f axis2;
  private static Vector3f axis3;
  private static Vector3f axis4;
  private static float time;
  private static float time2;
  private static float time3;
  private static float blind02;
  private static float blind06;
  private static boolean directOpenGL = false; // Unused

  public static void onInit() {
    initStars();

    Random random = new Random(131);
    axis1 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
    axis2 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
    axis3 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
    axis4 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
    axis1.normalize();
    axis2.normalize();
    axis3.normalize();
    axis4.normalize();
  }

  @Override
  public void render(int ticks, float partialTicks, MatrixStack ms, ClientWorld world, Minecraft mc) {
    time = (ticks % 360000) * 0.000017453292F;
    time2 = time * 2;
    time3 = time * 3;

    TextureManager textureManager = mc.getTextureManager();

    //      FogRenderer.resetFog();
    RenderSystem.enableTexture();

    //if (directOpenGL)
    {
      GL11.glEnable(GL11.GL_ALPHA_TEST);
      GL11.glAlphaFunc(516, 0.0F);
      GL11.glEnable(GL11.GL_BLEND);
      RenderSystem.depthMask(false);
    }
			/*else
			{
				RenderSystem.enableAlphaTest();
				RenderSystem.alphaFunc(516, 0.0F);
				RenderSystem.enableBlend();
			}*/

    float blindA = 1F;
    blind02 = blindA * 0.2F;
    blind06 = blindA * 0.6F;

    if (blindA > 0) {
      ms.pushPose();
      ms.last().normal().mul(new Quaternion(0, time, 0, false));
      textureManager.bind(HORIZON);
      renderBuffer(ms, horizon, DefaultVertexFormats.POSITION_TEX, 0.77F, 0.31F, 0.73F, 0.7F * blindA);
      ms.popPose();

      ms.pushPose();
      ms.last().normal().mul(new Quaternion(0, -time, 0, false));
      textureManager.bind(NEBULA_1);
      renderBuffer(ms, nebulas1, DefaultVertexFormats.POSITION_TEX, 0.77F, 0.31F, 0.73F, blind02);
      ms.popPose();

      ms.pushPose();
      ms.last().normal().mul(new Quaternion(0, time2, 0, false));
      textureManager.bind(NEBULA_2);
      renderBuffer(ms, nebulas2, DefaultVertexFormats.POSITION_TEX, 0.77F, 0.31F, 0.73F, blind02);
      ms.popPose();

      textureManager.bind(STARS);

      ms.pushPose();
      ms.last().normal().mul(axis3.rotation(time));
      renderBuffer(ms, stars3, DefaultVertexFormats.POSITION_TEX, 0.77F, 0.31F, 0.73F, blind06);
      ms.popPose();

      ms.pushPose();
      ms.last().normal().mul(axis4.rotation(time2));
      renderBuffer(ms, stars4, DefaultVertexFormats.POSITION_TEX, 1F, 1F, 1F, blind06);
      ms.popPose();
    }

    //    float a = (BackgroundInfo.fog - 1F);
    //    if (a > 0) {
    //      if (a > 1) a = 1;
    //      textureManager.bindTexture(FOG);
    //      renderBuffer(matrices, fog, DefaultVertexFormats.POSITION_TEX, BackgroundInfo.red, BackgroundInfo.green, BackgroundInfo.blue, a);
    //    }

    RenderSystem.disableTexture();

    if (blindA > 0) {
      ms.pushPose();
      ms.last().normal().mul(axis1.rotation(time3));
      renderBuffer(ms, stars1, DefaultVertexFormats.POSITION, 1, 1, 1, blind06);
      ms.popPose();

      ms.pushPose();
      ms.last().normal().mul(axis2.rotation(time2));
      renderBuffer(ms, stars2, DefaultVertexFormats.POSITION, 0.95F, 0.64F, 0.93F, blind06);
      ms.popPose();
    }

    RenderSystem.enableTexture();
    RenderSystem.depthMask(true);

    //    info.cancel();
  }

  private void renderBuffer(MatrixStack matrixStackIn, VertexBuffer buffer, VertexFormat format, float r, float g, float b, float a) {
    RenderSystem.color4f(r, g, b, a);
    buffer.bind();

    format.setupBufferState(0L);
    buffer.draw(matrixStackIn.last().pose(), 7);
    VertexBuffer.unbind();
    format.clearBufferState();
  }


  //  @Override
  //  public void render(int ticks, float partialTicks, MatrixStack ms, ClientWorld world, Minecraft mc) {
  //    RenderSystem.disableAlphaTest();
  //    RenderSystem.enableBlend();
  //    RenderSystem.defaultBlendFunc();
  //    RenderSystem.depthMask(false);
  //
  //    mc.getTextureManager().bind(SKY_TEXTURE);
  //    //    this.textureManager.bindTexture(END_SKY_TEXTURES);
  //    Tessellator tessellator = Tessellator.getInstance();
  //    BufferBuilder bufferbuilder = tessellator.getBuilder();
  //
  //    for (int i = 0; i < 6; ++i) {
  //      ms.pushPose();
  //      if (i == 1) {
  //        ms.mulPose(Vector3f.XP.rotationDegrees(90.0F));
  //      }
  //
  //      if (i == 2) {
  //        ms.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
  //      }
  //
  //      if (i == 3) {
  //        ms.mulPose(Vector3f.XP.rotationDegrees(180.0F));
  //      }
  //
  //      if (i == 4) {
  //        ms.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
  //      }
  //
  //      if (i == 5) {
  //        ms.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
  //      }
  //
  //      Matrix4f matrix4f = ms.last().pose();
  //      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
  //      bufferbuilder.vertex(matrix4f, -500.0F, -500.0F, -500.0F).uv(0.0F, 0.0F).endVertex();
  //      bufferbuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).uv(0.0F, 1.0F).endVertex();
  //      bufferbuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).uv(1.0F, 1.0F).endVertex();
  //      bufferbuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).uv(1.0F, 0.0F).endVertex();
  //      tessellator.end();
  //      ms.popPose();
  //    }
  //
  //    RenderSystem.depthMask(true);
  //    RenderSystem.enableTexture();
  //    RenderSystem.disableBlend();
  //    RenderSystem.enableAlphaTest();
  //  }

  private static void initStars() {
    BufferBuilder buffer = Tessellator.getInstance().getBuilder();
    stars1 = buildBufferStars(buffer, stars1, 0.1, 0.30, 3500, 41315);
    stars2 = buildBufferStars(buffer, stars2, 0.1, 0.35, 2000, 35151);
    stars3 = buildBufferUVStars(buffer, stars3, 0.4, 1.2, 1000, 61354);
    stars4 = buildBufferUVStars(buffer, stars4, 0.4, 1.2, 1000, 61355);
    nebulas1 = buildBufferFarFog(buffer, nebulas1, 40, 60, 30, 11515);
    nebulas2 = buildBufferFarFog(buffer, nebulas2, 40, 60, 10, 14151);
    horizon = buildBufferHorizon(buffer, horizon);
    fog = buildBufferFog(buffer, fog);
  }

  private static VertexBuffer buildBufferStars(BufferBuilder bufferBuilder, VertexBuffer buffer, double minSize, double maxSize, int count, long seed) {
    if (buffer != null) {
      buffer.close();
    }

    buffer = new VertexBuffer(DefaultVertexFormats.POSITION);
    makeStars(bufferBuilder, minSize, maxSize, count, seed);
    bufferBuilder.end();
    buffer.upload(bufferBuilder);

    return buffer;
  }

  private static VertexBuffer buildBufferUVStars(BufferBuilder bufferBuilder, VertexBuffer buffer, double minSize, double maxSize, int count, long seed) {
    if (buffer != null) {
      buffer.close();
    }

    buffer = new VertexBuffer(DefaultVertexFormats.POSITION_TEX);
    makeUVStars(bufferBuilder, minSize, maxSize, count, seed);
    bufferBuilder.end();
    buffer.upload(bufferBuilder);

    return buffer;
  }

  private static VertexBuffer buildBufferFarFog(BufferBuilder bufferBuilder, VertexBuffer buffer, double minSize, double maxSize, int count, long seed) {
    if (buffer != null) {
      buffer.close();
    }

    buffer = new VertexBuffer(DefaultVertexFormats.POSITION_TEX);
    makeFarFog(bufferBuilder, minSize, maxSize, count, seed);
    bufferBuilder.end();
    buffer.upload(bufferBuilder);

    return buffer;
  }

  private static VertexBuffer buildBufferHorizon(BufferBuilder bufferBuilder, VertexBuffer buffer) {
    if (buffer != null) {
      buffer.close();
    }

    buffer = new VertexBuffer(DefaultVertexFormats.POSITION_TEX);
    makeCylinder(bufferBuilder, 16, 50, 100);
    bufferBuilder.end();
    buffer.upload(bufferBuilder);

    return buffer;
  }

  private static VertexBuffer buildBufferFog(BufferBuilder bufferBuilder, VertexBuffer buffer) {
    if (buffer != null) {
      buffer.close();
    }

    buffer = new VertexBuffer(DefaultVertexFormats.POSITION_TEX);
    makeCylinder(bufferBuilder, 16, 50, 70);
    bufferBuilder.end();
    buffer.upload(bufferBuilder);

    return buffer;
  }

  private static void makeStars(BufferBuilder buffer, double minSize, double maxSize, int count, long seed) {
    Random random = new Random(seed);
    buffer.begin(7, DefaultVertexFormats.POSITION);

    for (int i = 0; i < count; ++i) {
      double posX = random.nextDouble() * 2.0 - 1.0;
      double posY = random.nextDouble() * 2.0 - 1.0;
      double posZ = random.nextDouble() * 2.0 - 1.0;
      double size = MathHelper.nextDouble(random, minSize, maxSize);
      double length = posX * posX + posY * posY + posZ * posZ;
      if (length < 1.0 && length > 0.001) {
        length = 1.0 / Math.sqrt(length);
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

        for (int v = 0; v < 4; ++v) {
          double x = (double) ((v & 2) - 1) * size;
          double y = (double) ((v + 1 & 2) - 1) * size;
          double aa = x * u - y * t;
          double ab = y * u + x * t;
          double ad = aa * q + 0.0 * r;
          double ae = 0.0 * q - aa * r;
          double af = ae * n - ab * o;
          double ah = ab * n + ae * o;
          buffer.vertex(j + af, k + ad, l + ah).endVertex();
        }
      }
    }
  }

  private static void makeUVStars(BufferBuilder buffer, double minSize, double maxSize, int count, long seed) {
    Random random = new Random(seed);
    buffer.begin(7, DefaultVertexFormats.POSITION_TEX);

    for (int i = 0; i < count; ++i) {
      double posX = random.nextDouble() * 2.0 - 1.0;
      double posY = random.nextDouble() * 2.0 - 1.0;
      double posZ = random.nextDouble() * 2.0 - 1.0;
      double size = randRange(minSize, maxSize, random);
      double length = posX * posX + posY * posY + posZ * posZ;
      if (length < 1.0 && length > 0.001) {
        length = 1.0 / Math.sqrt(length);
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
        float minV = random.nextInt(4) / 4F;
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
          float texV = (((pos + 1) >> 1) & 1) / 4F + minV;
          pos++;
          buffer.vertex(j + af, k + ad, l + ah).uv(texU, texV).endVertex();
        }
      }
    }
  }

  private static void makeFarFog(BufferBuilder buffer, double minSize, double maxSize, int count, long seed) {
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

  private static void makeCylinder(BufferBuilder buffer, int segments, double height, double radius) {
    buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
    for (int i = 0; i < segments; i++) {
      double a1 = (double) i * Math.PI * 2.0 / (double) segments;
      double a2 = (double) (i + 1) * Math.PI * 2.0 / (double) segments;
      double px1 = Math.sin(a1) * radius;
      double pz1 = Math.cos(a1) * radius;
      double px2 = Math.sin(a2) * radius;
      double pz2 = Math.cos(a2) * radius;

      float u0 = (float) i / (float) segments;
      float u1 = (float) (i + 1) / (float) segments;

      buffer.vertex(px1, -height, pz1).uv(u0, 0).endVertex();
      buffer.vertex(px1, height, pz1).uv(u0, 1).endVertex();
      buffer.vertex(px2, height, pz2).uv(u1, 1).endVertex();
      buffer.vertex(px2, -height, pz2).uv(u1, 0).endVertex();
    }
  }

  public static double randRange(double min, double max, Random random) {
    return min + random.nextDouble() * (max - min);
  }
}