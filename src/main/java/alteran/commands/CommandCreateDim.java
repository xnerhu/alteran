package alteran.commands;

import alteran.components.space.SpaceSystemManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CommandCreateDim implements Command<CommandSource> {
  private static final CommandCreateDim CMD = new CommandCreateDim();

  public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
    return Commands.literal("create").requires(cs -> cs.hasPermission(1)).then(Commands.argument("name", StringArgumentType.word()).then(Commands.argument("seed", LongArgumentType.longArg())).executes(CMD));
  }

  @Override
  public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
    //    SharedConstants.developmentMode = true;


    String name = context.getArgument("name", String.class);
    long seed = context.getSource().getLevel().getSeed(); // context.getArgument("seed", Long.class);

    String error = SpaceSystemManager.get().createDimension(context.getSource().getLevel(), name, seed);
    if (error != null) {
      context.getSource().sendSuccess(new StringTextComponent(TextFormatting.RED + error), true);
    }

    return 0;
  }
}
