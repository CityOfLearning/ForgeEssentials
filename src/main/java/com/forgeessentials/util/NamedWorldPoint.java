package com.forgeessentials.util;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.selections.WorldPoint;
import com.google.gson.annotations.Expose;

import net.minecraft.entity.Entity;
import net.minecraft.world.WorldServer;

/**
 * Keeps a WorldPoint linked to a particular multiworld, even if the dim-id
 * changes
 */
public class NamedWorldPoint extends WorldPoint {

	protected String worldName;

	@Expose(serialize = false)
	protected boolean isLinked = false;

	@Expose(serialize = false)
	protected boolean isValid = true;

	public NamedWorldPoint(Entity entity) {
		super(entity);
		worldName = APIRegistry.namedWorldHandler.getWorldName(dim);
		isLinked();
	}

	public NamedWorldPoint(int dimension, int x, int y, int z) {
		super(dimension, x, y, z);
		worldName = APIRegistry.namedWorldHandler.getWorldName(dimension);
		isLinked();
	}

	public NamedWorldPoint(int dimension, String worldName, int x, int y, int z) {
		super(dimension, x, y, z);
		this.worldName = worldName;
		isLinked();
	}

	public NamedWorldPoint(String worldName, int x, int y, int z) {
		this(0, worldName, x, y, z);
	}

	public NamedWorldPoint(WorldPoint point) {
		this(point.getDimension(), point.getX(), point.getY(), point.getZ());
	}

	public boolean isLinked() {
		if (!isValid()) {
			return false;
		}
		return isLinked;
	}

	public boolean isValid() {
		if (!isValid) {
			if (worldName != null) {
				// If there is a name for the dimension, use it
				WorldServer world = APIRegistry.namedWorldHandler.getWorld(worldName);
				if (world != null) {
					dim = world.provider.getDimensionId();
					isLinked = true;
					isValid = true;
				}
			} else {
				// If no name was set, just use dimID
				isLinked = false;
				isValid = true;
			}
		}
		return isValid;
	}

}
