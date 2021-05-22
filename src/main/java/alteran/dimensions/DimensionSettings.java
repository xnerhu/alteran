package alteran.dimensions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class DimensionSettings {
  public static final Codec<DimensionSettings> SETTINGS_CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.LONG.fieldOf("seed").forGetter(DimensionSettings::getSeed), Codec.STRING.fieldOf("data").forGetter(DimensionSettings::getData)).apply(instance, DimensionSettings::new));

  private final long seed;
  private final String data;
  private CompiledDescriptor compiledDescriptor;

  public DimensionSettings(long seed, String data) {
    this.seed = seed;
    this.data = data;
  }

  public long getSeed() {
    return seed;
  }

  public String getData() {
    return data;
  }

  public CompiledDescriptor getCompiledDescriptor() {
    if (compiledDescriptor == null) {
      DimensionDescriptor descriptor = new DimensionDescriptor();
      descriptor.read(getData());
      //      DimensionDescriptor randomizedDescriptor = new DimensionDescriptor();
      //      randomizedDescriptor.read(getRandomized());
      compiledDescriptor = new CompiledDescriptor();
      //      DescriptorError error = compiledDescriptor.compile(descriptor, randomizedDescriptor);
      //      if (!error.isOk()) {
      ////        RFToolsDim.setup.getLogger().error("Error compiling dimension descriptor: " + error.getMessage());
      //        //                throw new RuntimeException("Error compiling dimension descriptor: " + error.getMessage());
      //      }
      //      compiledDescriptor.complete();
    }
    return compiledDescriptor;
  }
}
