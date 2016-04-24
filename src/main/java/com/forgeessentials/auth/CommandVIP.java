package com.forgeessentials.auth;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

public class CommandVIP extends ForgeEssentialsCommandBase {

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public String getCommandName() {
		return "vip";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {

		return "/vip [add|remove} <player> Adds or removes a player from the VIP list";
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.OP;
	}

	@Override
	public String getPermissionNode() {
		return "fe.auth.vipcmd";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if ((args.length >= 2) && args[0].equalsIgnoreCase("add")) {
			APIRegistry.perms.setPlayerPermission(UserIdent.get(args[1], sender), "fe.auth.vip", true);
		} else if ((args.length >= 2) && args[0].equalsIgnoreCase("remove")) {
			APIRegistry.perms.setPlayerPermission(UserIdent.get(args[1], sender), "fe.auth.vip", false);
		}
	}

}
