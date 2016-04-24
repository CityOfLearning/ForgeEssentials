package com.forgeessentials.util.selections;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;

public class CommandDeselect extends ForgeEssentialsCommandBase {

	@Override
	public boolean canConsoleUseCommand() {
		return false;
	}

	@Override
	public String getCommandName() {
		return "/fedesel";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {

		return "//fedesel Deselects the selection";
	}

	@Override
	public String[] getDefaultAliases() {
		return new String[] { "/fedeselect", "/deselect", "/sel" };
	}

	@Override
	public PermissionLevel getPermissionLevel() {

		return PermissionLevel.TRUE;
	}

	@Override
	public String getPermissionNode() {
		return "fe.core.pos.deselect";
	}

	@Override
	public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException {
		PlayerInfo info;
		try {
			info = PlayerInfo.get(sender.getPersistentID());

			info.setSel1(null);
			info.setSel2(null);
			SelectionHandler.sendUpdate(sender);
			ChatOutputHandler.chatConfirmation(sender, "Selection cleared.");
		} catch (Exception e) {
			LoggingHandler.felog.error("Error getting player Info");
		}
	}
}
