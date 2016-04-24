package com.forgeessentials.core.preloader.mixin.server.management;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.fe.event.player.PlayerPostInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemInWorldManager.class)
public abstract class MixinItemInWorldManager
{

    @Shadow
    private EntityPlayerMP thisPlayerMP;

    @Shadow
    private World theWorld;

    @Shadow
    abstract boolean isCreative();

    // Fixes a few Forge bugs, and adds PlayerPostInteractEvent.
    @Overwrite
    public boolean activateBlockOrUseItem(EntityPlayer player, World world, ItemStack item, BlockPos pos, EnumFacing side, float dx, float dy, float dz)
    {
        PlayerInteractEvent event = ForgeEventFactory.onPlayerInteract(player, Action.RIGHT_CLICK_BLOCK, world, pos, side, null);
        if (event.isCanceled())
        {
            // PATCH: Fix a Forge bug related to fake players
            if (thisPlayerMP.playerNetServerHandler != null)
                thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(theWorld, pos));
            return false;
        }

        // PATCH: Fix a Forge bug allowing onItemUseFirst to trigger even if event.useItem is set to DENY
        if (event.useItem != Event.Result.DENY && item != null && item.getItem().onItemUseFirst(item, player, world, pos, side, dx, dy, dz))
        {
            // PATCH: Add event to get actual result of interaction
            MinecraftForge.EVENT_BUS.post(new PlayerPostInteractEvent(player, world, item, pos, side, dx, dy, dz));
            if (item.stackSize <= 0)
                ForgeEventFactory.onPlayerDestroyItem(thisPlayerMP, item);
            return true;
        }

        Block block = world.getBlockState(pos).getBlock();
        boolean useBlock = !player.isSneaking() || player.getHeldItem() == null;
        if (!useBlock)
            useBlock = player.getHeldItem().getItem().doesSneakBypassUse(world, pos, player);
        boolean result = false;

        if (useBlock)
        {
            if (event.useBlock != Event.Result.DENY)
            {
                result = block.onBlockActivated(world, pos, world.getBlockState(pos), player, side, dx, dy, dz);
                // PATCH: Add event to get actual result of interaction
                if (result)
                    MinecraftForge.EVENT_BUS.post(new PlayerPostInteractEvent(player, world, block.getDefaultState(), pos, side, dx, dy, dz));
            }
            else
            {
                // PATCH: Fix a Forge bug related to fake players
                if (thisPlayerMP.playerNetServerHandler != null)
                    thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(theWorld, pos));
                result = event.useItem != Event.Result.ALLOW;
            }
        }

        if (item != null && !result && event.useItem != Event.Result.DENY)
        {
            int meta = item.getItemDamage();
            int size = item.stackSize;
            result = item.onItemUse(player, world, pos, side, dx, dy, dz);
            if (isCreative())
            {
                item.setItemDamage(meta);
                item.stackSize = size;
            }
            if (item.stackSize <= 0)
                ForgeEventFactory.onPlayerDestroyItem(thisPlayerMP, item);
            if (result)
                MinecraftForge.EVENT_BUS.post(new PlayerPostInteractEvent(player, world, item, pos, side, dx, dy, dz));
        }

        return result;
    }
}
