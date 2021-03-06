package com.forgeessentials.commands.player;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.output.LoggingHandler;
import com.forgeessentials.core.commands.CommandFeSettings;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;

public class CommandAFK extends ForgeEssentialsCommandBase {

	public static final String PERM = "fe.commands.afk";

	public static final String PERM_ANNOUNCE = PERM + ".announce";

	public static final String PERM_WARMUP = PERM + ".warmup";

	public static final String PERM_AUTOTIME = PERM + ".autotime";

	public static final String PERM_AUTOKICK = PERM + ".autokick";

	public CommandAFK() {
		CommandFeSettings.addAlias("Afk", "timeout", PERM_AUTOTIME);
		CommandFeSettings.addAlias("Afk", "auto_kick", PERM_AUTOKICK);
		CommandFeSettings.addAlias("Afk", "warmup", PERM_WARMUP);
	}

	@Override
	public boolean canConsoleUseCommand() {
		return false;
	}

	@Override
	public String getCommandName() {
		return "feafk";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/afk: Mark yourself as away.";
	}

	@Override
	public String[] getDefaultAliases() {
		return new String[] { "afk" };
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.TRUE;
	}

	@Override
	public String getPermissionNode() {
		return PERM;
	}

	@Override
	public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException {
		UserIdent ident = UserIdent.get(sender);

		if (args.length >= 1) {
			// expected syntax: /afk timeout <group|player> <timeout>
			// to set custom afk timeout for yourself, replace <player> with
			// your own username
			if (args[0].equalsIgnoreCase("timeout")) {
				UserIdent applyTo = UserIdent.get(args[1], true);
				if (applyTo != null) {
					APIRegistry.perms.setPlayerPermissionProperty(applyTo, PERM_AUTOTIME, args[2]);
				} else {
					APIRegistry.perms.setGroupPermissionProperty(args[1], PERM_AUTOTIME, args[2]);
				}
			}
			// expected syntax: /afk timeout <group|player> [true|false}
			else if (args[0].equalsIgnoreCase("autokick")) {
				UserIdent applyTo = UserIdent.get(args[1], true);
				if (applyTo != null) {
					APIRegistry.perms.setPlayerPermissionProperty(applyTo, PERM_AUTOKICK, args[2]);
				} else {
					APIRegistry.perms.setGroupPermissionProperty(args[1], PERM_AUTOKICK, args[2]);
				}
			}
		} else {
			int autoTime = ServerUtil.parseIntDefault(ident.getPermissionProperty(CommandAFK.PERM_AUTOTIME), 60 * 2);
			int warmup = ServerUtil.parseIntDefault(ident.getPermissionProperty(PERM_WARMUP), 0);
			try {
				PlayerInfo.get(sender).setActive((autoTime * 1000) - (warmup * 1000));
			} catch (Exception e) {
				LoggingHandler.felog.error("Error getting player Info");
			}
			ChatOutputHandler.chatConfirmation(sender, Translator.format("Stand still for %d seconds.", warmup));
		}
	}

	@Override
	public void registerExtraPermissions() {
		APIRegistry.perms.registerPermission(PERM_ANNOUNCE, PermissionLevel.TRUE);
		APIRegistry.perms.registerPermissionProperty(PERM_WARMUP, "10",
				"Time a player needs to wait before he can go afk with /afk");
		APIRegistry.perms.registerPermissionProperty(PERM_AUTOTIME, "480",
				"Auto afk time in seconds. Set to 0 to disable.");
		APIRegistry.perms.registerPermission(PERM_AUTOKICK, PermissionLevel.FALSE,
				"Automatically kick a player, when he is AFK");
	}

}