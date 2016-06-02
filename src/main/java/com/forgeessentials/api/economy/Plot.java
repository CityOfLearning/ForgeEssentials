package com.forgeessentials.api.economy;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.commons.selections.WorldPoint;

public interface Plot {

	public WorldPoint getCenter();
	public int getDimension();
	public int getFee();
	public int getFeeTimeout();
	public String getName();
	public UserIdent getOwner();
	
	public WorldPoint getPlotCenter();
	public long getPrice();
	public AreaZone getZone();
	public boolean isForSale();
}
