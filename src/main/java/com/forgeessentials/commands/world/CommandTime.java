package com.forgeessentials.commands.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commands.world.CommandWeather.WeatherData;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.CommandParserArgs;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.permission.PermissionLevel;

public class CommandTime extends ParserCommandBase implements ConfigurableCommand {

	public static class TimeData {
		Long frozenTime;
	}

	public static final int dayTimeStart = 1;
	public static final int dayTimeEnd = 11;
	public static final int nightTimeStart = 14;

	public static final int nightTimeEnd = 22;

	protected static HashMap<Integer, TimeData> timeData = new HashMap<>();

	protected static TimeData getTimeData(int dim) {
		TimeData td = timeData.get(dim);
		if (td == null) {
			td = new TimeData();
			timeData.put(dim, td);
		}
		return td;
	}

	/* ------------------------------------------------------------ */

	public static void parseFreeze(CommandParserArgs arguments) throws CommandException {
		World world = arguments.isEmpty() ? null : arguments.parseWorld();
		if (arguments.isTabCompletion) {
			return;
		}

		if (world == null) {
			boolean freeze = getTimeData(0).frozenTime == null;
			for (World w : DimensionManager.getWorlds()) {
				TimeData td = getTimeData(w.provider.getDimensionId());
				td.frozenTime = freeze ? w.getWorldInfo().getWorldTime() : null;
			}
			if (freeze) {
				arguments.confirm("Froze time in all worlds");
			} else {
				arguments.confirm("Unfroze time in all worlds");
			}
		} else {
			TimeData td = getTimeData(world.provider.getDimensionId());
			td.frozenTime = (td.frozenTime == null) ? world.getWorldInfo().getWorldTime() : null;
			if (td.frozenTime != null) {
				arguments.confirm("Froze time");
			} else {
				arguments.confirm("Unfroze time");
			}
		}
		save();
	}

	public static void parseTime(CommandParserArgs arguments, boolean addTime) throws CommandException {
		long time;
		if (!addTime) {
			arguments.tabComplete("dawn", "day", "noon", "dusk", "night", "midnight");
			String timeStr = arguments.remove().toLowerCase();
			switch (timeStr) {
			case "dawn":
				time = 23000;
				break;
			case "day":
			case "morning":
				time = 1000;
				break;
			case "noon":
				time = 6 * 1000;
				break;
			case "dusk":
				time = 12 * 1000;
				break;
			case "night":
				time = 14 * 1000;
				break;
			case "midnight":
				time = 18 * 1000;
				break;
			default:
				time = parseInt(timeStr);
				break;
			}
		} else {
			time = parseInt(arguments.remove());
		}

		World world = arguments.isEmpty() ? null : arguments.parseWorld();
		if (arguments.isTabCompletion) {
			return;
		}

		if (world == null) {
			for (World w : DimensionManager.getWorlds()) {
				if (addTime) {
					w.getWorldInfo().setWorldTime(w.getWorldInfo().getWorldTime() + time);
				} else {
					w.getWorldInfo().setWorldTime(time);
				}
				TimeData td = getTimeData(w.provider.getDimensionId());
				if (td.frozenTime != null) {
					td.frozenTime = w.getWorldInfo().getWorldTime();
				}
			}
			arguments.confirm("Set time to %s in all worlds", time);
		} else {
			if (addTime) {
				world.getWorldInfo().setWorldTime(world.getWorldInfo().getWorldTime() + time);
			} else {
				world.getWorldInfo().setWorldTime(time);
			}
			TimeData td = getTimeData(world.provider.getDimensionId());
			if (td.frozenTime != null) {
				td.frozenTime = world.getWorldInfo().getWorldTime();
			}
			arguments.confirm("Set time to %s", time);
		}
	}

	public static void save() {
		DataManager.getInstance().deleteAll(WeatherData.class);
		for (Entry<Integer, TimeData> state : timeData.entrySet()) {
			DataManager.getInstance().save(state.getValue(), state.getKey().toString());
		}
	}

	public static void updateWorld(World world) {
		TimeData td = getTimeData(world.provider.getDimensionId());
		if (td.frozenTime != null) {
			world.getWorldInfo().setWorldTime(td.frozenTime);
		}
	}

	public CommandTime() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@SubscribeEvent
	public void doWorldTick(TickEvent.WorldTickEvent event) {
		if (event.phase == Phase.START) {
			return;
		}
		World world = event.world;
		WorldInfo wi = world.getWorldInfo();
		if ((wi.getWorldTotalTime() % 10) == 0) {
			updateWorld(world);
		}
	}

	@Override
	public String getCommandName() {
		return "time";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/time freeze|set|add [dawn|day|noon|dusk|night|midnight|<time>] [world]: Manipulate time.";
	}

	/* ------------------------------------------------------------ */

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.OP;
	}

	@Override
	public String getPermissionNode() {
		return ModuleCommands.PERM + ".time";
	}

	@Override
	public void loadConfig(Configuration config, String category) {
		/* do nothing */
	}

	@Override
	public void loadData() {
		Map<String, TimeData> states = DataManager.getInstance().loadAll(TimeData.class);
		timeData.clear();
		for (Entry<String, TimeData> state : states.entrySet()) {
			if (state.getValue() == null) {
				continue;
			}
			try {
				timeData.put(Integer.parseInt(state.getKey()), state.getValue());
			} catch (NumberFormatException e) {
				/* do nothing or log message */
			}
		}
	}

	@Override
	public void parse(CommandParserArgs arguments) throws CommandException {
		if (arguments.isEmpty()) {
			arguments.confirm(getCommandUsage(arguments.sender));
			return;
		}

		arguments.tabComplete("freeze", "set", "add");
		String subCmd = arguments.remove().toLowerCase();
		switch (subCmd) {
		case "freeze":
			parseFreeze(arguments);
			break;
		case "set":
			parseTime(arguments, false);
			break;
		case "add":
			parseTime(arguments, true);
			break;
		default:
			throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCmd);
		}
	}

}