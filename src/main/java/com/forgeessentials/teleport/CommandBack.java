package com.forgeessentials.teleport;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.LoggingHandler;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

public class CommandBack extends ForgeEssentialsCommandBase {

	@Override
	public boolean canConsoleUseCommand() {
		return false;
	}

	@Override
	public String getCommandName() {
		return "feback";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/back: Teleport you to your last death or teleport location.";
	}

	@Override
	public String[] getDefaultAliases() {
		return new String[] { "back" };
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.TRUE;
	}

	@Override
	public String getPermissionNode() {
		return TeleportModule.PERM_BACK;
	}

	@Override
	public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException {
		try {
			PlayerInfo pi = PlayerInfo.get(sender.getPersistentID());

			WarpPoint point = null;
			if (PermissionManager.checkPermission(sender, TeleportModule.PERM_BACK_ONDEATH)) {
				point = pi.getLastDeathLocation();
			}
			if (point == null) {
				point = pi.getLastTeleportOrigin();
			}
			if (point == null) {
				throw new TranslatedCommandException("You have nowhere to get back to");
			}

			TeleportHelper.teleport(sender, point);
		} catch (Exception e) {
			LoggingHandler.felog.error("Error getting player Info");
		}
	}

}
