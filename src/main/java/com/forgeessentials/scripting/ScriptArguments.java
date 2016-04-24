package com.forgeessentials.scripting;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.chat.ChatConfig;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.FEConfig;
import com.forgeessentials.scripting.ScriptParser.MissingPlayerException;
import com.forgeessentials.scripting.ScriptParser.ScriptArgument;
import com.forgeessentials.scripting.ScriptParser.ScriptException;
import com.forgeessentials.scripting.ScriptParser.SyntaxException;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;
import com.google.common.collect.ImmutableMap;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;

public final class ScriptArguments {

	private static Map<String, ScriptArgument> scriptArguments = new HashMap<>();

	public static final Pattern ARGUMENT_PATTERN = Pattern.compile("@\\{?(\\w+)\\}?");

	public static ScriptArgument sender = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Command sender name";
		}

		@Override
		public String process(ICommandSender sender) {
			if (sender == null) {
				throw new MissingPlayerException();
			}
			return sender.getName();
		}
	};

	public static ScriptArgument player = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Player name";
		}

		@Override
		public String process(ICommandSender sender) {
			if (sender == null) {
				throw new MissingPlayerException();
			}
			return sender.getName();
		}
	};

	public static ScriptArgument uuid = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Player UUID";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			return ((EntityPlayerMP) sender).getPersistentID().toString();
		}
	};

	public static ScriptArgument x = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Player X position (as integer)";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			return Integer.toString((int) ((EntityPlayerMP) sender).posX);
		}
	};

	public static ScriptArgument y = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Player Y position (as integer)";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			return Integer.toString((int) ((EntityPlayerMP) sender).posY);
		}
	};

	public static ScriptArgument z = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Player Z position (as integer)";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			return Integer.toString((int) ((EntityPlayerMP) sender).posZ);
		}
	};

	public static ScriptArgument xd = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Player X position (as floating point number)";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			return Double.toString(((EntityPlayerMP) sender).posX);
		}
	};

	public static ScriptArgument yd = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Player Y position (as floating point number)";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			return Double.toString(((EntityPlayerMP) sender).posY);
		}
	};

	public static ScriptArgument zd = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Player Z position (as floating point number)";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			return Double.toString(((EntityPlayerMP) sender).posZ);
		}
	};

	public static ScriptArgument dim = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Player dimension";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			return Integer.toString(((EntityPlayerMP) sender).dimension);
		}
	};

	public static ScriptArgument gm = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Player gamemode";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			if (((EntityPlayerMP) sender).theItemInWorldManager.getGameType().isCreative()) {
				return ChatConfig.gamemodeCreative;
			}
			if (((EntityPlayerMP) sender).theItemInWorldManager.getGameType().isAdventure()) {
				return ChatConfig.gamemodeAdventure;
			}
			return ChatConfig.gamemodeSurvival;
		}
	};

	public static ScriptArgument health = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Player health";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			return Float.toString(((EntityPlayerMP) sender).getHealth());
		}
	};

	public static ScriptArgument healthcolor = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Insert color code based on player health";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			float health = ((EntityPlayerMP) sender).getHealth();
			if (health <= 6) {
				return EnumChatFormatting.RED.toString();
			}
			if (health < 16) {
				return EnumChatFormatting.YELLOW.toString();
			}
			return EnumChatFormatting.GREEN.toString();
		}
	};

	public static ScriptArgument hunger = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Player hunger level";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			return Integer.toString(((EntityPlayerMP) sender).getFoodStats().getFoodLevel());
		}
	};

	public static ScriptArgument hungercolor = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Insert color code based on player hunger level";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			float hunger = ((EntityPlayerMP) sender).getFoodStats().getFoodLevel();
			if (hunger <= 6) {
				return EnumChatFormatting.RED.toString();
			}
			if (hunger < 12) {
				return EnumChatFormatting.YELLOW.toString();
			}
			return EnumChatFormatting.GREEN.toString();
		}
	};

	public static ScriptArgument saturation = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Player (food) saturation level";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			return Float.toString(((EntityPlayerMP) sender).getFoodStats().getSaturationLevel());
		}
	};

	public static ScriptArgument saturationcolor = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Insert color code based on player saturation level";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			float hunger = ((EntityPlayerMP) sender).getFoodStats().getSaturationLevel();
			if (hunger <= 0) {
				return EnumChatFormatting.RED.toString();
			}
			if (hunger <= 1.5) {
				return EnumChatFormatting.YELLOW.toString();
			}
			return EnumChatFormatting.GREEN.toString();
		}
	};

	public static ScriptArgument zone = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Get name of the zone the player is in";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			return APIRegistry.perms.getServerZone().getZoneAt(new WorldPoint(((EntityPlayerMP) sender))).getName();
		}
	};

	public static ScriptArgument zoneId = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Get ID of the zone the player is in";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			return Integer.toString(
					APIRegistry.perms.getServerZone().getZoneAt(new WorldPoint(((EntityPlayerMP) sender))).getId());
		}
	};

	public static ScriptArgument group = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Get name of the zone the player is in";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			EntityPlayerMP _player = ((EntityPlayerMP) sender);
			return APIRegistry.perms.getServerZone().getPlayerGroups(UserIdent.get(_player)).first().getGroup();
		}
	};

	public static ScriptArgument timePlayed = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Get total time a player played on the server" + "";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			EntityPlayerMP _player = ((EntityPlayerMP) sender);
			try {
				return ChatOutputHandler.formatTimeDurationReadable(PlayerInfo.get(_player).getTimePlayed() / 1000,
						true);
			} catch (Exception e) {
				LoggingHandler.felog.error("Error getting player Info");
				return "";
			}
		}
	};

	public static ScriptArgument lastLogout = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Get the time a player logged out last time";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			EntityPlayerMP _player = ((EntityPlayerMP) sender);
			try {
				return FEConfig.FORMAT_DATE_TIME.format(PlayerInfo.get(_player).getLastLogout());
			} catch (Exception e) {
				LoggingHandler.felog.error("Error getting player Info");
				return "";
			}
		}
	};

	public static ScriptArgument lastLogin = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Get the time a player logged in last time";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			EntityPlayerMP _player = ((EntityPlayerMP) sender);
			try {
				return FEConfig.FORMAT_DATE_TIME.format(PlayerInfo.get(_player).getLastLogin());
			} catch (Exception e) {
				LoggingHandler.felog.error("Error getting player Info");
				return "";
			}
		}
	};

	public static ScriptArgument sinceLastLogout = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Get the time since a player logged out last time";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			EntityPlayerMP _player = ((EntityPlayerMP) sender);
			try {
				return ChatOutputHandler.formatTimeDurationReadable(
						(new Date().getTime() - PlayerInfo.get(_player).getLastLogout().getTime()) / 1000, true);
			} catch (Exception e) {
				LoggingHandler.felog.error("Error getting player Info");
				return "";
			}
		}
	};

	public static ScriptArgument sinceLastLogin = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Get the time since a player logged in last time";
		}

		@Override
		public String process(ICommandSender sender) {
			if (!(sender instanceof EntityPlayerMP)) {
				throw new MissingPlayerException();
			}
			EntityPlayerMP _player = ((EntityPlayerMP) sender);
			try {
				return ChatOutputHandler.formatTimeDurationReadable(
						(new Date().getTime() - PlayerInfo.get(_player).getLastLogin().getTime()) / 1000, true);
			} catch (Exception e) {
				LoggingHandler.felog.error("Error getting player Info");
				return "";
			}
		}
	};

	public static ScriptArgument tps = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Ticks per second";
		}

		@Override
		public String process(ICommandSender sender) {
			return new DecimalFormat("#").format(Math.min(20, ServerUtil.getTPS()));
		}
	};

	public static ScriptArgument realTime = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Current real time";
		}

		@Override
		public String process(ICommandSender sender) {
			return FEConfig.FORMAT_TIME.format(new Date());
		}
	};

	public static ScriptArgument realDate = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Current real date";
		}

		@Override
		public String process(ICommandSender sender) {
			return FEConfig.FORMAT_DATE.format(new Date());
		}
	};

	public static ScriptArgument worldTime = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Current MC world time";
		}

		@Override
		public String process(ICommandSender sender) {
			return new DecimalFormat("#").format(MinecraftServer.getServer().getEntityWorld().getWorldTime());
		}
	};

	public static ScriptArgument worldTimeClock = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Current MC world time formatted as H:MM";
		}

		@Override
		public String process(ICommandSender sender) {
			try {
				FEConfig.FORMAT_TIME.setTimeZone(TimeZone.getTimeZone("US"));
				long ticks = MinecraftServer.getServer().getEntityWorld().getWorldTime();
				Date time = new Date(((ticks * 1000 * 60 * 60 * 24) / 24000) + (1000 * 60 * 60 * 6));
				return FEConfig.FORMAT_TIME.format(time);
			} finally {
				FEConfig.FORMAT_TIME.setTimeZone(TimeZone.getDefault());
			}
		}
	};

	public static ScriptArgument totalWorldTime = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "MC time passed since map creation";
		}

		@Override
		public String process(ICommandSender sender) {
			return new DecimalFormat("#").format(MinecraftServer.getServer().getEntityWorld().getTotalWorldTime());
		}
	};

	public static ScriptArgument serverUptime = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Time since server start";
		}

		@Override
		public String process(ICommandSender sender) {
			RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
			return ChatOutputHandler.formatTimeDurationReadable(rb.getUptime() / 1000, true);
		}
	};

	public static ScriptArgument onlinePlayers = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Number of players that are online right now";
		}

		@Override
		public String process(ICommandSender sender) {
			int online = 0;
			try {
				online = MinecraftServer.getServer().getCurrentPlayerCount();
			} catch (Exception e) {
				/* do nothing */
			}
			return Integer.toString(online);
		}
	};

	public static ScriptArgument uniquePlayers = new ScriptArgument() {
		@Override
		public String getHelp() {
			return "Number of unique players on the server at all time";
		}

		@Override
		public String process(ICommandSender sender) {
			return Integer.toString(APIRegistry.perms.getServerZone().getKnownPlayers().size());
		}
	};

	static {
		registerAll();
		add("p", player);
	}

	public static void add(String name, ScriptArgument argument) {
		if (scriptArguments.containsKey(name)) {
			throw new RuntimeException(String.format("Script argument name @%s already registered", name));
		}
		scriptArguments.put(name, argument);
	}

	public static ScriptArgument get(String name) {
		return scriptArguments.get(name);
	}

	public static Map<String, ScriptArgument> getAll() {
		return ImmutableMap.copyOf(scriptArguments);
	}

	public static String process(String text, ICommandSender sender) throws ScriptException {
		return process(text, sender, null);
	}

	public static String process(String text, ICommandSender sender, List<?> args) throws ScriptException {
		Matcher m = ARGUMENT_PATTERN.matcher(text);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String modifier = m.group(1).toLowerCase();
			ScriptArgument argument = get(modifier);
			if (argument != null) {
				m.appendReplacement(sb, argument.process(sender));
			} else if (args == null) {
				m.appendReplacement(sb, m.group());
			} else {
				try {
					int idx = Integer.parseInt(modifier);
					if ((args == null) || (idx < 1) || (idx > args.size())) {
						throw new SyntaxException("Missing argument @%d", idx);
					}
					m.appendReplacement(sb, args.get(idx - 1).toString());
				} catch (NumberFormatException e) {
					m.appendReplacement(sb, m.group());
				}
			}
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public static String processSafe(String text, ICommandSender sender) {
		return processSafe(text, sender, null);
	}

	public static String processSafe(String text, ICommandSender sender, List<?> args) {
		Matcher m = ARGUMENT_PATTERN.matcher(text);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String modifier = m.group(1).toLowerCase();
			ScriptArgument argument = get(modifier);
			if (argument != null) {
				try {
					m.appendReplacement(sb, argument.process(sender));
				} catch (ScriptException e) {
					m.appendReplacement(sb, m.group());
				}
			} else if (args == null) {
				m.appendReplacement(sb, m.group());
			} else {
				try {
					int idx = Integer.parseInt(modifier);
					if ((args == null) || (idx >= args.size())) {
						throw new SyntaxException("Missing argument @%d", idx);
					}
					m.appendReplacement(sb, args.get(idx).toString());
				} catch (NumberFormatException e) {
					m.appendReplacement(sb, m.group());
				}
			}
		}
		m.appendTail(sb);
		return sb.toString();
	}

	private static void registerAll() {
		try {
			for (Field field : ScriptArguments.class.getDeclaredFields()) {
				if (ScriptArgument.class.isAssignableFrom(field.getType()) && Modifier.isStatic(field.getModifiers())) {
					add(field.getName().toLowerCase(), (ScriptArgument) field.get(null));
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
