package com.forgeessentials.worldborder.effect;

import com.forgeessentials.commons.output.LoggingHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;

/**
 * Expected syntax: <interval> <damage>
 *
 * Use 20 for damage if you want to kill him, interval is always in seconds.
 */
public class EffectDamage extends WorldBorderEffect {

	private static int INTERVAL;

	private int damage = 1;

	@Override
	public String getSyntax() {
		return "<interval> <damage>";
	}

	@Override
	public boolean provideArguments(String[] args) {
		if (args.length < 2) {
			return false;
		}
		INTERVAL = Integer.parseInt(args[0]);
		damage = Integer.parseInt(args[1]);
		return true;
	}

	@Override
	public void tick(WorldBorder border, EntityPlayerMP player) {
		try {
			PlayerInfo pi = PlayerInfo.get(player);
			if (pi.checkTimeout(this.getClass().getName())) {
				player.attackEntityFrom(DamageSource.outOfWorld, damage);
				pi.startTimeout(this.getClass().getName(), INTERVAL * 1000);
			}
		} catch (NullPointerException npe) {
			LoggingHandler.felog.error("Error getting player Info");
		}

	}

	@Override
	public String toString() {
		return "damage trigger: " + triggerDistance + "interval: " + INTERVAL + " damage: " + damage;
	}

}
