package com.forgeessentials.commands.player;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.WorldUtil;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;

public class CommandFly extends FEcmdModuleCommands {
	@Override
	public boolean canConsoleUseCommand() {
		return false;
	}

	@Override
	public String getCommandName() {
		return "fly";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/fly [true|false] Toggle flight mode.";
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.OP;
	}

	@Override
	public void processCommandPlayer(EntityPlayerMP player, String[] args) {
		if (args.length == 0) {
			if (!player.capabilities.allowFlying) {
				player.capabilities.allowFlying = true;
			} else {
				player.capabilities.allowFlying = false;
			}
		} else {
			player.capabilities.allowFlying = Boolean.parseBoolean(args[0]);
		}
		if (!player.onGround) {
			player.capabilities.isFlying = player.capabilities.allowFlying;
		}
		if (!player.capabilities.allowFlying) {
			WorldUtil.placeInWorld(player);
		}
		player.sendPlayerAbilities();
		ChatOutputHandler.chatNotification(player,
				"Flying " + (player.capabilities.allowFlying ? "enabled" : "disabled"));
	}

}
