package com.forgeessentials.commands.item;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.misc.TranslatedCommandException;

public class CommandRename extends FEcmdModuleCommands
{
    @Override
    public String getName()
    {
        return "rename";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException
    {
        if (args.length == 0)
            throw new TranslatedCommandException(getCommandUsage(sender));

        ItemStack is = sender.inventory.getCurrentItem();
        if (is == null)
            throw new TranslatedCommandException("You are not holding a valid item.");

        StringBuilder sb = new StringBuilder();
        for (String arg : args)
        {
            sb.append(arg + " ");
        }
        is.setStackDisplayName(sb.toString().trim());
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/rename <new name> Renames the item you are currently holding.";
    }
}