package com.forgeessentials.commons.selections;

import com.google.gson.annotations.Expose;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class WarpPoint {

	protected int dim;

	protected float pitch;

	protected float yaw;

	protected double xd;

	protected double yd;

	protected double zd;

	@Expose(serialize = false)
	protected WorldServer world;

	// ------------------------------------------------------------

	public WarpPoint(Entity entity) {
		this(entity.worldObj instanceof WorldServer ? (WorldServer) entity.worldObj : null, entity.posX, entity.posY,
				entity.posZ, entity.rotationPitch, entity.rotationYaw);
	}

	public WarpPoint(int dimension, BlockPos location, float pitch, float yaw) {
		this(dimension, location.getX() + 0.5, location.getY(), location.getZ() + 0.5, pitch, yaw);
	}

	public WarpPoint(int dimension, double x, double y, double z, float playerPitch, float playerYaw) {
		dim = dimension;
		xd = x;
		yd = y;
		zd = z;
		pitch = playerPitch;
		yaw = playerYaw;
	}

	public WarpPoint(Point point, int dimension, float pitch, float yaw) {
		this(dimension, point.getX(), point.getY(), point.getZ(), pitch, yaw);
	}

	public WarpPoint(WarpPoint point) {
		this(point.dim, point.xd, point.yd, point.zd, point.pitch, point.yaw);
	}

	public WarpPoint(WorldPoint point) {
		this(point, 0, 0);
	}

	public WarpPoint(WorldPoint point, float pitch, float yaw) {
		this(point.getDimension(), point.getX() + 0.5, point.getY(), point.getZ() + 0.5, pitch, yaw);
	}

	public WarpPoint(WorldServer world, double x, double y, double z, float playerPitch, float playerYaw) {
		this.world = world;
		dim = world.provider.getDimensionId();
		xd = x;
		yd = y;
		zd = z;
		pitch = playerPitch;
		yaw = playerYaw;
	}

	// ------------------------------------------------------------

	/**
	 * Returns the distance to another entity
	 */
	public double distance(Entity e) {
		return Math.sqrt(
				((xd - e.posX) * (xd - e.posX)) + ((yd - e.posY) * (yd - e.posY)) + ((zd - e.posZ) * (zd - e.posZ)));
	}

	/**
	 * Returns the distance to another point
	 */
	public double distance(WarpPoint v) {
		return Math.sqrt(((xd - v.xd) * (xd - v.xd)) + ((yd - v.yd) * (yd - v.yd)) + ((zd - v.zd) * (zd - v.zd)));
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof WarpPoint) {
			WarpPoint p = (WarpPoint) object;
			return (xd == p.xd) && (yd == p.yd) && (zd == p.zd);
		}
		if (object instanceof Point) {
			Point p = (Point) object;
			return ((int) xd == p.getX()) && ((int) yd == p.getY()) && ((int) zd == p.getZ());
		}
		if (object instanceof WorldPoint) {
			WorldPoint p = (WorldPoint) object;
			return (dim == p.getDimension()) && ((int) xd == p.getX()) && ((int) yd == p.getY())
					&& ((int) zd == p.getZ());
		}
		return false;
	}

	public BlockPos getBlockPos() {
		return new BlockPos(getBlockX(), getBlockY(), getBlockZ());
	}

	public int getBlockX() {
		return (int) Math.floor(xd);
	}

	public int getBlockY() {
		return (int) Math.floor(yd);
	}

	public int getBlockZ() {
		return (int) Math.floor(zd);
	}

	public int getDimension() {
		return dim;
	}

	public float getPitch() {
		return pitch;
	}

	public WorldServer getWorld() {
		if ((world == null) || (world.provider.getDimensionId() != dim)) {
			world = DimensionManager.getWorld(dim);
		}
		return world;
	}

	public double getX() {
		return xd;
	}

	public double getY() {
		return yd;
	}

	public float getYaw() {
		return yaw;
	}

	public double getZ() {
		return zd;
	}

	@Override
	public int hashCode() {
		int h = 1 + Double.valueOf(xd).hashCode();
		h = (h * 31) + Double.valueOf(yd).hashCode();
		h = (h * 31) + Double.valueOf(zd).hashCode();
		h = (h * 31) + Double.valueOf(pitch).hashCode();
		h = (h * 31) + Double.valueOf(yaw).hashCode();
		h = (h * 31) + dim;
		return h;
	}

	/**
	 * Returns the length of this vector
	 */
	public double length() {
		return Math.sqrt((xd * xd) + (yd * yd) + (zd * zd));
	}

	public void setDimension(int dim) {
		this.dim = dim;
	}

	// ------------------------------------------------------------

	public void setPitch(float value) {
		pitch = value;
	}

	public void setX(double value) {
		xd = value;
	}

	public void setY(double value) {
		yd = value;
	}

	public void setYaw(float value) {
		yaw = value;
	}

	public void setZ(double value) {
		zd = value;
	}

	public String toReadableString() {
		return String.format("%.0f %.0f %.0f dim=%d", xd, yd, zd, dim);
	}

	// ------------------------------------------------------------

	@Override
	public String toString() {
		return "[" + xd + "," + yd + "," + zd + ",dim=" + dim + ",pitch=" + pitch + ",yaw=" + yaw + "]";
	}

	public Vec3 toVec3() {
		return new Vec3(xd, yd, zd);
	}

	public WorldPoint toWorldPoint() {
		return new WorldPoint(this);
	}

	public void validatePositiveY() {
		if (yd < 0) {
			yd = 0;
		}
	}

}
