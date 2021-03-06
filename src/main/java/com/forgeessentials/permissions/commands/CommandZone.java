package com.forgeessentials.permissions.commands;

import java.util.Map.Entry;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.permissions.Zone.PermissionList;
import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.AreaShape;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.events.EventCancelledException;
import com.forgeessentials.util.selections.SelectionHandler;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

public class CommandZone extends ParserCommandBase {

	public static final String PERM_NODE = "fe.perm.zone";
	public static final String PERM_ALL = PERM_NODE + Zone.ALL_PERMS;
	public static final String PERM_LIST = PERM_NODE + ".list";
	public static final String PERM_INFO = PERM_NODE + ".info";
	public static final String PERM_DEFINE = PERM_NODE + ".define";
	public static final String PERM_DELETE = PERM_NODE + ".delete";
	public static final String PERM_SETTINGS = PERM_NODE + ".settings";
	public static final String PERM_SWAP = PERM_NODE + ".swap";
	public static final String PERM_DUPLICATE = PERM_NODE + ".duplicate";
	public static final String PERM_COPY = PERM_NODE + ".copy";

	public static AreaZone getAreaZone(WorldZone worldZone, String arg) {
		try {
			Zone z = APIRegistry.perms.getZoneById(arg);
			if ((z != null) && (z instanceof AreaZone)) {
				return (AreaZone) z;
			}
		} catch (NumberFormatException e) {
			/* none */
		}
		return worldZone.getAreaZone(arg);
	}

	public static void parseCopy(CommandParserArgs arguments) throws CommandException {
		arguments.checkPermission(PERM_COPY);
		if (arguments.isEmpty()) {
			throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
		}

		tabCompleteArea(arguments);
		String areaName1 = arguments.remove();

		tabCompleteArea(arguments);
		String areaName2 = arguments.remove();

		if (arguments.isTabCompletion) {
			return;
		}

		WorldZone worldZone = arguments.getWorldZone();
		AreaZone areaZone1 = getAreaZone(worldZone, areaName1);
		AreaZone areaZone2 = getAreaZone(worldZone, areaName2);
		if (areaZone1 == null) {
			throw new TranslatedCommandException("Area \"%s\" does not exist!", areaName1);
		}
		if (areaZone2 == null) {
			throw new TranslatedCommandException("Area \"%s\" does not exist!", areaName2);
		}

		for (Entry<String, PermissionList> groupPerm : areaZone1.getGroupPermissions().entrySet()) {
			for (Entry<String, String> perm : groupPerm.getValue().entrySet()) {
				areaZone2.setGroupPermissionProperty(groupPerm.getKey(), perm.getKey(), perm.getValue());
			}
		}

		for (Entry<UserIdent, PermissionList> playerPerm : areaZone1.getPlayerPermissions().entrySet()) {
			for (Entry<String, String> perm : playerPerm.getValue().entrySet()) {
				areaZone2.setPlayerPermissionProperty(playerPerm.getKey(), perm.getKey(), perm.getValue());
			}
		}
	}

	public static void parseDefine(CommandParserArgs arguments, boolean redefine) throws CommandException {
		arguments.checkPermission(PERM_DEFINE);
		if (arguments.isEmpty()) {
			throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
		}

		tabCompleteArea(arguments);
		String areaName = arguments.remove();

		WorldZone worldZone = arguments.getWorldZone();
		AreaZone area = getAreaZone(worldZone, areaName);
		if (!redefine && (area != null)) {
			throw new TranslatedCommandException(String.format("Area \"%s\" already exists!", areaName));
		}
		if (redefine && (area == null)) {
			throw new TranslatedCommandException(String.format("Area \"%s\" does not exist!", areaName));
		}

		AreaShape shape = null;
		if (!arguments.isEmpty()) {
			arguments.tabComplete(AreaShape.valueNames());
			shape = AreaShape.getByName(arguments.remove());
			if (shape == null) {
				shape = AreaShape.BOX;
			}
		}

		if (arguments.isTabCompletion) {
			return;
		}

		AreaBase selection = SelectionHandler.getSelection(arguments.senderPlayer);
		if (selection == null) {
			throw new TranslatedCommandException("No selection available. Please select a region first.");
		}

		arguments.permissionContext.setTargetStart(selection.getLowPoint().toVec3())
				.setTargetEnd(selection.getHighPoint().toVec3());
		arguments.checkPermission(PERM_DEFINE);

		if (redefine && (area != null)) {
			area.setArea(selection);
			if (shape != null) {
				area.setShape(shape);
			}
			arguments.confirm("Area \"%s\" has been redefined.", areaName);
		} else {
			try {
				area = new AreaZone(worldZone, areaName, selection);
				if (shape != null) {
					area.setShape(shape);
				}
				arguments.confirm("Area \"%s\" has been defined.", areaName);
			} catch (EventCancelledException e) {
				throw new TranslatedCommandException("Defining area \"%s\" has been cancelled.", areaName);
			}
		}
	}

