package com.forgeessentials.util.events;

import com.forgeessentials.api.permissions.Zone;

import net.minecraftforge.fml.common.eventhandler.Event;

public class ZoneEvent extends Event {

	public static class Create extends ZoneEvent {
		public Create(Zone created) {
			zone = created;
		}
	}

	public static class Delete extends ZoneEvent {
		public Delete(Zone toDelete) {
			zone = toDelete;
		}
	}

	protected static Zone zone;

	public Zone getZone() {
		return zone;
	}
}
