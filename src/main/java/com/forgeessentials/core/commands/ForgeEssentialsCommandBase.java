package com.forgeessentials.core.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.output.LoggingHandler;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.permission.PermissionManager;
import net.minecraftforge.permission.PermissionObject;

public abstract class ForgeEssentialsCommandBase extends CommandBase implements PermissionObject {

	public static List<String> completePlayername(String arg) {
		List<String> arraylist = new ArrayList<>();
		for (UserIdent s2 : APIRegistry.perms.getServerZone().getKnownPlayers()) {
			if (doesStringStartWith(arg, s2.getUsernameOrUuid())) {
				arraylist.add(s2.getUsernameOrUuid());
			}
		}
		return arraylist;
	}

	// ------------------------------------------------------------
	// Command alias

	public static List<String> getListOfStringsMatchingLastWord(String arg, Collection<String> possibleMatches) {
		List<String> arraylist = new ArrayList<>();
		for (String s2 : possibleMatches) {
			if (doesStringStartWith(arg, s2)) {
				arraylist.add(s2);
			}
		}
		return arraylist;
	}

	public static List<String> getListOfStringsMatchingLastWord(String arg, String... possibleMatches) {
		List<String> arraylist = new ArrayList<>();
		int i = possibleMatches.length;
		for (int j = 0; j < i; ++j) {
			String s2 = possibleMatches[j];
			if (doesStringStartWith(arg, s2)) {
				arraylist.add(s2);
			}
		}
		return arraylist;
	}

	/**
	 * Parse double with support for relative values.
	 *
	 * @param string
	 * @param relativeStart
	 * @return
	 */
	public static double parseDouble(String string, double relativeStart) throws NumberInvalidException {
		if (string.startsWith("~")) {
			string = string.substring(1);
			return relativeStart + parseInt(string);
		} else {
			return parseInt(string);
		}
	}

	/**
	 * Parse int with support for relative int.
	 *
	 * @param string
	 * @param relativeStart
	 * @return
	 * @throws NumberInvalidException
	 */
	public static int parseInt(String string, int relativeStart) throws NumberInvalidException {
		if (string.startsWith("~")) {
			string = string.substring(1);
			return relativeStart + parseInt(string);
		} else {
			return parseInt(string);
		}
	}

	public List<String> aliases = new ArrayList<>();

	// ------------------------------------------------------------
	// Command processing

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		if (!canConsoleUseCommand() && !(sender instanceof EntityPlayer)) {
			return false;
		}
		return true;
	}

	public abstract boolean canConsoleUseCommand();

	/**
	 * Check, if the sender has permissions to use this command
	 */
	public boolean checkCommandPermission(ICommandSender sender) {
		if ((getPermissionNode() == null) || getPermissionNode().isEmpty()) {
			return true;
		}
		return PermissionManager.checkPermission(sender, this, getPermissionNode());
	}

	@SuppressWarnings("unchecked")
	public void deregister() {
		if (MinecraftServer.getServer() == null) {
			return;
		}
		CommandHandler cmdHandler = (CommandHandler) MinecraftServer.getServer().getCommandManager();
		Map<String, ICommand> commandMap = cmdHandler.getCommands();
		Set<ICommand> commandSet = (Set<ICommand>) ReflectionHelper.getPrivateValue(CommandHandler.class, cmdHandler,
				"field_71561_b", "commandSet");

		String commandName = getCommandName();
		List<String> commandAliases = getCommandAliases();
		commandSet.remove(this);
		if (commandName != null) {
			commandMap.remove(commandName);
		}
		if ((commandAliases != null) && !commandAliases.isEmpty()) {
			for (String alias : commandAliases) {
				commandMap.remove(alias);
			}
		}
	}

	// ------------------------------------------------------------
	// Command usage

	@Override
	public List<String> getCommandAliases() {
		return aliases;
	}

	@Override
	public abstract String getCommandUsage(ICommandSender sender);

	// ------------------------------------------------------------
	// Permissions

	/**
	 * Returns a list of default aliases, that will be added to the
	 * configuration on first run
	 */
	public String[] getDefaultAliases() {
		return new String[] {};
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (sender instanceof EntityPlayerMP) {
			processCommandPlayer((EntityPlayerMP) sender, args);
		} else if (sender instanceof CommandBlockLogic) {
			processCommandBlock((CommandBlockLogic) sender, args);
		} else {
			processCommandConsole(sender, args);
		}
	}

	public void processCommandBlock(CommandBlockLogic block, String[] args) throws CommandException {
		processCommandConsole(block, args);
	}

	public void processCommandConsole(ICommandSender sender, String[] args) throws CommandException {
		throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
	}

	// ------------------------------------------------------------
	// Utilities

	public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException {
		throw new TranslatedCommandException("This command cannot be used as player");
	}

	/*
	 * public static List<String> getListOfStringsMatchingLastWord(String[]
	 * args, Collection<?> possibleMatches) { return
	 * getListOfStringsMatchingLastWord(args[args.length - 1], possibleMatches);
	 * }
	 */

	/**
	 * Registers this command and it's permission node
	 */
	public void register() {
		if (MinecraftServer.getServer() == null) {
			return;
		}

		Map<?, ?> commandMap = ((CommandHandler) MinecraftServer.getServer().getCommandManager()).getCommands();
		if (commandMap.containsKey(getCommandName())) {
			LoggingHandler.felog.error(String.format("Command %s registered twice", getCommandName()));
		}

		if ((getCommandAliases() != null) && !getCommandAliases().isEmpty()) {
			for (String alias : getCommandAliases()) {
				if ((alias != null) && commandMap.containsKey(alias)) {
					LoggingHandler.felog.error(
							String.format("Command alias %s of command %s registered twice", alias, getCommandName()));
				}
			}
		}

		((CommandHandler) MinecraftServer.getServer().getCommandManager()).registerCommand(this);
		registerExtraPermissions();
	}

	/**
	 * Registers additional permissions
	 */
	public void registerExtraPermissions() {
		/* do nothing */
	}

	public void setAliases(List<String> aliases) {
		this.aliases = aliases;
	}

	public void setAliases(String[] aliases) {
		if (aliases == null) {
			setAliases(new ArrayList<String>());
		} else {
			setAliases(Arrays.asList(aliases));
		}
	}

}
