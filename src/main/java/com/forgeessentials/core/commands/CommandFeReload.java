package com.forgeessentials.core.commands;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

public class CommandFeReload extends ForgeEssentialsCommandBase {

	public static void reload(ICommandSender sender) {
		ModuleLauncher.instance.reloadConfigs();
		ChatOutputHandler.chatConfirmation(sender,
				Translator.translate("Reloaded configs. (may not work for all settings)"));
	}

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public String getCommandName() {
		return "fereload";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/fereload: Reload FE configuration";
	}

	@Override
	public String[] getDefaultAliases() {
		return new String[] { "reload" };
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.OP;
	}

	@Override
	public String getPermissionNode() {
		return ForgeEssentials.PERM_RELOAD;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		reload(sender);
	}

}
