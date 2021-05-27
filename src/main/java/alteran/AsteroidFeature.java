package alteran;

import alteran.common.AlteranCommon;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

public class AsteroidFeature extends Feature<NoFeatureConfig> {
  public AsteroidFeature(Codec codec) {
    super(codec);
  }

  @Override
  public boolean place(ISeedReader world, ChunkGenerator generator, Random random, BlockPos pos, NoFeatureConfig config) {
    //    if (random.nextInt(4) != 0) return false;
    ChunkPos chunkPos = new ChunkPos(pos);
    int size = Math.max(7, random.nextInt(12));
    int halfSize = size / 2;
    final MetaBalls noise = new MetaBalls(size, random);
    final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
    for (int x = 0; x < 32; x++) {
      for (int z = 0; z < 32; z++) {
        for (int y = 0; y < 32; y++) {
          mutablePos.set(pos).move(x - halfSize, y - halfSize, z - halfSize);
          if (noise.noise(x - size, y - size, z - size) > 0.5f) {
            world.setBlock(mutablePos, Blocks.STONE.defaultBlockState(), 3);
          }
        }
      }
    }
    return true;
    //    for (int x = 0; x < 32; x++) {
    //      for (int z = 0; z < 32; z++) {
    //        for (int y = 0; y < 32; y++) {
    //          mutablePos.set(pos).move(x - 8, y, z - 8);
    //          if (noise.noise(x - pos.getX(), y - pos.getY(), z - pos.getZ()) > 0.5f)
    //            world.setBlock(mutablePos, Blocks.STONE.defaultBlockState(), 3);
    //        }
    //      }
    //    }
    //  }
    //    if ((chunkPos.x & 1) == 0 && (chunkPos.z & 1) == 0) {
    //
    //    }
    //    return true;
    //    BlockPos.Mutable blockpos$Mutable = new BlockPos.Mutable();
    //
    //    int radius = Math.max(3, random.nextInt(8));
    //
    //    if (radius % 2 != 0) {
    //      radius += 1;
    //    }
    //
    //    int posX = pos.getX();
    //    int posY = pos.getY();
    //    int posZ = pos.getZ();
    //
    //    for (int x = posX - radius; x < posX + radius; x++) {
    //      for (int y = posY - radius; y < posY + radius; y++) {
    //        for (int z = posZ - radius; z < posZ + radius; z++) {
    //          float formula = (float) (Math.pow(x - posX, 2) + Math.pow(y - posY, 2) + Math.pow(z - posZ, 2));
    //
    //          if (formula <= Math.pow(radius, 2)) {
    //            blockpos$Mutable.set(pos).move(x - posX, y - posY, z - posZ);
    //
    //            if (world.getBlockState(blockpos$Mutable).getBlock() == Blocks.AIR) {
    //              world.setBlock(blockpos$Mutable, Blocks.STONE.defaultBlockState(), 3);
    //            }
    //          }
    //        }
    //
    //      }
    //    }
    //    int radius = 5;

    //    for (int x = -radius; x < radius; x++) {
    //      for (int y = -radius; y < radius; y++) {
    //        for (int z = -radius; z < radius; z++) {
    //          if ((x * x + y * y + z * z) == radius * radius) {
    //            blockpos$Mutable.set(pos).move(x, y, z);
    //            if (world.getBlockState(blockpos$Mutable).getBlock() == Blocks.AIR) {
    //              world.setBlock(blockpos$Mutable, Blocks.STONE.defaultBlockState(), 3);
    //            }
    //          }
    //        }
    //      }
    //    }

    //    for (int x = -2; x <= 2; x++) {
    //      for (int y = -2; y <= 2; y++) {
    //        for (int z = -2; z <= 2; z++) {
    //          blockpos$Mutable.set(pos).move(x, y, z);
    //          if (world.getBlockState(blockpos$Mutable).getBlock() == Blocks.AIR) {
    //            world.setBlock(blockpos$Mutable, Blocks.STONE.defaultBlockState(), 3);
    //          }
    //        }
    //      }
    //    }

    //    return true;
  }
}
