package com.forgeessentials.playerlogger.event;

import java.util.Date;

import javax.persistence.EntityManager;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action01Block;
import com.forgeessentials.playerlogger.entity.Action01Block.ActionBlockType;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class LogEventInteract extends PlayerLoggerEvent<PlayerInteractEvent> {

	public LogEventInteract(PlayerInteractEvent event) {
		super(event);
	}

	@Override
	public void process(EntityManager em) {
		if (event.action != Action.LEFT_CLICK_BLOCK) {
			return;
		}
		Action01Block action = new Action01Block();
		action.time = new Date();
		action.player = getPlayer(event.entityPlayer);
		action.world = getWorld(event.world.provider.getDimensionId());
		// action.block = getBlock(block);
		// action.metadata = metadata;
		action.type = event.action == Action.LEFT_CLICK_BLOCK ? ActionBlockType.USE_LEFT : ActionBlockType.USE_RIGHT;
		action.x = event.pos.getX();
		action.y = event.pos.getY();
		action.z = event.pos.getZ();
		em.persist(action);
	}

}