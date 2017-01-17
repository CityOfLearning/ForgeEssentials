package com.forgeessentials.commons.selections;

import net.minecraft.world.World;

public class PlotArea extends WorldArea {

	protected String name;

	public PlotArea(String name, int dim, AreaBase area) {
		super(dim, area.getHighPoint(), area.getLowPoint());
		this.name = name;
	}

	public PlotArea(String name, int dim, Point start, Point end) {
		super(dim, start, end);
		this.name = name;
	}

	public PlotArea(String name, World world, AreaBase area) {
		super(world, area.getHighPoint(), area.getLowPoint());
		this.name = name;
	}

	public PlotArea(String name, World world, Point start, Point end) {
		super(world, start, end);
		this.name = name;
	}

	public boolean contains(PlotArea area) {
		if (area.dim == dim) {
			return super.contains(area);
		} else {
			return false;
		}
	}

	@Override
	public boolean contains(WorldPoint point) {
		if (point.dim == dim) {
			return super.contains(point);
		} else {
			return false;
		}
	}

	@Override
	public WorldPoint getCenter() {
		return new WorldPoint(dim, (high.x + low.x) / 2, (high.y + low.y) / 2, (high.z + low.z) / 2);
	}

	public AreaBase getIntersection(PlotArea area) {
		if (area.dim == dim) {
			return super.getIntersection(area);
		} else {
			return null;
		}
	}

	public String getName() {
		return name;
	}

	public boolean intersectsWith(PlotArea area) {
		if (area.dim == dim) {
			return super.intersectsWith(area);
		} else {
			return false;
		}
	}

	public boolean makesCuboidWith(PlotArea area) {
		if (area.dim == dim) {
			return super.makesCuboidWith(area);
		} else {
			return false;
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return " { " + name + " , " + dim + " , " + getHighPoint().toString() + " , " + getLowPoint().toString() + " }";
	}

}
