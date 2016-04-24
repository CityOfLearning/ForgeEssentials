package com.forgeessentials.util.selections;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.LoggingHandler;

public class PlayerInfoSelectionProvider implements ISelectionProvider
{

    @Override
    public Selection getSelection(EntityPlayerMP player)
    {
        PlayerInfo pi;
		try {
			pi = PlayerInfo.get(player);
		
        return new Selection(pi.getSelDim(), pi.getSel1(), pi.getSel2());
        } catch (Exception e) {
        	LoggingHandler.felog.error("Error getting player Info");
        	return new Selection(0, new Point(0,0,0), new Point(0,0,0));
		}
    }

    @Override
    public void setDimension(EntityPlayerMP player, int dim)
    {
        try {
			PlayerInfo.get(player).setSelDim(dim);
		} catch (Exception e) {
			LoggingHandler.felog.error("Error getting player Info");
		}
    }

    @Override
    public void setStart(EntityPlayerMP player, Point start)
    {
        try {
			PlayerInfo.get(player).setSel1(start);
		} catch (Exception e) {
			LoggingHandler.felog.error("Error getting player Info");
		}
    }

    @Override
    public void setEnd(EntityPlayerMP player, Point end)
    {
        try {
			PlayerInfo.get(player).setSel2(end);
		} catch (Exception e) {
			LoggingHandler.felog.error("Error getting player Info");
		}
    }

    @Override
    public void select(EntityPlayerMP player, int dimension, AreaBase area)
    {
        PlayerInfo pi;
		try {
			pi = PlayerInfo.get(player);
		
        pi.setSelDim(dimension);
        pi.setSel1(area.getLowPoint());
        pi.setSel2(area.getHighPoint());
        SelectionHandler.sendUpdate(player);} catch (Exception e) {
        	LoggingHandler.felog.error("Error getting player Info");
		}
    }

}
