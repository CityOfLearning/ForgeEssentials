package com.forgeessentials.util.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public class EventCancelledException extends Exception {
	private static final long serialVersionUID = 6106472655247525969L;

	public static void checkedPost(Event e) throws EventCancelledException {
		checkedPost(e, MinecraftForge.EVENT_BUS);
	}

	public static void checkedPost(Event e, EventBus eventBus) throws EventCancelledException {
		if (eventBus.post(e)) {
			throw new EventCancelledException(e);
		}
	}

	private Event event;

	public EventCancelledException(Event event) {
		this.event = event;
	}

	public Event getEvent() {
		return event;
	}

}
