package com.forgeessentials.worldborder.effect;

import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.LoggingHandler;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Expected syntax: <interval>
 */
public class EffectSmite extends WorldBorderEffect {

	public int interval;

	@Override
	public void activate(WorldBorder border, EntityPlayerMP player) {
		if (interval <= 0) {
			doEffect(player);
		}
	}

	public void doEffect(EntityPlayerMP player) {
		player.worldObj
				.addWeatherEffect(new EntityLightningBolt(player.worldObj, player.posX, player.posY, player.posZ));
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
		interval = Integer.parseInt(args[0]);
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
		return "smite interval: " + interval + " smite";
	}

}
