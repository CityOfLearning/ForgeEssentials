package com.forgeessentials.core.preloader.mixin.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.misc.BlockPortalSize;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.teleport.portal.Portal;
import com.forgeessentials.teleport.portal.PortalManager;

@Mixin(BlockPortal.class)
public abstract class MixinBlockPortal extends BlockPortal
{

    @Override
    @Overwrite
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
    {
        if (entity == null)
            return;
        Portal portal = null;
        if (entity != null && PortalManager.getInstance() != null)
            portal = PortalManager.getInstance().getPortalAt(new WorldPoint(entity.dimension, pos));
        if (portal == null)
        {
            if (entity.ridingEntity == null && entity.riddenByEntity == null)
                entity.setInPortal();
        }
        else if (!world.isRemote)
        {
            if (entity instanceof EntityPlayerMP)
                TeleportHelper.checkedTeleport((EntityPlayerMP) entity, portal.getTarget().toWarpPoint(entity.rotationPitch, entity.rotationYaw));
            else
                TeleportHelper.doTeleportEntity(entity, portal.getTarget().toWarpPoint(entity.rotationPitch, entity.rotationYaw));
        }
    }

    @Override
    @Overwrite
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side)
    {
    	EnumFacing.Axis axis = null;
        IBlockState iblockstate = world.getBlockState(pos);

        if (world.getBlockState(pos).getBlock() == this)
        {
            axis = (EnumFacing.Axis)iblockstate.getValue(AXIS);

            if (axis == null)
            {
                return false;
            }

            if (axis == EnumFacing.Axis.Z && side != EnumFacing.EAST && side != EnumFacing.WEST)
            {
                return false;
            }

            if (axis == EnumFacing.Axis.X && side != EnumFacing.SOUTH && side != EnumFacing.NORTH)
            {
                return false;
            }
        }
        return true;
    }

    /*@Override
    @Overwrite
    public boolean func_150000_e(World p_150000_1_, int p_150000_2_, int p_150000_3_, int p_150000_4_)
    {
        if (PortalManager.getInstance() != null
                && PortalManager.getInstance().getPortalAt(new WorldPoint(p_150000_1_, p_150000_2_, p_150000_3_, p_150000_4_)) == null)
        {
            BlockPortalSize size = new BlockPortalSize(p_150000_1_, p_150000_2_, p_150000_3_, p_150000_4_, 1);
            BlockPortalSize size1 = new BlockPortalSize(p_150000_1_, p_150000_2_, p_150000_3_, p_150000_4_, 2);

            if (size.func_150860_b() && size.field_150864_e == 0)
            {
                size.func_150859_c();
                return true;
            }
            else if (size1.func_150860_b() && size1.field_150864_e == 0)
            {
                size1.func_150859_c();
                return true;
            }
            else
            {
                return false;
            }
        }
        return false;
    }

    @Override
    @Overwrite
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        int l = p_149719_1_.getBlockMetadata(p_149719_2_, p_149719_3_, p_149719_4_) & 7;

        if (l == 0)
        {
            if (p_149719_1_.getBlock(p_149719_2_ - 1, p_149719_3_, p_149719_4_) != this
                    && p_149719_1_.getBlock(p_149719_2_ + 1, p_149719_3_, p_149719_4_) != this)
            {
                l = 2;
            }
            else if (p_149719_1_.getBlock(p_149719_2_, p_149719_3_ - 1, p_149719_4_) != this
                    && p_149719_1_.getBlock(p_149719_2_, p_149719_3_ + 1, p_149719_4_) != this)
            {
                l = 4;
            }
            else
            {
                l = 1;
            }

            if (p_149719_1_ instanceof World && !((World) p_149719_1_).isRemote)
            {
                ((World) p_149719_1_).setBlockMetadataWithNotify(p_149719_2_, p_149719_3_, p_149719_4_, l, 2);
            }
        }

        if (l == 1)
        {
            this.setBlockBounds(0, 0, 0.375F, 1, 1, 0.625F);
        }
        if (l == 2)
        {
            this.setBlockBounds(0.375F, 0, 0, 0.625F, 1, 1);
        }
        if (l == 4)
        {
            this.setBlockBounds(0, 0.375F, 0, 1, 0.625F, 1);
        }
    }

    @Override
    @Overwrite
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block  neighborBlock)
    {
        if (PortalManager.getInstance().getPortalAt(new WorldPoint(worldIn, pos)) == null)
        {
            int l = BlockPortal.func_149999_b(worldIn.getBlockMetadata(p_149695_2_, p_149695_3_, p_149695_4_));
            BlockPortalSize size = new BlockPortalSize(worldIn, pos);
            BlockPortalSize size1 = new BlockPortalSize(worldIn, p_149695_2_, p_149695_3_, p_149695_4_, 2);

            if (l == 1 && (!size.func_150860_b() || size.field_150864_e < size.field_150868_h * size.field_150862_g))
            {
                worldIn.setBlock(p_149695_2_, p_149695_3_, p_149695_4_, Blocks.air);
            }
            else if (l == 2 && (!size1.func_150860_b() || size1.field_150864_e < size1.field_150868_h * size1.field_150862_g))
            {
                worldIn.setBlock(p_149695_2_, p_149695_3_, p_149695_4_, Blocks.air);
            }
            else if (l == 0 && !size.func_150860_b() && !size1.func_150860_b())
            {
                worldIn.setBlock(p_149695_2_, p_149695_3_, p_149695_4_, Blocks.air);
            }
        }
    }*/

}
