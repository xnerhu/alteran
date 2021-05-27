package alteran.commands;

import alteran.RenderSpaceSky;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class CommandReloadSpaceSkyRenderer implements Command<CommandSource> {
	private static final CommandReloadSpaceSkyRenderer CMD = new CommandReloadSpaceSkyRenderer();

	public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
		return Commands.literal("reload-space-sky-renderer").requires(cs -> cs.hasPermission(1)).executes(CMD);
	}

	@Override
	public int run(CommandContext<CommandSource> context) {
		RenderSpaceSky.getInstance().reload();
		return 0;
	}
}
