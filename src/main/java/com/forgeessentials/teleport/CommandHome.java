package com.forgeessentials.teleport;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

public class CommandHome extends ForgeEssentialsCommandBase
{

    @Override
    public String getName()
    {
        return "home";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException
    {
        if (args.length == 0)
        {
            WarpPoint home;
			try {
				home = PlayerInfo.get(sender.getPersistentID()).getHome();
            TeleportHelper.teleport(sender, home);} catch (Exception e) {
				throw new TranslatedCommandException("No home set. Use \"/home set\" first.");
			}
        }
        else
        {
            if (args[0].equalsIgnoreCase("set"))
            {
                EntityPlayerMP player = sender;
                if (args.length == 2)
                {
                    if (!PermissionManager.checkPermission(sender, TeleportModule.PERM_HOME_OTHER))
                        throw new TranslatedCommandException("You don't have the permission to access other players home.");
                    player = UserIdent.getPlayerByMatchOrUsername(sender, args[1]);
                    if (player == null)
                        throw new TranslatedCommandException("Player %s not found.", args[1]);
                }
                else if (!PermissionManager.checkPermission(sender, TeleportModule.PERM_HOME_SET))
                    throw new TranslatedCommandException("You don't have the permission to set your home location.");

                WarpPoint p = new WarpPoint(sender);
                PlayerInfo info;
				try {
					info = PlayerInfo.get(player.getPersistentID());
				
                info.setHome(p);
                info.save();
                ChatOutputHandler.chatConfirmation(sender, Translator.format("Home set to: %1.0f, %1.0f, %1.0f", p.getX(), p.getY(), p.getZ()));} catch (Exception e) {
                	LoggingHandler.felog.error("Error getting player Info");
				}
            }
            else
                throw new TranslatedCommandException("Unknown subcommand");
        }
    }

    @Override
    public String getPermissionNode()
    {
        return TeleportModule.PERM_HOME;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "here");
        }
        return null;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        if (sender instanceof EntityPlayer)
        {
            return "/home [here|x, y, z] Set your home location.";
        }
        else
        {
            return null;
        }
    }

}
