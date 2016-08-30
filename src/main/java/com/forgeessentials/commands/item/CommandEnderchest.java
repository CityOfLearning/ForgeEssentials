package com.forgeessentials.commands.item;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraftforge.permission.PermissionLevel;

/**
 * Opens your enderchest.
 */
public class CommandEnderchest extends ForgeEssentialsCommandBase {
	@Override
	public boolean canConsoleUseCommand() {
		return false;
	}

	@Override
	public String getCommandName() {
		return "enderchest";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/enderchest Opens your enderchest.";
	}

	@Override
	public String[] getDefaultAliases() {
		return new String[] { "echest" };
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.OP;
	}

	@Override
	public String getPermissionNode() {
		return ModuleCommands.PERM + ".enderchest";
	}

	@Override
	public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException {
		EntityPlayerMP player = sender;
		if (player.openContainer != player.inventoryContainer) {
			player.closeScreen();
		}
		player.getNextWindowId();

		InventoryEnderChest chest = player.getInventoryEnderChest();
		chest.setChestTileEntity(null);
		player.displayGUIChest(chest);
	}

}
