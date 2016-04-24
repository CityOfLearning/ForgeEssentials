package com.forgeessentials.util.selections;

import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;

import net.minecraft.entity.player.EntityPlayerMP;

public interface ISelectionProvider {

	public Selection getSelection(EntityPlayerMP player);

	public void select(EntityPlayerMP player, int dimension, AreaBase area);

	public void setDimension(EntityPlayerMP player, int dim);

	public void setEnd(EntityPlayerMP player, Point end);

	public void setStart(EntityPlayerMP player, Point start);

}
