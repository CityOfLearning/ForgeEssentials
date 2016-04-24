package com.forgeessentials.permissions.commands;

import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.util.CommandParserArgs;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

public class CommandPermissions extends ParserCommandBase {

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public final String getCommandName() {
		return "feperm";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/feperm Configure FE permissions.";
	}

	@Override
	public String[] getDefaultAliases() {
		return new String[] { "perm", "fep", "p" };
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.TRUE;
	}

	@Override
	public String getPermissionNode() {
		return PermissionCommandParser.PERM;
	}

	@Override
	public void parse(CommandParserArgs arguments) throws CommandException {
		PermissionCommandParser.parseMain(arguments);
	}

}
