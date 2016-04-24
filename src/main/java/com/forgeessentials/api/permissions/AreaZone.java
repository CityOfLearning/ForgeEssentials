package com.forgeessentials.api.permissions;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.AreaShape;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.events.EventCancelledException;
import com.google.gson.annotations.Expose;

/**
 * {@link AreaZone} covers just a specific area in one world. It has higher
 * priority than all other {@link Zone} types. Area zones can overlap. Priority
 * is then decided by assigning highest priority to the innermost, smallest
 * area.
 */
public class AreaZone extends Zone implements Comparable<AreaZone> {

	@Expose(serialize = false)
	protected WorldZone worldZone;

	private String name;

	private AreaBase area;

	private AreaShape shape = AreaShape.BOX;

	private int priority;

	private AreaZone(int id) {
		super(id);
	}

	public AreaZone(WorldZone worldZone, String name, AreaBase area) throws EventCancelledException {
		// Initialize basic AreaZone data
		this(worldZone.getServerZone().getMaxZoneID() + 1);
		this.worldZone = worldZone;
		this.name = name;
		this.area = area;

		// Check if the creation of the zone should be cancelled
		EventCancelledException.checkedPost(new PermissionEvent.Zone.Create(worldZone.getServerZone(), this),
				APIRegistry.getFEEventBus());

		// If not cancelled, inc the zoneID pointer and add the zone to the
		// world
		worldZone.getServerZone().nextZoneID();
		this.worldZone.addAreaZone(this);
	}

	public AreaZone(WorldZone worldZone, String name, AreaBase area, int id) {
		this(id);
		this.worldZone = worldZone;
		this.name = name;
		this.area = area;
		this.worldZone.addAreaZone(this);
	}

	@Override
	public int compareTo(AreaZone otherArea) {
		int cmp = otherArea.priority - priority;
		if (cmp != 0) {
			return cmp;
		}

		Point areaSize = otherArea.getArea().getSize();
		Point thisSize = getArea().getSize();
		cmp = (thisSize.getX() * thisSize.getY()) - (areaSize.getX() * areaSize.getY());
		if (cmp != 0) {
			return cmp;
		}

		return cmp;
	}

	public AreaBase getArea() {
		return area;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Zone getParent() {
		return worldZone;
	}

	public int getPriority() {
		return priority;
	}

	@Override
	public ServerZone getServerZone() {
		return worldZone.getServerZone();
	}

	public AreaShape getShape() {
		return shape;
	}

	public String getShortName() {
		return name;
	}

	public WorldArea getWorldArea() {
		return new WorldArea(worldZone.getDimensionID(), area);
	}

	public WorldZone getWorldZone() {
		return worldZone;
	}

	@Override
	public boolean isHidden() {
		String hiddenValue = getGroupPermission(GROUP_DEFAULT, FEPermissions.ZONE_HIDDEN);
		return (hiddenValue != null) && !PERMISSION_FALSE.equals(hiddenValue);
	}

	@Override
	public boolean isInZone(WorldArea otherArea) {
		if (!worldZone.isInZone(otherArea)) {
			return false;
		}
		return shape.contains(area, otherArea);
	}

	@Override
	public boolean isInZone(WorldPoint point) {
		if (!worldZone.isInZone(point)) {
			return false;
		}
		return shape.contains(area, point);
	}

	@Override
	public boolean isPartOfZone(WorldArea otherArea) {
		if (!worldZone.isPartOfZone(otherArea)) {
			return false;
		}
		return area.intersectsWith(otherArea);
	}

	public void setArea(AreaBase area) {
		this.area = area;
		setDirty();
		getWorldZone().sortAreaZones();
	}

	public void setHidden(boolean hidden) {
		if (hidden) {
			setGroupPermission(GROUP_DEFAULT, FEPermissions.ZONE_HIDDEN, hidden);
		} else {
			clearGroupPermission(GROUP_DEFAULT, FEPermissions.ZONE_HIDDEN);
		}
	}

	public void setPriority(int priority) {
		this.priority = priority;
		setDirty();
	}

	public void setShape(AreaShape shape) {
		if (shape == null) {
			this.shape = AreaShape.BOX;
		} else {
			this.shape = shape;
		}
		setDirty();
	}

	@Override
	public String toString() {
		return worldZone.getName() + "_" + name;
	}

}