	public static void parseDelete(CommandParserArgs arguments) throws CommandException {
		arguments.checkPermission(PERM_DELETE);
		if (arguments.isEmpty()) {
			throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
		}

		tabCompleteArea(arguments);
		String areaName = arguments.remove();

		if (arguments.isTabCompletion) {
			return;
		}

		WorldZone worldZone = arguments.getWorldZone();
		AreaZone areaZone = getAreaZone(worldZone, areaName);
		if (areaZone == null) {
			throw new TranslatedCommandException("Area \"%s\" has does not exist!", areaName);
		}
		areaZone.getWorldZone().removeAreaZone(areaZone);
		arguments.confirm("Area \"%s\" has been deleted.", areaZone.getName());
	}

	public static void parseDuplicate(CommandParserArgs arguments) throws CommandException {
		arguments.checkPermission(PERM_DUPLICATE);
		if (arguments.isEmpty()) {
			throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
		}

		tabCompleteArea(arguments);
		String areaName1 = arguments.remove();

		tabCompleteArea(arguments);
		String areaName2 = arguments.remove();

		if (arguments.isTabCompletion) {
			return;
		}

		WorldZone worldZone = arguments.getWorldZone();
		AreaZone areaZone1 = getAreaZone(worldZone, areaName1);
		AreaZone areaZone2 = getAreaZone(worldZone, areaName2);
		if (areaZone1 == null) {
			throw new TranslatedCommandException("Area \"%s\" does not exist!", areaName1);
		}
		if (areaZone2 != null) {
			throw new TranslatedCommandException(String.format("Area \"%s\" already exists!", areaName2));
		}
		AreaShape shape = AreaShape.BOX;
		AreaBase selection = SelectionHandler.getSelection(arguments.senderPlayer);
		if (selection == null) {
			throw new TranslatedCommandException("No selection available. Please select a region first.");
		}

		arguments.permissionContext.setTargetStart(selection.getLowPoint().toVec3())
				.setTargetEnd(selection.getHighPoint().toVec3());
		arguments.checkPermission(PERM_DEFINE);

		try {
			areaZone2 = new AreaZone(worldZone, areaName2, selection);
			if (shape != null) {
				areaZone2.setShape(shape);
			}

			for (Entry<String, PermissionList> groupPerm : areaZone1.getGroupPermissions().entrySet()) {
				for (Entry<String, String> perm : groupPerm.getValue().entrySet()) {
					areaZone2.setGroupPermissionProperty(groupPerm.getKey(), perm.getKey(), perm.getValue());
				}
			}

			for (Entry<UserIdent, PermissionList> playerPerm : areaZone1.getPlayerPermissions().entrySet()) {
				for (Entry<String, String> perm : playerPerm.getValue().entrySet()) {
					areaZone2.setPlayerPermissionProperty(playerPerm.getKey(), perm.getKey(), perm.getValue());
				}
			}

			arguments.confirm("Area \"%s\" has been defined.", areaName2);
		} catch (EventCancelledException e) {
			throw new TranslatedCommandException("Defining area \"%s\" has been cancelled.", areaName2);
		}
	}

