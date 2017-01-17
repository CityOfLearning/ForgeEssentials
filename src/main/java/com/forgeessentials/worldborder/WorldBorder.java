package com.forgeessentials.worldborder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.AreaShape;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.data.v2.Loadable;
import com.google.gson.annotations.Expose;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class WorldBorder implements Loadable {

	public static WorldBorder load(World world) {
		// TODO: Better way to identify dimensions
		String key = Integer.toString(world.provider.getDimensionId());
		return DataManager.getInstance().load(WorldBorder.class, key);
	}

	private boolean enabled = false;

	private Point center;

	private Point size;

	private AreaShape shape = AreaShape.BOX;

	private List<WorldBorderEffect> effects = new ArrayList<>();

	int dimID;

	@Expose(serialize = false)
	private AreaBase area;

	@Expose(serialize = false)
	private Map<EntityPlayer, Set<WorldBorderEffect>> activeEffects = new WeakHashMap<>();

	public WorldBorder(Point center, int xSize, int zSize, int dimID) {
		this.center = center;
		size = new Point(xSize, 0, zSize);
		this.dimID = dimID;
		updateArea();
	}

	public void addEffect(WorldBorderEffect effect) {
		effects.add(effect);
	}

	@Override
	public void afterLoad() {
		if (effects == null) {
			effects = new ArrayList<>();
		}
		for (Iterator<WorldBorderEffect> iterator = effects.iterator(); iterator.hasNext();) {
			if (iterator.next() == null) {
				iterator.remove();
			}
		}
		activeEffects = new WeakHashMap<>();
		updateArea();
	}

	public Set<WorldBorderEffect> getActiveEffects(EntityPlayer player) {
		return activeEffects.get(player);
	}

	public AreaBase getArea() {
		return area;
	}

	public Point getCenter() {
		return center;
	}

	public List<WorldBorderEffect> getEffects() {
		return effects;
	}

	public Set<WorldBorderEffect> getOrCreateActiveEffects(EntityPlayer player) {
		Set<WorldBorderEffect> effects = activeEffects.get(player);
		if (effects == null) {
			effects = new HashSet<>();
			activeEffects.put(player, effects);
		}
		return effects;
	}

	public AreaShape getShape() {
		return shape;
	}

	public Point getSize() {
		return size;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void save() {
		// TODO: Better way to identify dimensions
		String key = Integer.toString(dimID);
		DataManager.getInstance().save(this, key);
	}

	public void setCenter(Point center) {
		this.center = center;
		updateArea();
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setShape(AreaShape shape) {
		this.shape = shape;
	}

	public void setSize(Point size) {
		this.size = size;
		updateArea();
	}

	public void updateArea() {
		Point minP = new Point( //
				center.getX() - size.getX(), //
				center.getY() - size.getY(), //
				center.getZ() - size.getZ());
		Point maxP = new Point( //
				center.getX() + size.getX(), //
				center.getY() + size.getY(), //
				center.getZ() + size.getZ());
		area = new AreaBase(minP, maxP);
	}

}
