package com.forgeessentials.commands.player;

import java.util.List;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commons.output.LoggingHandler;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.util.BlockPos;
import net.minecraftforge.permission.PermissionLevel;

public class CommandSeen extends ForgeEssentialsCommandBase {

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		CommandParserArgs arguments = new CommandParserArgs(this, args, sender, true);
		try {
			parse(arguments);
		} catch (CommandException e) {
			return arguments.tabCompletion;
		}
		return arguments.tabCompletion;
	}

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public String getCommandName() {
		return "feseen";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/seen <player>: Check when a player has been last seen online";
	}

	@Override
	public String[] getDefaultAliases() {
		return new String[] { "seen" };
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.TRUE;
	}

	@Override
	public String getPermissionNode() {
		return ModuleCommands.PERM + ".seen";
	}

	public void parse(CommandParserArgs arguments) throws CommandException {
		if (arguments.isEmpty()) {
			throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
		}

		UserIdent player = arguments.parsePlayer(false, false);

		if (player.hasPlayer()) {
			arguments.confirm("Player %s is currently online", player.getUsernameOrUuid());
			return;
		}

		if (!player.hasUuid() || !PlayerInfo.exists(player.getUuid())) {
			throw new PlayerNotFoundException();
		}
		try {
			PlayerInfo pi = PlayerInfo.get(player.getUuid());
			long t = (System.currentTimeMillis() - pi.getLastLogout().getTime()) / 1000;
			arguments.confirm(Translator.format("Player %s was last seen %s ago", player.getUsernameOrUuid(),
					ChatOutputHandler.formatTimeDurationReadable(t, false)));
			PlayerInfo.discard(pi.ident.getUuid());
		} catch (Exception e) {
			LoggingHandler.felog.error("Error getting player Info");
		}
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		CommandParserArgs arguments = new CommandParserArgs(this, args, sender);
		parse(arguments);
	}

}
