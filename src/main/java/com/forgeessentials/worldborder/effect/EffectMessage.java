package com.forgeessentials.worldborder.effect;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.commons.output.LoggingHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Expected syntax: <interval> <message>
 */
public class EffectMessage extends WorldBorderEffect {

	public String message = "You left the worldborder. Please return!";

	public int interval = 6000;

	@Override
	public void activate(WorldBorder border, EntityPlayerMP player) {
		if (interval <= 0) {
			doEffect(player);
		}
	}

	public void doEffect(EntityPlayerMP player) {
		ChatOutputHandler.chatError(player, ModuleChat.processChatReplacements(player, message));
	}

	@Override
	public String getSyntax() {
		return "<interval> <message>";
	}

	@Override
	public boolean provideArguments(String[] args) {
		if (args.length < 2) {
			return false;
		}
		interval = Integer.parseInt(args[0]);
		message = StringUtils.join(ServerUtil.dropFirst(args), " ");
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
		return "message trigger: " + triggerDistance + "interval: " + interval + " message: " + message;
	}

}
