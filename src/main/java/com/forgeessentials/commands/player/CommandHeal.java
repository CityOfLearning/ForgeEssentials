package com.forgeessentials.commands.player;

import java.util.List;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

public class CommandHeal extends ForgeEssentialsCommandBase {

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
		return "feheal";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		if (sender instanceof EntityPlayer) {
			return "/heal <player> Heal yourself or other players (if you have permission).";
		} else {
			return "/heal <player> Heal a player.";
		}
	}

	@Override
	public String[] getDefaultAliases() {
		return new String[] { "heal" };
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.OP;
	}

	@Override
	public String getPermissionNode() {
		return ModuleCommands.PERM + ".heal";
	}

	public void heal(EntityPlayer target) {
		float toHealBy = target.getMaxHealth() - target.getHealth();
		target.heal(toHealBy);
		target.extinguish();
		target.getFoodStats().addStats(20, 1.0F);
		ChatOutputHandler.chatConfirmation(target, "You were healed.");
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 1) {
			EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
			if (player != null) {
				heal(player);
			} else {
				throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
			}
		} else {
			throw new TranslatedCommandException(getCommandUsage(sender));
		}
	}

	@Override
	public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException {
		if (args.length == 0) {
			heal(sender);
		} else if ((args.length == 1) && PermissionManager.checkPermission(sender, getPermissionNode() + ".others")) {
			EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
			if (player != null) {
				heal(player);
			} else {
				ChatOutputHandler.chatError(sender,
						String.format("Player %s does not exist, or is not online.", args[0]));
			}
		} else {
			throw new TranslatedCommandException(getCommandUsage(sender));
		}
	}

	@Override
	public void registerExtraPermissions() {
		APIRegistry.perms.registerPermission(getPermissionNode() + ".others", PermissionLevel.OP);
	}

}
