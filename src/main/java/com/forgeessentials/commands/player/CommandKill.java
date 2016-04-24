package com.forgeessentials.commands.player;

import java.util.List;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

public class CommandKill extends FEcmdModuleCommands {

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args,
					FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		} else {
			return null;
		}
	}

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public String getCommandName() {
		return "fekill";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/kill <player> Commit suicide or kill other players (with special permission).";
	}

	@Override
	public String[] getDefaultAliases() {
		return new String[] { "kill" };
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.OP;
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) throws CommandException {
		if (args.length >= 1) {
			EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
			if (player != null) {
				player.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
				ChatOutputHandler.chatError(player, Translator.translate("You were killed. You probably deserved it."));
			} else {
				throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
			}
		} else {
			throw new TranslatedCommandException(getCommandUsage(sender));
		}
	}

	@Override
	public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException {
		if ((args.length >= 1) && PermissionManager.checkPermission(sender, getPermissionNode() + ".others")) {
			EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
			if (player != null) {
				player.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
				ChatOutputHandler.chatError(player, Translator.translate("You were killed. You probably deserved it."));
			} else {
				throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
			}
		} else {
			sender.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
			ChatOutputHandler.chatError(sender, Translator.translate("You were killed. You probably deserved it."));
		}
	}

	@Override
	public void registerExtraPermissions() {
		APIRegistry.perms.registerPermission(getPermissionNode() + ".others", PermissionLevel.OP);
	}

}
