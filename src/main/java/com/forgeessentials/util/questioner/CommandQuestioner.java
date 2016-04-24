package com.forgeessentials.util.questioner;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

public class CommandQuestioner extends ForgeEssentialsCommandBase {
	private final boolean type;

	public CommandQuestioner(boolean type) {
		this.type = type;
	}

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public String getCommandName() {
		if (type) {
			return "yes";
		} else {
			return "no";
		}
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		if (type) {
			return "/yes Reply yes to a question.";
		} else {
			return "/no Reply no to a question.";
		}
	}

	@Override
	public String[] getDefaultAliases() {
		if (type) {
			return new String[] { "accept", "allow" };
		} else {
			return new String[] { "decline", "deny" };
		}
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.TRUE;
	}

	@Override
	public String getPermissionNode() {
		return "fe.questioner";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		Questioner.answer(sender, type);
	}

}
