package com.forgeessentials.worldborder.effect;

import com.forgeessentials.commons.output.LoggingHandler;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/**
 * Expected syntax: <interval> (in seconds)
 */
public class EffectKick extends WorldBorderEffect {

	private int TIMEOUT = 0;

	@Override
	public void activate(WorldBorder border, EntityPlayerMP player) {
		if (!MinecraftServer.getServer().isDedicatedServer()) {
			LoggingHandler.felog.warn("[WorldBorder] Kick effect is not supported on integrated servers!");
			return;
		}
		ChatOutputHandler.chatError(player, Translator
				.format("You have %d seconds to return inside the world border, or you will get kicked!", TIMEOUT));
		PlayerInfo pi = PlayerInfo.get(player);
		pi.startTimeout(this.getClass().getName(), TIMEOUT * 1000);
	}

	@Override
	public String getSyntax() {
		return "<interval>";
	}

	@Override
	public boolean provideArguments(String[] args) {
		if (args.length < 1) {
			return false;
		}
		TIMEOUT = Integer.parseInt(args[0]);
		return true;
	}

	@Override
	public void tick(WorldBorder border, EntityPlayerMP player) {
		try {
			PlayerInfo pi = PlayerInfo.get(player);
			if (pi.checkTimeout(this.getClass().getName())) {
				player.playerNetServerHandler.kickPlayerFromServer("You left the world border");
				// For safety restart the timeout
				pi.startTimeout(this.getClass().getName(), TIMEOUT);
			}
		} catch (NullPointerException npe) {
			LoggingHandler.felog.error("Error getting player Info");
		}
	}

	@Override
	public String toString() {
		return "kick trigger: " + triggerDistance + "interval: " + TIMEOUT;
	}

}
