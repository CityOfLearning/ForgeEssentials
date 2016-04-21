package com.forgeessentials.core.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.permission.PermissionManager;
import net.minecraftforge.permission.PermissionObject;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.output.LoggingHandler;

public abstract class ForgeEssentialsCommandBase extends CommandBase implements PermissionObject
{

    public List<String> aliases = new ArrayList<String>();

    // ------------------------------------------------------------
    // Command alias

    @Override
    public abstract String getCommandUsage(ICommandSender sender);

    @Override
    public List<String> getAliases()
    {
        return aliases;
    }

    /**
     * Returns a list of default aliases, that will be added to the configuration on first run
     */
    public String[] getDefaultAliases()
    {
        return new String[] {};
    }

    public void setAliases(String[] aliases)
    {
        if (aliases == null)
            setAliases(new ArrayList<String>());
        else
            setAliases(Arrays.asList(aliases));
    }

    public void setAliases(List<String> aliases)
    {
        this.aliases = aliases;
    }

    // ------------------------------------------------------------
    // Command processing

    @Override
    public void execute(ICommandSender sender, String[] args) throws CommandException
    {
        if (sender instanceof EntityPlayerMP)
        {
            processCommandPlayer((EntityPlayerMP) sender, args);
        }
        else if (sender instanceof CommandBlockLogic)
        {
            processCommandBlock((CommandBlockLogic) sender, args);
        }
        else
        {
            processCommandConsole(sender, args);
        }
    }

    public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException
    {
        throw new TranslatedCommandException("This command cannot be used as player");
    }

    public void processCommandConsole(ICommandSender sender, String[] args) throws CommandException
    {
        throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
    }

    public void processCommandBlock(CommandBlockLogic block, String[] args) throws CommandException
    {
        processCommandConsole(block, args);
    }

    // ------------------------------------------------------------
    // Command usage

    @Override
    public boolean canCommandSenderUse(ICommandSender sender)
    {
        if (!canConsoleUseCommand() && !(sender instanceof EntityPlayer))
            return false;
        return true;
    }

    public abstract boolean canConsoleUseCommand();

    // ------------------------------------------------------------
    // Permissions

    /**
     * Registers this command and it's permission node
     */
    public void register()
    {
        Map<?, ?> commandMap = ((CommandHandler) MinecraftServer.getServer().getCommandManager()).getCommands();
        if (commandMap.containsKey(getName()))
            LoggingHandler.felog.error(String.format("Command %s registered twice", getName()));

        if (getAliases() != null && !getAliases().isEmpty())
        {
            for (String alias : getAliases())
                if (alias != null && commandMap.containsKey(alias))
                    LoggingHandler.felog.error(String.format("Command alias %s of command %s registered twice", alias, getName()));
        }

        ((CommandHandler) MinecraftServer.getServer().getCommandManager()).registerCommand(this);
        registerExtraPermissions();
    }

    @SuppressWarnings("unchecked")
    public void deregister()
    {
        CommandHandler cmdHandler = (CommandHandler) MinecraftServer.getServer().getCommandManager();
        Map<String, ICommand> commandMap = cmdHandler.getCommands();
        Set<ICommand> commandSet = (Set<ICommand>) ReflectionHelper.getPrivateValue(CommandHandler.class, cmdHandler, "field_71561_b", "commandSet");

        String commandName = getName();
        List<String> commandAliases = getAliases();
        commandSet.remove(this);
        if (commandName != null)
            commandMap.remove(commandName);
        if (commandAliases != null && !commandAliases.isEmpty())
        {
            for (String alias : commandAliases)
            {
                commandMap.remove(alias);
            }
        }
    }

    /**
     * Registers additional permissions
     */
    public void registerExtraPermissions()
    {
        /* do nothing */
    }

    /**
     * Check, if the sender has permissions to use this command
     */
    public boolean checkCommandPermission(ICommandSender sender)
    {
        if (getPermissionNode() == null || getPermissionNode().isEmpty())
            return true;
        return PermissionManager.checkPermission(sender, this, getPermissionNode());
    }

    // ------------------------------------------------------------
    // Utilities

    public static List<String> getListOfStringsMatchingLastWord(String arg, Collection<String> possibleMatches)
    {
        List<String> arraylist = new ArrayList<>();
        for (String s2 : possibleMatches)
        {
            if (doesStringStartWith(arg, s2))
            {
                arraylist.add(s2);
            }
        }
        return arraylist;
    }

    public static List<String> getListOfStringsMatchingLastWord(String[] args, Collection<String> possibleMatches)
    {
        return getListOfStringsMatchingLastWord(args[args.length - 1], possibleMatches);
    }

    public static List<String> getListOfStringsMatchingLastWord(String arg, String... possibleMatches)
    {
        List<String> arraylist = new ArrayList<>();
        int i = possibleMatches.length;
        for (int j = 0; j < i; ++j)
        {
            String s2 = possibleMatches[j];
            if (doesStringStartWith(arg, s2))
            {
                arraylist.add(s2);
            }
        }
        return arraylist;
    }

    public static List<String> getListOfStringsMatchingLastWord(String[] args, String... possibleMatches)
    {
        return getListOfStringsMatchingLastWord(args[args.length - 1], possibleMatches);
    }

    public static List<String> completePlayername(String arg)
    {
        List<String> arraylist = new ArrayList<>();
        for (UserIdent s2 : APIRegistry.perms.getServerZone().getKnownPlayers())
        {
            if (doesStringStartWith(arg, s2.getUsernameOrUuid()))
            {
                arraylist.add(s2.getUsernameOrUuid());
            }
        }
        return arraylist;
    }

    @Override
    public int compareTo(Object o)
    {
        if (o instanceof ICommand)
            return this.compareTo((ICommand) o);
        return 0;
    }

    /**
     * Parse int with support for relative int.
     *
     * @param sender
     * @param string
     * @param relativeStart
     * @return
     * @throws NumberInvalidException
     */
    public static int parseInt(String string, int relativeStart) throws NumberInvalidException
    {
        if (string.startsWith("~"))
        {
            string = string.substring(1);
            return relativeStart + parseInt(string);
        }
        else
        {
            return parseInt(string);
        }
    }

    /**
     * Parse double with support for relative values.
     *
     * @param sender
     * @param string
     * @param relativeStart
     * @return
     */
    public static double parseDouble(String string, double relativeStart) throws NumberInvalidException
    {
        if (string.startsWith("~"))
        {
            string = string.substring(1);
            return relativeStart + parseInt(string);
        }
        else
        {
            return parseInt(string);
        }
    }

}
