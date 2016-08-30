package com.forgeessentials.jscripting.wrapper;

import java.util.UUID;

import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.ServerUtil;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class JsEntity<T extends Entity> extends JsWrapper<T> {

	private JsWorld<World> world;

	private JsEntity<Entity> ridingEntity;

	private JsEntity<Entity> riddenByEntity;

	public JsEntity(T that) {
		super(that);
	}

	public String _getNbt() // tsgen ignore
	{
		return DataManager.toJson(that.getEntityData());
	}

	public void _setNbt(String value) // tsgen ignore
	{
		ServerUtil.copyNbt(that.getEntityData(), DataManager.fromJson(value, NBTTagCompound.class));
	}

	public int getChunkCoordX() {
		return that.chunkCoordX;
	}

	public int getChunkCoordY() {
		return that.chunkCoordY;
	}

	public int getChunkCoordZ() {
		return that.chunkCoordZ;
	}

	public int getDimension() {
		return that.dimension;
	}

	public int getEntityId() {
		return that.getEntityId();
	}

	public float getHeight() {
		return that.height;
	}

	public String getId() {
		return that.getPersistentID().toString();
	}

	public double getMotionX() {
		return that.motionX;
	}

	public double getMotionY() {
		return that.motionY;
	}

	public double getMotionZ() {
		return that.motionZ;
	}

	public String getName() {
		return that.getName();
	}

	public JsEntity<Entity> getRiddenByEntity() {
		if (riddenByEntity == null) {
			riddenByEntity = new JsEntity<>(that.riddenByEntity);
		}
		return riddenByEntity;
	}

	public JsEntity<Entity> getRidingEntity() {
		if (ridingEntity == null) {
			ridingEntity = new JsEntity<>(that.ridingEntity);
		}
		return ridingEntity;
	}

	public float getStepHeight() {
		return that.stepHeight;
	}

	public UUID getUuid() {
		return that.getPersistentID();
	}

	public float getWidth() {
		return that.width;
	}

	public JsWorld<World> getWorld() {
		if (world == null) {
			world = new JsWorld<>(that.worldObj);
		}
		return world;
	}

	public double getX() {
		return that.posX;
	}

	public double getY() {
		return that.posY;
	}

	public double getZ() {
		return that.posZ;
	}

	public boolean isOnGround() {
		return that.onGround;
	}

}
