package alteran.commands;

import alteran.AlteranCapabilities;
import alteran.capabilities.PlayerDataCapability;
import alteran.common.AlteranCommon;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

public class CommandSetMomentum implements Command<CommandSource> {
  private static final CommandSetMomentum CMD = new CommandSetMomentum();

  public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
    return Commands.literal("momentum").requires(cs -> cs.hasPermission(1)).executes(CMD);
  }

  @Override
  public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
    ServerPlayerEntity entity = context.getSource().getPlayerOrException();
    PlayerDataCapability cap = entity.getCapability(AlteranCapabilities.PLAYER_DATA).orElse(null);

    cap.clearMomentum();

    return 0;
  }
}
