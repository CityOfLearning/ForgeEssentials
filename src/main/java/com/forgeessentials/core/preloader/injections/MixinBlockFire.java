package com.forgeessentials.core.preloader.injections;

import com.forgeessentials.core.preloader.asminjector.annotation.Mixin;

import net.minecraft.block.BlockFire;

@Mixin(BlockFire.class)
public abstract class MixinBlockFire extends BlockFire {
	/*
	 * TODO update for 1.8.9
	 * 
	 * @Inject(target =
	 * "tryCatchFire(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;ILjava/util/Random;ILnet/minecraft/util/EnumFacing;)V",
	 * aliases = { "setBlockState=func_180501_a" }, at = @At(value = "INVOKE",
	 * target =
	 * "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z",
	 * shift = Shift.LAST_LABEL) ) public void fireDestroysBlockAndSpreads(World
	 * world, BlockPos pos, int chance, Random rnd, int spreadChance, EnumFacing
	 * face, CallbackInfo ci) { if (MinecraftForge.EVENT_BUS.post(new
	 * FireEvent.Destroy(x, y, z, world, this, argValue1))) { //
	 * System.out.println("Injector: Fire could not destroy block");
	 * ci.doReturn(); } else { // System.out.println(
	 * "Injector: Fire destroyed block and spread to below block -> Spread"); if
	 * (MinecraftForge.EVENT_BUS.post(new FireEvent.Spread(world, pos,
	 * pos.offset(face)))) { world.setBlockToAir(pos); ci.doReturn(); } } //
	 * System.out.println("Injector: Fire destroyed block replacing it"); }
	 * 
	 * @Inject(target =
	 * "tryCatchFire(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;ILjava/util/Random;ILnet/minecraft/util/EnumFacing;)V",
	 * aliases = { "setBlockToAir=func_175698_g" }, at = @At(value = "INVOKE",
	 * target =
	 * "Lnet/minecraft/world/World;setBlockToAir(Lnet/minecraft/util/BlockPos;)Z",
	 * shift = Shift.LAST_LABEL) ) public void
	 * fireDestroysBlockWithoutSpread(World world, BlockPos pos, int chance,
	 * Random rnd, int spreadChance, EnumFacing face, CallbackInfo ci) { //
	 * System.out.println("Injector: Fire destroyed block"); if
	 * (MinecraftForge.EVENT_BUS.post(new FireEvent.Destroy(world, pos))) {
	 * ci.doReturn(); } }
	 * 
	 * @Inject(target =
	 * "updateTick(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V",
	 * aliases = { "updateTick=func_180650_b", "setBlockState=func_180501_a" },
	 * at = @At(value = "INVOKE", target =
	 * "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z",
	 * shift = Shift.LAST_LABEL, ordinal = 1) ) public void fireSpread(World
	 * world, BlockPos source, IBlockState sourceBlock, Random rnd, CallbackInfo
	 * ci,
	 * 
	 * @Local BlockPos blockpos1) { // System.out.println(String.format(
	 * "Injector: Fire spreading to other block from [%s] to [%s]",
	 * source.toString(), blockpos1.toString())); if
	 * (MinecraftForge.EVENT_BUS.post(new FireEvent.Spread(world, blockpos1,
	 * source))) { ci.doReturn(); } }
	 */
}
