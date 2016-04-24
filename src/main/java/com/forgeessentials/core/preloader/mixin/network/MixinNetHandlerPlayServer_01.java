package com.forgeessentials.core.preloader.mixin.network;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.world.SignEditEvent;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer_01 implements INetHandlerPlayServer, ITickable {

	@Shadow
	public MinecraftServer serverController;

	@Shadow
	public EntityPlayerMP playerEntity;

	private IChatComponent[] onSignEditEvent(C12PacketUpdateSign data, EntityPlayerMP player) {
		SignEditEvent e = new SignEditEvent(data.getPosition(), data.getLines(), player);
		if (MinecraftForge.EVENT_BUS.post(e)) {
			return null;
		}
		return e.text;

	}

	@Override
	@Overwrite
	public void processUpdateSign(C12PacketUpdateSign packetIn) {
		PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, playerEntity.getServerForPlayer());
		playerEntity.markPlayerActive();
		WorldServer worldserver = serverController.worldServerForDimension(playerEntity.dimension);
		BlockPos blockpos = packetIn.getPosition();

		if (worldserver.isBlockLoaded(blockpos)) {
			TileEntity tileentity = worldserver.getTileEntity(blockpos);

			if (!(tileentity instanceof TileEntitySign)) {
				return;
			}

			TileEntitySign tileentitysign = (TileEntitySign) tileentity;

			if (!tileentitysign.getIsEditable() || (tileentitysign.getPlayer() != playerEntity)) {
				serverController
						.logWarning("Player " + playerEntity.getName() + " just tried to change non-editable sign");
				return;
			}

			IChatComponent[] aichatcomponent = onSignEditEvent(packetIn, playerEntity);
			if (aichatcomponent == null) {
				return;
			} // FE: sign edit event

			for (int i = 0; i < aichatcomponent.length; ++i) {
				tileentitysign.signText[i] = new ChatComponentText(
						EnumChatFormatting.getTextWithoutFormattingCodes(aichatcomponent[i].getUnformattedText()));
			}

			tileentitysign.markDirty();
			worldserver.markBlockForUpdate(blockpos);
		}
	}

}
