package com.forgeessentials.worldborder.effect;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.scripting.ScriptArguments;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.LoggingHandler;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/**
 * Expected syntax: <interval> <command>
 */
public class EffectCommand extends WorldBorderEffect {

	public String command = "say @player Go back while you still can!";

	public int interval = 0;

	@Override
	public void activate(WorldBorder border, EntityPlayerMP player) {
		if (interval <= 0) {
			doEffect(player);
		}
	}

	public void doEffect(EntityPlayerMP player) {
		String cmd = ScriptArguments.processSafe(command, player);
		MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), cmd);
	}

	@Override
	public String getSyntax() {
		return "<interval> <command>";
	}

	@Override
	public boolean provideArguments(String[] args) {
		if (args.length < 2) {
			return false;
		}
		interval = Integer.parseInt(args[0]);
		command = StringUtils.join(ServerUtil.dropFirst(args), " ");
		return true;
	}

	@Override
	public void tick(WorldBorder border, EntityPlayerMP player) {
		if (interval <= 0) {
			return;
		}
		try {
			PlayerInfo pi = PlayerInfo.get(player);
			if (pi.checkTimeout(this.getClass().getName())) {
				doEffect(player);
				pi.startTimeout(this.getClass().getName(), interval * 1000);
			}
		} catch (NullPointerException npe) {
			LoggingHandler.felog.error("Error getting player Info");
		}
	}

	@Override
	public String toString() {
		return "command trigger: " + triggerDistance + "interval: " + interval + " command: " + command;
	}
}
