package alteran.commands;

import alteran.common.AlteranCommon;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class AlteranCommands {
  public static void register(CommandDispatcher<CommandSource> dispatcher) {
    LiteralCommandNode<CommandSource> commands = dispatcher.register(Commands.literal(AlteranCommon.modId).then(CommandCreateDim.register(dispatcher)).then(CommandTpDim.register(dispatcher)));

    dispatcher.register(Commands.literal("dim").redirect(commands));
  }
}