	public static void parseEntryExitMessage(CommandParserArgs arguments, boolean isEntry) throws CommandException {
		arguments.checkPermission(PERM_SETTINGS);
		if (arguments.isEmpty()) {
			throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
		}

		tabCompleteArea(arguments);
		String areaName = arguments.remove();

		WorldZone worldZone = arguments.getWorldZone();
		AreaZone areaZone = getAreaZone(worldZone, areaName);
		if (areaZone == null) {
			throw new TranslatedCommandException("Area \"%s\" has does not exist!", areaName);
		}

		if (arguments.isEmpty()) {
			arguments.confirm(
					Translator.format((isEntry ? "Entry" : "Exit") + " message for area %s:", areaZone.getName()));
			arguments.confirm(areaZone.getGroupPermission(Zone.GROUP_DEFAULT,
					isEntry ? FEPermissions.ZONE_ENTRY_MESSAGE : FEPermissions.ZONE_EXIT_MESSAGE));
			return;
		}

		arguments.tabComplete("clear");
		String msg = arguments.toString();
		if (msg.equalsIgnoreCase("clear")) {
			msg = null;
		}

		if (arguments.isTabCompletion) {
			return;
		}
		areaZone.setGroupPermissionProperty(Zone.GROUP_DEFAULT,
				isEntry ? FEPermissions.ZONE_ENTRY_MESSAGE : FEPermissions.ZONE_EXIT_MESSAGE, msg);
	}

	public static void parseInfo(CommandParserArgs arguments) throws CommandException {
		arguments.checkPermission(PERM_INFO);
		if (arguments.isEmpty()) {
			throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
		}

		tabCompleteArea(arguments);
		String areaName = arguments.remove();

		if (arguments.isTabCompletion) {
			return;
		}

		WorldZone worldZone = arguments.getWorldZone();
		AreaZone areaZone = getAreaZone(worldZone, areaName);
		if (areaZone == null) {
			throw new TranslatedCommandException("Area \"%s\" has does not exist!", areaName);
		}
		AreaBase area = areaZone.getArea();

		arguments.confirm("Area \"%s\"", areaZone.getName());
		arguments.notify("  start = " + area.getLowPoint().toString());
		arguments.notify("  end   = " + area.getHighPoint().toString());
	}

	public static void parseList(CommandParserArgs arguments) throws CommandException {
		if (arguments.isTabCompletion) {
			return;
		}

		arguments.checkPermission(PERM_LIST);

		final int PAGE_SIZE = 12;
		int limit = 1;
		if (!arguments.isEmpty()) {
			try {
				limit = Integer.parseInt(arguments.remove());
			} catch (NumberFormatException e) {
				limit = 1;
			}
		}
		arguments.confirm("List of areas (page #" + limit + "):");
		limit *= PAGE_SIZE;

		WorldZone worldZone = arguments.getWorldZone();
		if (worldZone == null) {
			for (WorldZone wz : APIRegistry.perms.getServerZone().getWorldZones().values()) {
				for (AreaZone areaZone : wz.getAreaZones()) {
					if (areaZone.isHidden()) {
						continue;
					}
					if (limit >= 0) {
						if (limit <= PAGE_SIZE) {
							arguments.confirm("#" + areaZone.getId() + ": " + areaZone.toString());
						}
						limit--;
					} else {
						break;
					}
				}
			}
		} else {
			for (AreaZone areaZone : worldZone.getAreaZones()) {
				if (areaZone.isHidden()) {
					continue;
				}
				if (limit >= 0) {
					if (limit <= PAGE_SIZE) {
						arguments.confirm("#" + areaZone.getId() + ": " + areaZone.toString());
					}
					limit--;
				} else {
					break;
				}
			}
		}
	}

	public static void parseSelect(CommandParserArgs arguments) throws CommandException {
		arguments.checkPermission(PERM_INFO);
		if (arguments.isEmpty()) {
			throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
		}

		tabCompleteArea(arguments);
		String areaName = arguments.remove();

		if (arguments.isTabCompletion) {
			return;
		}

		WorldZone worldZone = arguments.getWorldZone();
		AreaZone areaZone = getAreaZone(worldZone, areaName);
		if (areaZone == null) {
			throw new TranslatedCommandException("Area \"%s\" has does not exist!", areaName);
		}

		AreaBase area = areaZone.getArea();
		SelectionHandler.select(arguments.senderPlayer, worldZone.getDimensionID(), area);
		arguments.confirm("Area \"%s\" has been selected.", areaName);
	}

