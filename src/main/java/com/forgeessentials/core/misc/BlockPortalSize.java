package com.forgeessentials.core.misc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockPortalSize {

	private final World field_150867_a;
	private final EnumFacing.Axis field_150865_b;
	private final EnumFacing field_150866_c;
	private final EnumFacing field_150863_d;
	private BlockPos field_150861_f;
	private int field_150862_g;
	private int field_150868_h;

	public BlockPortalSize(World worldIn, BlockPos p_i45694_2_, EnumFacing.Axis p_i45694_3_) {
		field_150867_a = worldIn;
		field_150865_b = p_i45694_3_;

		if (p_i45694_3_ == EnumFacing.Axis.X) {
			field_150863_d = EnumFacing.EAST;
			field_150866_c = EnumFacing.WEST;
		} else {
			field_150863_d = EnumFacing.NORTH;
			field_150866_c = EnumFacing.SOUTH;
		}

		for (BlockPos blockpos1 = p_i45694_2_; (p_i45694_2_.getY() > (blockpos1.getY() - 21))
				&& (p_i45694_2_.getY() > 0) && func_150857_a(
						worldIn.getBlockState(p_i45694_2_.down()).getBlock()); p_i45694_2_ = p_i45694_2_.down()) {
			;
		}

		int i = func_180120_a(p_i45694_2_, field_150863_d) - 1;

		if (i >= 0) {
			field_150861_f = p_i45694_2_.offset(field_150863_d, i);
			field_150868_h = func_180120_a(field_150861_f, field_150866_c);

			if ((field_150868_h < 2) || (field_150868_h > 21)) {
				field_150861_f = null;
				field_150868_h = 0;
			}
		}

		if (field_150861_f != null) {
			field_150862_g = func_150858_a();
		}
	}

	protected boolean func_150857_a(Block p_150857_1_) {
		return (p_150857_1_.getMaterial() == Material.air) || (p_150857_1_ == Blocks.fire)
				|| (p_150857_1_ == Blocks.portal);
	}

	protected int func_150858_a() {
		int i;
		label56:

		for (field_150862_g = 0; field_150862_g < 21; ++field_150862_g) {
			for (i = 0; i < field_150868_h; ++i) {
				BlockPos blockpos = field_150861_f.offset(field_150866_c, i).up(field_150862_g);
				Block block = field_150867_a.getBlockState(blockpos).getBlock();

				if (!func_150857_a(block)) {
					break label56;
				}

				if (block == Blocks.portal) {
				}

				if (i == 0) {
					block = field_150867_a.getBlockState(blockpos.offset(field_150863_d)).getBlock();

					if (block != Blocks.obsidian) {
						break label56;
					}
				} else if (i == (field_150868_h - 1)) {
					block = field_150867_a.getBlockState(blockpos.offset(field_150866_c)).getBlock();

					if (block != Blocks.obsidian) {
						break label56;
					}
				}
			}
		}

		for (i = 0; i < field_150868_h; ++i) {
			if (field_150867_a.getBlockState(field_150861_f.offset(field_150866_c, i).up(field_150862_g))
					.getBlock() != Blocks.obsidian) {
				field_150862_g = 0;
				break;
			}
		}

		if ((field_150862_g <= 21) && (field_150862_g >= 3)) {
			return field_150862_g;
		} else {
			field_150861_f = null;
			field_150868_h = 0;
			field_150862_g = 0;
			return 0;
		}
	}

	public void func_150859_c() {
		for (int i = 0; i < field_150868_h; ++i) {
			BlockPos blockpos = field_150861_f.offset(field_150866_c, i);

			for (int j = 0; j < field_150862_g; ++j) {
				field_150867_a.setBlockState(blockpos.up(j),
						Blocks.portal.getDefaultState().withProperty(BlockPortal.AXIS, field_150865_b), 2);
			}
		}
	}

	public boolean func_150860_b() {
		return (field_150861_f != null) && (field_150868_h >= 2) && (field_150868_h <= 21) && (field_150862_g >= 3)
				&& (field_150862_g <= 21);
	}

	protected int func_180120_a(BlockPos p_180120_1_, EnumFacing p_180120_2_) {
		int i;

		for (i = 0; i < 22; ++i) {
			BlockPos blockpos1 = p_180120_1_.offset(p_180120_2_, i);

			if (!func_150857_a(field_150867_a.getBlockState(blockpos1).getBlock())
					|| (field_150867_a.getBlockState(blockpos1.down()).getBlock() != Blocks.obsidian)) {
				break;
			}
		}

		Block block = field_150867_a.getBlockState(p_180120_1_.offset(p_180120_2_, i)).getBlock();
		return block == Blocks.obsidian ? i : 0;
	}

}