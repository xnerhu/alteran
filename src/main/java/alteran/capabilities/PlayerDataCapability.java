package alteran.capabilities;

import alteran.AlteranCapabilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class PlayerDataCapability {
  private final static String TAG_MOMENTUM_X = "momentum_x";
  private final static String TAG_MOMENTUM_Y = "momentum_y";
  private final static String TAG_MOMENTUM_Z = "momentum_z";

  public PlayerEntity player;

  private Vector3d momentum = new Vector3d(0, 0, 0);

  public PlayerDataCapability(PlayerEntity player) {
    this.player = player;
  }

  public boolean hasMomentum() {
    return this.momentum != null;
  }

  public Vector3d getMomentum() {
    return this.momentum;
  }

  public void setMomentum(Vector3d momentum) {
    this.momentum = momentum;
  }

  public void clearMomentum() {
    this.momentum = null;
  }


  public CompoundNBT serializeNBT() {
    CompoundNBT tag = new CompoundNBT();

    if (this.momentum != null) {
      tag.putDouble(TAG_MOMENTUM_X, this.momentum.x);
      tag.putDouble(TAG_MOMENTUM_Y, this.momentum.y);
      tag.putDouble(TAG_MOMENTUM_Z, this.momentum.z);
    } else if (tag.contains(TAG_MOMENTUM_X)) {
      tag.remove(TAG_MOMENTUM_X);
      tag.remove(TAG_MOMENTUM_Y);
      tag.remove(TAG_MOMENTUM_Z);
    }

    return tag;
  }

  public void deserializeNBT(CompoundNBT nbt) {
    if (nbt.contains(TAG_MOMENTUM_X)) {
      double x = nbt.getDouble(TAG_MOMENTUM_X);
      double y = nbt.getDouble(TAG_MOMENTUM_Y);
      double z = nbt.getDouble(TAG_MOMENTUM_Z);

      this.momentum = new Vector3d(x, y, z);
    } else {
      this.momentum = null;
    }
  }

  public static class Storage implements Capability.IStorage<PlayerDataCapability> {
    @Override
    public INBT writeNBT(Capability<PlayerDataCapability> capability, PlayerDataCapability instance, Direction side) {
      return instance.serializeNBT();
    }

    @Override
    public void readNBT(Capability<PlayerDataCapability> capability, PlayerDataCapability instance, Direction side, INBT nbt) {
      if (nbt instanceof CompoundNBT) instance.deserializeNBT((CompoundNBT) nbt);
    }
  }

  public static class Provider implements ICapabilitySerializable<CompoundNBT> {
    PlayerDataCapability data;

    public Provider(PlayerDataCapability data) {
      this.data = data;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
      return cap == AlteranCapabilities.PLAYER_DATA ? (LazyOptional<T>) LazyOptional.of(() -> data) : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
      return data.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
      data.deserializeNBT(nbt);
    }

  }
}
