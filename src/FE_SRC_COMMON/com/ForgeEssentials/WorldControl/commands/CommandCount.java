package com.ForgeEssentials.WorldControl.commands;

//Depreciated
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

import com.ForgeEssentials.WorldControl.TickTasks.TickTaskCountSelection;
import com.ForgeEssentials.WorldControl.TickTasks.TickTaskDistribution;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayerArea;
import com.ForgeEssentials.api.permissions.query.PermQuery.PermResult;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.BlockInfo;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TickTaskHandler;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;

public class CommandCount extends WorldControlCommandBase
{

	public CommandCount()
	{
		super(true);
		this.aliasList.add("size");
	}

	@Override
	public String getName()
	{
		return "count";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		PlayerInfo info = PlayerInfo.getPlayerInfo(player);
		AreaBase sel = info.getSelection();
		if(sel == null) {
			OutputHandler.chatError(player, "You must have a selection!");
			return;
		}
		if(args.length==0) {
			PermQueryPlayerArea query = new PermQueryPlayerArea(player, getCommandPerm(), sel, false);
			PermResult result = PermissionsAPI.checkPermResult(query);

			if(result==PermResult.ALLOW||result==PermResult.PARTIAL) {
				OutputHandler.chatConfirmation(player, "Counted "+(sel.getXLength() * sel.getYLength() * sel.getZLength())+" block!");
			}else{
				OutputHandler.chatError(player, "You do not have permission!");
			}
			return;
		}else if(args.length==1) {
			BlockInfo inf = BlockInfo.parse(args[0], player);
			if(inf!=null) {
				PermQueryPlayerArea query = new PermQueryPlayerArea(player, getCommandPerm(), sel, false);
				PermResult result = PermissionsAPI.checkPermResult(query);

				if(result==PermResult.ALLOW) {
					TickTaskHandler.addTask(new TickTaskCountSelection(player, sel, inf));
				}else if(result==PermResult.PARTIAL) {
					TickTaskHandler.addTask(new TickTaskCountSelection(player, sel, inf, query.applicable));
				}else{
					OutputHandler.chatError(player, "You do not have permission!");
				}
			}
			return;
		}else{
			OutputHandler.chatError(player, "Invalid block identifier!");
		}
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/" + getCommandName() + " [block]";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Count Selection Blocks";
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.WorldControl.selection";
	}

}
