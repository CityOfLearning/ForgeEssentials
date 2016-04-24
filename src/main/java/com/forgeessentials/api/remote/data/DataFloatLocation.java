package com.forgeessentials.api.remote.data;

import com.forgeessentials.commons.selections.WarpPoint;

import net.minecraft.entity.Entity;

/**
 *
 */
public class DataFloatLocation {

	public int dim;

	public double x;

	public double y;

	public double z;

	public DataFloatLocation(Entity entity) {
		dim = entity.dimension;
		x = entity.posX;
		y = entity.posY;
		z = entity.posZ;
	}

	public DataFloatLocation(int dim, double x, double y, double z) {
		this.dim = dim;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public DataFloatLocation(WarpPoint point) {
		dim = point.getDimension();
		x = point.getX();
		y = point.getY();
		z = point.getZ();
	}

}
