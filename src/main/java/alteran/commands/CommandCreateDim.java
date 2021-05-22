package alteran.commands;

import alteran.common.AlteranCommon;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.SharedConstants;
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
    //    String name = context.getArgument("name", String.class);
    //    String descriptor = context.getArgument("descriptor", String.class);
    //    long seed = context.getArgument("seed", Long.class);
    //    String error = DimensionManager.get().createDimension(context.getSource().getWorld(), name, seed, descriptor);
    //    if (error != null) {
    //      context.getSource().sendFeedback(new StringTextComponent(TextFormatting.RED + error), true);
    //    }
    AlteranCommon.logger.debug("XDddddddddddd");

    return 0;
  }
}
