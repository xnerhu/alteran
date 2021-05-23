package alteran;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.ICloudRenderHandler;
import net.minecraftforge.client.ISkyRenderHandler;

public class YellowStarSystemRenderInfo extends DimensionRenderInfo {
  public YellowStarSystemRenderInfo() {
    super(-1, true, DimensionRenderInfo.FogType.NORMAL, false, false);
  }

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
    return (int ticks, float partialTicks, MatrixStack ms, ClientWorld world, Minecraft mc, double viewEntityX, double viewEntityY, double viewEntityZ) -> {

    };
  }

  @Override
  public float[] getSunriseColor(float p_230492_1_, float p_230492_2_) {
    return null;
  }
}
