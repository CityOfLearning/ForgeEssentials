package com.forgeessentials.commons.selections;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.annotations.Expose;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.BlockEvent;

/**
 * Point which stores dimension as well
 */
public class WorldPoint extends Point {

	private static final Pattern fromStringPattern = Pattern
			.compile("\\s*\\[\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*,\\s*dim\\s*=\\s*(-?\\d+)\\s*\\]\\s*");

	public static WorldPoint create(ICommandSender sender) {
		return new WorldPoint(sender.getEntityWorld(), sender.getPosition());
	}

	// ------------------------------------------------------------

	public static WorldPoint fromString(String value) {
		Matcher m = fromStringPattern.matcher(value);
		if (m.matches()) {
			try {
				return new WorldPoint(Integer.parseInt(m.group(4)), Integer.parseInt(m.group(1)),
						Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)));
			} catch (NumberFormatException e) {
				/* do nothing */
			}
		}
		return null;
	}

	protected int dim;

	@Expose(serialize = false)
	protected World world;

	public WorldPoint(BlockEvent event) {
		this(event.world, event.pos);
	}

	public WorldPoint(Entity entity) {
		super(entity);
		dim = entity.dimension;
		world = entity.worldObj;
	}

	public WorldPoint(int dimension, BlockPos location) {
		this(dimension, location.getX(), location.getY(), location.getZ());
	}

	public WorldPoint(int dimension, int x, int y, int z) {
		super(x, y, z);
		dim = dimension;
	}

	public WorldPoint(int dimension, Point point) {
		this(dimension, point.x, point.y, point.z);
	}

	public WorldPoint(int dim, Vec3 vector) {
		super(vector);
		this.dim = dim;
	}

	public WorldPoint(WarpPoint other) {
		this(other.getDimension(), other.getBlockX(), other.getBlockY(), other.getBlockZ());
	}

	public WorldPoint(World world, BlockPos location) {
		this(world, location.getX(), location.getY(), location.getZ());
	}

	// ------------------------------------------------------------

	public WorldPoint(World world, int x, int y, int z) {
		super(x, y, z);
		dim = world.provider.getDimensionId();
		this.world = world;
	}

	public WorldPoint(WorldPoint other) {
		this(other.dim, other.x, other.y, other.z);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof WorldPoint) {
			WorldPoint p = (WorldPoint) object;
			return (dim == p.dim) && (x == p.x) && (y == p.y) && (z == p.z);
		}
		if (object instanceof WarpPoint) {
			WarpPoint p = (WarpPoint) object;
			return (dim == p.dim) && (x == p.getBlockX()) && (y == p.getBlockY()) && (z == p.getBlockZ());
		}
		return false;
	}

	public Block getBlock() {
		return getWorld().getBlockState(getBlockPos()).getBlock();
	}

	public int getDimension() {
		return dim;
	}

	public TileEntity getTileEntity() {
		return getWorld().getTileEntity(getBlockPos());
	}

	public World getWorld() {
		if ((world != null) && (world.provider.getDimensionId() != dim)) {
			return world;
		}
		world = DimensionManager.getWorld(dim);
		return world;
	}

	@Override
	public int hashCode() {
		int h = 1 + x;
		h = (h * 31) + y;
		h = (h * 31) + z;
		h = (h * 31) + dim;
		return h;
	}

	public void setDimension(int dim) {
		this.dim = dim;
	}

	// ------------------------------------------------------------

	@Override
	public WorldPoint setX(int x) {
		this.x = x;
		return this;
	}

	@Override
	public WorldPoint setY(int y) {
		this.y = y;
		return this;
	}

	@Override
	public WorldPoint setZ(int z) {
		this.z = z;
		return this;
	}

	@Override
	public String toString() {
		return "[" + x + "," + y + "," + z + ",dim=" + dim + "]";
	}

	public WarpPoint toWarpPoint(float rotationPitch, float rotationYaw) {
		return new WarpPoint(this, rotationPitch, rotationYaw);
	}

}