	public static void parseSwap(CommandParserArgs arguments) throws CommandException {
		arguments.checkPermission(PERM_SWAP);
		if (arguments.isEmpty()) {
			throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
		}

		tabCompleteArea(arguments);
		String areaName1 = arguments.remove();

		tabCompleteArea(arguments);
		String areaName2 = arguments.remove();

		if (arguments.isTabCompletion) {
			return;
		}

		WorldZone worldZone = arguments.getWorldZone();
		AreaZone areaZone1 = getAreaZone(worldZone, areaName1);
		AreaZone areaZone2 = getAreaZone(worldZone, areaName2);
		if (areaZone1 == null) {
			throw new TranslatedCommandException("Area \"%s\" does not exist!", areaName1);
		}
		if (areaZone2 == null) {
			throw new TranslatedCommandException("Area \"%s\" does not exist!", areaName2);
		}

		areaZone1.swapPermissions(areaZone2);
	}

	public static void tabCompleteArea(CommandParserArgs arguments) throws CommandException {
		if (arguments.isTabCompletion && (arguments.size() == 1)) {
			for (Zone z : APIRegistry.perms.getZones()) {
				if (z instanceof AreaZone) {
					if (z.getName().startsWith(arguments.peek())) {
						arguments.tabCompleteWord(z.getName());
					}
					if (Integer.toString(z.getId()).startsWith(arguments.peek())) {
						arguments.tabCompleteWord(Integer.toString(z.getId()));
					}
				}
			}
			throw new CommandParserArgs.CancelParsingException();
		}
	}

	@Override
	public boolean canConsoleUseCommand() {
		return false;
	}

	@Override
	public String getCommandName() {
		return "zone";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/zone: Manage permission zones";
	}

	@Override
	public String[] getDefaultAliases() {
		return new String[] { "area" };
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.OP;
	}

	@Override
	public String getPermissionNode() {
		return PERM_NODE;
	}

	@Override
	public void parse(CommandParserArgs arguments) throws CommandException {
		if (arguments.isEmpty()) {
			arguments.confirm("/zone list [page]: Lists all zones");
			arguments.confirm("/zone info <zone>|here: Zone information");
			arguments.confirm("/zone define|redefine <zone-name>: define or redefine a zone.");
			arguments.confirm("/zone delete <zone-id>: Delete a zone.");
			arguments.confirm("/zone select <zone-id>: Select a zone.");
			arguments.confirm("/zone entry|exit <zone-id> <message|clear>: Set the zone entry/exit message.");
			arguments.confirm("/zone swap <zone-id> <zone-id>: Swaps permissions with another zone");
			arguments.confirm(
					"/zone duplicate <zone-id-from> <new-zone-name>: Duplicates the zones permissions and creates a new zone.");
			arguments.confirm(
					"/zone copy <zone-id-from> <zone-id-to>: Copies the zones permissions into the destination zone.");
			return;
		}

		arguments.tabComplete("define", "list", "delete", "select", "redefine", "exit", "entry", "swap", "duplicate",
				"copy", "info");
		String arg = arguments.remove().toLowerCase();
		switch (arg) {
		case "select":
			parseSelect(arguments);
			break;
		case "info":
			parseInfo(arguments);
			break;
		case "list":
			parseList(arguments);
			break;
		case "define":
			parseDefine(arguments, false);
			break;
		case "redefine":
			parseDefine(arguments, true);
			break;
		case "delete":
			parseDelete(arguments);
			break;
		case "entry":
			parseEntryExitMessage(arguments, true);
			break;
		case "exit":
			parseEntryExitMessage(arguments, false);
			break;
		case "swap":
			parseSwap(arguments);
			break;
		case "duplicate":
			parseDuplicate(arguments);
			break;
		case "copy":
			parseCopy(arguments);
			break;
		default:
			throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, arg);
		}
	}
}
