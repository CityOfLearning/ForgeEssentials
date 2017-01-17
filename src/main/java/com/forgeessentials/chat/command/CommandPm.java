package com.forgeessentials.chat.command;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.CommandParserArgs;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.permission.PermissionLevel;

public class CommandPm extends ParserCommandBase {

	public static Map<ICommandSender, WeakReference<ICommandSender>> targetMap = new WeakHashMap<>();

	public static void clearTarget(ICommandSender sender) {
		targetMap.remove(sender);
	}

	public static ICommandSender getTarget(ICommandSender sender) {
		WeakReference<ICommandSender> target = targetMap.get(sender);
		if (target == null) {
			return null;
		}
		return target.get();
	}

	public static void setTarget(ICommandSender sender, ICommandSender target) {
		targetMap.put(sender, new WeakReference<>(target));
	}

	/* ------------------------------------------------------------ */

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public String getCommandName() {
		return "pm";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/pm <player>: Sticky private message mode";
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.TRUE;
	}

	@Override
	public String getPermissionNode() {
		return "fe.chat.pm";
	}

	@Override
	public void parse(CommandParserArgs arguments) throws CommandException {
		ICommandSender target = getTarget(arguments.sender);
		if (target == null) {
			if (arguments.size() != 1) {
				throw new TranslatedCommandException("You must first select a target with /pm <player>");
			}
			UserIdent player = arguments.parsePlayer(true, true);
			if (arguments.isTabCompletion) {
				return;
			}
			if (arguments.sender == player.getPlayer()) {
				throw new PlayerNotFoundException("commands.message.sameTarget");
			}
			setTarget(arguments.sender, player.getPlayer());
			arguments.confirm("Set PM target to %s", player.getUsernameOrUuid());
		} else {
			if (arguments.isTabCompletion) {
				return;
			}
			if (arguments.isEmpty()) {
				clearTarget(arguments.sender);
				arguments.confirm("Cleared PM target");
			} else {
				IChatComponent message = getChatComponentFromNthArg(arguments.sender, arguments.toArray(), 0,
						!(arguments.sender instanceof EntityPlayer));
				ModuleChat.tell(arguments.sender, message, target);
			}
		}
	}

}
