package com.forgeessentials.commands.player;

import java.util.List;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.permission.PermissionLevel;

public class CommandLocate extends FEcmdModuleCommands {
	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args,
					FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		}
		return null;
	}

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public String getCommandName() {
		return "locate";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/locate <player> Locates a player.";
	}

	@Override
	public String[] getDefaultAliases() {
		return new String[] { "gps", "loc", "playerinfo" };
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.OP;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length != 1) {
			throw new TranslatedCommandException(getCommandUsage(sender));
		}

		EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
		if (player == null) {
			throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
		}

		WorldPoint point = new WorldPoint(player);
		ChatOutputHandler.chatConfirmation(sender,
				Translator.format("%s is at %d, %d, %d in dim %d with gamemode %s", //
						player.getName(), point.getX(), point.getY(), point.getZ(), point.getDimension(), //
						player.theItemInWorldManager.getGameType().getName()));
	}
}
