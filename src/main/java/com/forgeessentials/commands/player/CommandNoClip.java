package com.forgeessentials.commands.player;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.Packet5Noclip;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.WorldUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;

public class CommandNoClip extends ForgeEssentialsCommandBase {

	public static void checkClip(EntityPlayer player) {
		if (player.noClip) {
			if (!player.capabilities.isFlying) {
				player.noClip = false;
				WorldUtil.placeInWorld(player);
				if (!player.worldObj.isRemote) {
					NetworkUtils.netHandler.sendTo(new Packet5Noclip(player.noClip), (EntityPlayerMP) player);
					ChatOutputHandler.chatNotification(player,
							"NoClip auto-disabled: the targeted player is not flying");
				}
			}
		}
	}

	@Override
	public boolean canConsoleUseCommand() {
		return false;
	}

	@Override
	public String getCommandName() {
		return "fenoclip";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/noclip [true/false]";
	}

	@Override
	public String[] getDefaultAliases() {
		return new String[] { "noclip" };
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.OP;
	}

	@Override
	public String getPermissionNode() {
		return ModuleCommands.PERM + ".noclip";
	}

	@Override
	public void processCommandPlayer(EntityPlayerMP player, String[] args) throws CommandException {
		try {
			if (!PlayerInfo.get(player).getHasFEClient()) {
				ChatOutputHandler.chatError(player, "You need the FE client addon to use this command.");
				ChatOutputHandler.chatError(player,
						"Please visit https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/FE-Client-mod for more information.");
				return;
			}
		} catch (Exception e) {
			LoggingHandler.felog.error("Error getting player Info");
			return;
		}

		if (!player.capabilities.isFlying && !player.noClip) {
			throw new TranslatedCommandException("You must be flying.");
		}

		if (args.length == 0) {
			if (!player.noClip) {
				player.noClip = true;
			} else {
				player.noClip = false;
			}
		} else {
			player.noClip = Boolean.parseBoolean(args[0]);
		}
		if (!player.noClip) {
			WorldUtil.placeInWorld(player);
		}

		NetworkUtils.netHandler.sendTo(new Packet5Noclip(player.noClip), player);
		ChatOutputHandler.chatConfirmation(player, "Noclip " + (player.noClip ? "enabled" : "disabled"));
	}

}
