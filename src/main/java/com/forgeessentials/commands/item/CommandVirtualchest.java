package com.forgeessentials.commands.item;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.commands.util.VirtualChest;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permission.PermissionLevel;

/**
 * Opens a configurable virtual chest
 *
 * @author Dries007
 */
public class CommandVirtualchest extends FEcmdModuleCommands implements ConfigurableCommand {
	public static int size = 54;
	public static String name = "Vault 13";

	@Override
	public boolean canConsoleUseCommand() {
		return false;
	}

	@Override
	public String getCommandName() {
		return "virtualchest";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/vchest Open a virtual chest";
	}

	@Override
	public String[] getDefaultAliases() {
		return new String[] { "vchest" };
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.OP;
	}

	@Override
	public void loadConfig(Configuration config, String category) {
		size = config
				.get(category, "VirtualChestRows", 6, "1 row = 9 slots. 3 = 1 chest, 6 = double chest (max size!).")
				.getInt(6) * 9;
		name = config.get(category, "VirtualChestName", "Vault 13", "Don't use special stuff....").getString();
	}

	@Override
	public void loadData() {
		/* do nothing */
	}

	@Override
	public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException {
		EntityPlayerMP player = sender;
		if (player.openContainer != player.inventoryContainer) {
			player.closeScreen();
		}
		player.getNextWindowId();

		VirtualChest chest = new VirtualChest(player);
		player.displayGUIChest(chest);
	}

}
