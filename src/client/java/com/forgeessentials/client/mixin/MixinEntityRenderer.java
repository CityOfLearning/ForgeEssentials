package com.forgeessentials.client.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer implements IResourceManagerReloadListener {

	@Shadow
	private Minecraft mc;

	@Shadow
	private Entity pointedEntity;

	@Overwrite
	public void getMouseOver(float partialTime) {
		if (mc.renderViewEntity != null) {
			if (mc.theWorld != null) {
				mc.pointedEntity = null;

				double maxReach = mc.playerController.getBlockReachDistance();
				double blockDistance = maxReach;
				if (mc.playerController.extendedReach()) {
					maxReach = 6.0D;
					blockDistance = 6.0D;
				} else {
					if (maxReach > 3.0D) {
						blockDistance = 3.0D;
					}

					maxReach = blockDistance;
				}

				// d0 = 20;
				// d1 = 20;

				Vec3 startPos = mc.renderViewEntity.getPosition(partialTime);
				mc.objectMouseOver = mc.renderViewEntity.rayTrace(maxReach, partialTime);
				if (mc.objectMouseOver != null) {
					blockDistance = mc.objectMouseOver.hitVec.distanceTo(startPos);
				}

				Vec3 vec31 = mc.renderViewEntity.getLook(partialTime);
				Vec3 vec32 = startPos.addVector(vec31.xCoord * maxReach, vec31.yCoord * maxReach,
						vec31.zCoord * maxReach);
				pointedEntity = null;
				Vec3 vec33 = null;
				float f1 = 1.0F;
				List<?> list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.renderViewEntity,
						mc.renderViewEntity.boundingBox
								.addCoord(vec31.xCoord * maxReach, vec31.yCoord * maxReach, vec31.zCoord * maxReach)
								.expand(f1, f1, f1));
				double entityDistance = blockDistance;

				for (int i = 0; i < list.size(); ++i) {
					Entity entity = (Entity) list.get(i);

					if (entity.canBeCollidedWith()) {
						float f2 = entity.getCollisionBorderSize();
						AxisAlignedBB axisalignedbb = entity.boundingBox.expand(f2, f2, f2);
						MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(startPos, vec32);

						if (axisalignedbb.isVecInside(startPos)) {
							if ((0.0D < entityDistance) || (entityDistance == 0.0D)) {
								pointedEntity = entity;
								vec33 = movingobjectposition == null ? startPos : movingobjectposition.hitVec;
								entityDistance = 0.0D;
							}
						} else if (movingobjectposition != null) {
							double d3 = startPos.distanceTo(movingobjectposition.hitVec);

							if ((d3 < entityDistance) || (entityDistance == 0.0D)) {
								if ((entity == mc.renderViewEntity.ridingEntity) && !entity.canRiderInteract()) {
									if (entityDistance == 0.0D) {
										pointedEntity = entity;
										vec33 = movingobjectposition.hitVec;
									}
								} else {
									pointedEntity = entity;
									vec33 = movingobjectposition.hitVec;
									entityDistance = d3;
								}
							}
						}
					}
				}

				if ((pointedEntity != null) && ((entityDistance < blockDistance) || (mc.objectMouseOver == null))) {
					mc.objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);

					if ((pointedEntity instanceof EntityLivingBase) || (pointedEntity instanceof EntityItemFrame)) {
						mc.pointedEntity = pointedEntity;
					}
				}
			}
		}
	}

}
