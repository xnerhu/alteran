package alteran.dimensions;

import net.minecraft.world.World;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public abstract class AbstractWorldData<T extends AbstractWorldData<T>> extends WorldSavedData {
  protected AbstractWorldData(String name) {
    super(name);
  }

  public void save() {
    this.setDirty();
  }

  @Nonnull
  public static <T extends AbstractWorldData<T>> T getData(World world, Supplier<? extends T> supplier, String name) {
    if (world.isClientSide) {
      throw new RuntimeException("Don't access this client-side!");
    } else {
      DimensionSavedDataManager storage = WorldTools.getOverworld(world).getDataStorage();
      return storage.computeIfAbsent(supplier, name);
    }
  }
}