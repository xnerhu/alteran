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
  public PlayerEntity player;

  public Vector3d momentum = new Vector3d(0, 0, 0);

  public PlayerDataCapability(PlayerEntity player) {
    this.player = player;

  }

  public CompoundNBT serializeNBT() {
    CompoundNBT tag = new CompoundNBT();
    tag.putDouble("momentum_x", this.momentum.x);
    tag.putDouble("momentum_y", this.momentum.y);
    tag.putDouble("momentum_z", this.momentum.z);

    return tag;
  }

  public void deserializeNBT(CompoundNBT nbt) {
    if (nbt.contains("momentum_x")) {
      double x = nbt.getDouble("momentum_x");
      double y = nbt.getDouble("momentum_y");
      double z = nbt.getDouble("momentum_z");
      this.momentum = new Vector3d(x, y, z);
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
