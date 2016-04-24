package com.forgeessentials.chat.command;

import com.forgeessentials.chat.irc.IrcHandler;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

public class CommandIrcBot extends ForgeEssentialsCommandBase {

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public String getCommandName() {
		return "ircbot";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/ircbot [reconnect|disconnect] Connect or disconnect the IRC server bot.";
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.OP;
	}

	@Override
	public String getPermissionNode() {
		return "fe.chat.ircbot";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("reconnect") || args[0].equalsIgnoreCase("connect")) {
				IrcHandler.getInstance().connect();
			} else if (args[0].equalsIgnoreCase("disconnect")) {
				IrcHandler.getInstance().disconnect();
			}
		} else {
			ChatOutputHandler.sendMessage(sender,
					"IRC bot is " + (IrcHandler.getInstance().isConnected() ? "online" : "offline"));
		}
	}

}
