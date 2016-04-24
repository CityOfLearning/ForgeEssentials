package com.forgeessentials.chat.irc;

import java.util.Collection;

import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public interface IrcCommand {

	public static abstract class IrcCommandParser implements IrcCommand {

		public static class IrcCommandParserArgs extends CommandParserArgs {

			public final IrcCommand ircCommand;

			public IrcCommandParserArgs(IrcCommand command, String[] args, ICommandSender sender) {
				super(null, args, sender);
				ircCommand = command;
			}

			@Override
			public void error(String message, Object... args) {
				if (!isTabCompletion) {
					ChatOutputHandler.chatError(sender, "Error: " + Translator.format(message, args));
				}
			}

		}

		@Override
		public abstract String getCommandHelp();

		@Override
		public abstract Collection<String> getCommandNames();

		@Override
		public abstract String getCommandUsage();

		@Override
		public abstract boolean isAdminCommand();

		public abstract void parse(CommandParserArgs arguments) throws CommandException;

		@Override
		public void processCommand(ICommandSender sender, String[] args) throws CommandException {
			CommandParserArgs arguments = new IrcCommandParserArgs(null, args, sender);
			parse(arguments);
		}

	}

	public static final String COMMAND_CHAR = IrcHandler.COMMAND_CHAR;

	public String getCommandHelp();

	public Collection<String> getCommandNames();

	public String getCommandUsage();

	public boolean isAdminCommand();

	public void processCommand(ICommandSender sender, String[] args) throws CommandException;

}
