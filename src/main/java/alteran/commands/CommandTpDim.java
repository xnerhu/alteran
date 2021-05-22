package alteran.commands;

import alteran.common.AlteranCommon;
import alteran.dimensions.DimensionId;
import alteran.dimensions.DimensionManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SharedConstants;
import net.minecraft.world.World;

public class CommandTpDim implements Command<CommandSource> {
  private static final CommandTpDim CMD = new CommandTpDim();

  public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
    return Commands.literal("tp").requires(cs -> cs.hasPermission(1)).then(Commands.argument("name", StringArgumentType.string()).executes(CMD));
  }

  @Override
  public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
    //    SharedConstants.developmentMode = true;
    String name = context.getArgument("name", String.class);
    ServerPlayerEntity player = context.getSource().getPlayerOrException();
    double x = player.position().x();
    double z = player.position().z();
    World world = DimensionManager.get().getDimWorld(name);
    if (world == null) {
      AlteranCommon.logger.error("Can't find dimension '" + name + "'!");
      return 0;
    }

    DimensionId id = DimensionId.fromWorld(world);
    TeleportationTools.teleport(player, id, x, 200, z, Direction.NORTH);
    return 0;
  }
}
