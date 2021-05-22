package alteran.commands;

import alteran.common.AlteranCommon;
import alteran.components.dimensions.DimensionId;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.function.Function;

public class TeleportationTools {
  public static void teleport(PlayerEntity player, DimensionId dimension, double destX, double destY, double destZ, @Nullable Direction direction) {
    DimensionId oldId = DimensionId.fromWorld(player.level);
    float rotationYaw = 0f; // player.;
    float rotationPitch = 0f; //  player.rotationPitch;
    if (!oldId.equals(dimension)) {
      teleportToDimension(player, dimension, destX, destY, destZ);
    }

    //    if (direction != null) {
    //      fixOrientation(player, destX, destY, destZ, direction);
    //    } else {
    //      player.rotationYaw = rotationYaw;
    //      player.rotationPitch = rotationPitch;
    //    }

    player.setPos(destX, destY, destZ);
  }

  public static void teleportToDimension(PlayerEntity player, DimensionId dimension, final double x, final double y, final double z) {
    final ServerWorld world = dimension.loadWorld(player.level);
    if (world == null) {
      AlteranCommon.logger.error("Something went wrong teleporting to dimension " + dimension.getName());
    } else {
      player.changeDimension(world, new ITeleporter() {
        public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
          entity.setLevel(world);
          world.addDuringPortalTeleport((ServerPlayerEntity) entity);
          entity.moveTo(x, y, z);
          entity.setPos(x, y, z);
          return entity;
        }
      });
    }
  }
}
