package alteran.components.space;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SpaceSystemSettings {
  public static final Codec<SpaceSystemSettings> SETTINGS_CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.LONG.fieldOf("seed").forGetter(SpaceSystemSettings::getSeed), Codec.STRING.fieldOf("data").forGetter(SpaceSystemSettings::getData)).apply(instance, SpaceSystemSettings::new));

  private final long seed;
  private final String data;

  public SpaceSystemSettings(long seed, String data) {
    this.seed = seed;
    this.data = data;
  }

  public long getSeed() {
    return seed;
  }

  public String getData() {
    return data;
  }
}
