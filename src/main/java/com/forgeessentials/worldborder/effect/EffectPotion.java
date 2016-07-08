package com.forgeessentials.worldborder.effect;

import com.forgeessentials.data.v2.Loadable;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.LoggingHandler;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;

/**
 * Expected syntax: <interval> <effect> <seconds> <amplifier>
 */
public class EffectPotion extends WorldBorderEffect implements Loadable {

	public int id;

	public int duration;

	public int modifier;

	public int interval;

	public EffectPotion() {
	}

	@Override
	public void activate(WorldBorder border, EntityPlayerMP player) {
		if (interval <= 0) {
			player.addPotionEffect(new PotionEffect(id, duration, modifier));
		}
	}

	@Override
	public void afterLoad() {
		if ((Integer) id == null) {
			id = 9;
			duration = 5;
			modifier = 0;
		}
	}

	@Override
	public String getSyntax() {
		return "<interval> <effect> <seconds> <amplifier>";
	}

	@Override
	public boolean provideArguments(String[] args) {
		if (args.length < 4) {
			return false;
		}
		interval = Integer.parseInt(args[0]);
		id = Integer.parseInt(args[1]);
		duration = Integer.parseInt(args[2]);
		modifier = Integer.parseInt(args[3]);

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
				player.addPotionEffect(new PotionEffect(id, duration, modifier));
				pi.startTimeout(this.getClass().getName(), interval * 1000);
			}
		} catch (NullPointerException npe) {
			LoggingHandler.felog.error("Error getting player Info");
		}
	}

	@Override
	public String toString() {
		return String.format("potion interval: %d1 id: %d2 duration: %d3 amplifier: %d4", interval, id, duration,
				modifier);
	}

}
