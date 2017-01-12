package com.forgeessentials.core.preloader.mixin.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.entity.EntityPortalEvent;

@Mixin(BlockPortal.class)
public class MixinBlockPortal {

	@Overwrite
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if ((entityIn.ridingEntity == null) && (entityIn.riddenByEntity == null)) { // TODO:
																					// get
																					// target
																					// coordinates
																					// somehow
			if (!worldIn.isRemote && MinecraftForge.EVENT_BUS.post(new EntityPortalEvent(entityIn, worldIn, pos,
					entityIn.dimension == -1 ? 0 : -1, new BlockPos(0, 0, 0)))) {
				return;
			}
			entityIn.setPortal(pos);
		}
	}

}
