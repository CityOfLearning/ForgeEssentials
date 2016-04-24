package com.forgeessentials.core.misc;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.LoggingHandler;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TaskRegistry extends ServerEventHandler {

	public static interface TickTask {

		public boolean editsBlocks();

		public boolean tick();

	}

	private static TaskRegistry instance;

	public static int MAX_BLOCK_TASKS = 6;

	protected static ConcurrentLinkedQueue<TickTask> tickTasks = new ConcurrentLinkedQueue<>();

	protected static ConcurrentLinkedQueue<Runnable> runLater = new ConcurrentLinkedQueue<>();

	protected static Timer timer = new Timer();

	protected static Map<Runnable, TimerTask> runnableTasks = new WeakHashMap<>();

	/* ------------------------------------------------------------ */

	public static TaskRegistry getInstance() {
		return instance;
	}

	public static long getMilliseconds(int h, int m, int s, int ms) {
		return (((((h * 60) + m) * 60) + s) * 1000) + ms;
	}

	protected static TimerTask getTimerTask(final Runnable task, final boolean repeated) {
		TimerTask timerTask = runnableTasks.get(task);
		if (timerTask == null) {
			timerTask = new TimerTask() {
				@Override
				public void run() {
					task.run();
					if (!repeated) {
						runnableTasks.remove(task);
					}
				}
			};
			runnableTasks.put(task, timerTask);
		}
		return timerTask;
	}

	/* ------------------------------------------------------------ */

	public static void remove(Runnable task) {
		TimerTask timerTask = runnableTasks.remove(task);
		if (timerTask != null) {
			remove(timerTask);
		}
	}

	public static void remove(TickTask task) {
		tickTasks.remove(task);
	}

	public static void remove(TimerTask task) {
		task.cancel();
		timer.purge();
	}

	public static void runLater(Runnable task) {
		runLater.add(task);
	}

	/* ------------------------------------------------------------ */
	/* Timers */

	public static void schedule(Runnable task, long delay) {
		schedule(getTimerTask(task, false), delay);
	}

	public static void schedule(TickTask task) {
		tickTasks.add(task);
	}

	public static void schedule(TimerTask task, long delay) {
		try {
			timer.schedule(task, delay);
		} catch (IllegalStateException e) {
			LoggingHandler.felog.warn("Could not schedule timer");
			e.printStackTrace();
		}
	}

	public static void scheduleRepeated(Runnable task, long interval) {
		scheduleRepeated(task, interval, interval);
	}

	/* ------------------------------------------------------------ */
	/* Runnable compatibility */

	public static void scheduleRepeated(Runnable task, long delay, long interval) {
		scheduleRepeated(getTimerTask(task, true), delay, interval);
	}

	public static void scheduleRepeated(TimerTask task, long interval) {
		scheduleRepeated(task, interval, interval);
	}

	public static void scheduleRepeated(TimerTask task, long delay, long interval) {
		try {
			timer.scheduleAtFixedRate(task, delay, interval);
		} catch (IllegalStateException e) {
			LoggingHandler.felog.warn("Exception scheduling timer");
			e.printStackTrace();
		}
	}

	public TaskRegistry() {
		super();
		instance = this;
	}

	@SubscribeEvent
	public void onServerStop(FEModuleServerStopEvent event) {
		tickTasks.clear();
		runnableTasks.clear();
		timer.cancel();
		timer = new Timer(true);
	}

	/* ------------------------------------------------------------ */

	@SubscribeEvent
	public void onTick(TickEvent.ServerTickEvent event) {
		for (Runnable task : runLater) {
			task.run();
		}
		runLater.clear();

		int blockTaskCount = 0;
		for (Iterator<TickTask> iterator = tickTasks.iterator(); iterator.hasNext();) {
			TickTask task = iterator.next();
			if (task.editsBlocks()) {
				if (blockTaskCount >= MAX_BLOCK_TASKS) {
					continue;
				}
				blockTaskCount++;
			}
			if (task.tick()) {
				iterator.remove();
			}
		}
	}

}
