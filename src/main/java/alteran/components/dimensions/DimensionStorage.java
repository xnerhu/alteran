package alteran.components.dimensions;

import alteran.common.AlteranCommon;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class DimensionStorage extends AbstractWorldData<DimensionStorage> {
  private static final String NAME = "AlteranDimensions";

  private final Map<ResourceLocation, DimensionData> data = new HashMap<>();
  //  private final Map<DimensionDescriptor, DimensionData> dataByDescriptor = new HashMap<>();

  public DimensionStorage(String name) {
    super(name);
  }

  @Nonnull
  public static DimensionStorage get(World world) {
    return getData(world, () -> new DimensionStorage(NAME), NAME);
  }

  public DimensionData getData(ResourceLocation id) {
    return data.get(id);
  }

  //  public DimensionData getData(DimensionDescriptor descriptor) {
  //    return dataByDescriptor.get(descriptor);
  //  }

  public Map<ResourceLocation, DimensionData> getData() {
    return data;
  }

  // No error checking! It is assumed the caller checks before!
  public void register(DimensionData dd) {
    data.put(dd.getId(), dd);
    //    dataByDescriptor.put(dd.getDescriptor(), dd);
    this.setDirty();
  }

  public void forget(ResourceLocation key) {
    DimensionData dd = data.get(key);
    data.remove(key);
    if (dd != null) {
      //      dataByDescriptor.remove(dd.getDescriptor());
    }
    this.setDirty();
  }

  @Override
  public void load(CompoundNBT tag) {
    ListNBT dimensions = tag.getList("dimensions", Constants.NBT.TAG_COMPOUND);
    data.clear();
    //    dataByDescriptor.clear();
    for (INBT inbt : dimensions) {
      CompoundNBT dtag = (CompoundNBT) inbt;
      DimensionData dd = new DimensionData(dtag);
      //      AlteranCommon.logger.error("XDDDDDDDDDDDDDDDDDDDDDDDDDDDDD" + dd.getId().toString());
      data.put(dd.getId(), dd);
      //      dataByDescriptor.put(dd.getDescriptor(), dd);
    }
  }

  @Override
  public CompoundNBT save(CompoundNBT compound) {
    CompoundNBT tag = new CompoundNBT();

    ListNBT list = new ListNBT();
    for (Map.Entry<ResourceLocation, DimensionData> entry : data.entrySet()) {
      CompoundNBT dtag = new CompoundNBT();
      entry.getValue().write(dtag);
      list.add(dtag);
    }

    tag.put("dimensions", list);
    return tag;
  }
}
