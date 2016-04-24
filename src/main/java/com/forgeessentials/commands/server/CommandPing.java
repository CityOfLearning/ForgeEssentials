package com.forgeessentials.commands.server;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permission.PermissionLevel;

public class CommandPing extends FEcmdModuleCommands implements ConfigurableCommand {
	public String response = "Pong! %time";

	@Override
	public boolean canConsoleUseCommand() {
		return false;
	}

	@Override
	public String getCommandName() {
		return "ping";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/ping Ping the server.";
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.TRUE;
	}

	@Override
	public void loadConfig(Configuration config, String category) {
		response = config.get(category, "response", "Pong! %time").getString();
	}

	@Override
	public void loadData() {
		/* do nothing */
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) throws CommandException {
		ChatOutputHandler.chatNotification(sender, response.replaceAll("%time", ""));
	}

	@Override
	public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException {
		ChatOutputHandler.chatNotification(sender, response.replaceAll("%time", sender.ping + "ms."));
	}

}
