package com.forgeessentials.commands.item;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.PlayerUtil;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.permission.PermissionLevel;

public class CommandDuplicate extends FEcmdModuleCommands {

	@Override
	public boolean canConsoleUseCommand() {
		return false;
	}

	@Override
	public String getCommandName() {
		return "duplicate";
	}

	@Override
	public String getCommandUsage(ICommandSender par1ICommandSender) {
		return "/duplicate [amount]: Duplicates your current item";
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.OP;
	}

	@Override
	public void processCommandPlayer(EntityPlayerMP player, String[] args) throws CommandException {
		ItemStack stack = player.getCurrentEquippedItem();
		if (stack == null) {
			throw new TranslatedCommandException("No item equipped");
		}

		int stackSize = 0;
		if (args.length > 0) {
			stackSize = parseInt(args[0]);
		}

		ItemStack newStack = stack.copy();
		if (stackSize > 0) {
			newStack.stackSize = stackSize;
		}

		PlayerUtil.give(player, newStack);
	}

}
