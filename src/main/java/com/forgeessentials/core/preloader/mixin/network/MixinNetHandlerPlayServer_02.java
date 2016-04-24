package com.forgeessentials.core.preloader.mixin.network;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.netty.buffer.Unpooled;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraftforge.permission.PermissionManager;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer_02 implements INetHandlerPlayServer, ITickable {

	private static final Logger logger = LogManager.getLogger(NetHandlerPlayServer.class);

	@Shadow
	public EntityPlayerMP playerEntity;

	@Shadow
	public MinecraftServer serverController;

	@Override
	@Overwrite
	public void processVanilla250Packet(C17PacketCustomPayload packetIn) {
		PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, playerEntity.getServerForPlayer());
		PacketBuffer packetbuffer;
		ItemStack itemstack;
		ItemStack itemstack1;

		if ("MC|BEdit".equals(packetIn.getChannelName())) {
			packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(packetIn.getBufferData()));

			try {
				itemstack = packetbuffer.readItemStackFromBuffer();

				if (itemstack != null) {
					if (!ItemWritableBook.isNBTValid(itemstack.getTagCompound())) {
						throw new IOException("Invalid book tag!");
					}

					itemstack1 = playerEntity.inventory.getCurrentItem();

					if (itemstack1 == null) {
						return;
					}

					if ((itemstack.getItem() == Items.writable_book) && (itemstack.getItem() == itemstack1.getItem())) {
						itemstack1.setTagInfo("pages", itemstack.getTagCompound().getTagList("pages", 8));
					}

					return;
				}
			} catch (Exception exception4) {
				logger.error("Couldn\'t handle book info", exception4);
				return;
			} finally {
				packetbuffer.release();
			}

			return;
		} else if ("MC|BSign".equals(packetIn.getChannelName())) {
			packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(packetIn.getBufferData()));

			try {
				itemstack = packetbuffer.readItemStackFromBuffer();

				if (itemstack == null) {
					return;
				}

				if (!ItemEditableBook.validBookTagContents(itemstack.getTagCompound())) {
					throw new IOException("Invalid book tag!");
				}

				itemstack1 = playerEntity.inventory.getCurrentItem();

				if (itemstack1 != null) {
					if ((itemstack.getItem() == Items.written_book) && (itemstack1.getItem() == Items.writable_book)) {
						itemstack1.setTagInfo("author", new NBTTagString(playerEntity.getName()));
						itemstack1.setTagInfo("title", new NBTTagString(itemstack.getTagCompound().getString("title")));
						itemstack1.setTagInfo("pages", itemstack.getTagCompound().getTagList("pages", 8));
						itemstack1.setItem(Items.written_book);
					}

					return;
				}
			} catch (Exception exception3) {
				logger.error("Couldn\'t sign book", exception3);
				return;
			} finally {
				packetbuffer.release();
			}

			return;
		} else if ("MC|TrSel".equals(packetIn.getChannelName())) {
			try {
				int i = packetIn.getBufferData().readInt();
				Container container = playerEntity.openContainer;

				if (container instanceof ContainerMerchant) {
					((ContainerMerchant) container).setCurrentRecipeIndex(i);
				}
			} catch (Exception exception2) {
				logger.error("Couldn\'t select trade", exception2);
			}
		} else if ("MC|AdvCdm".equals(packetIn.getChannelName())) {
			if (!serverController.isCommandBlockEnabled()) {
				playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notEnabled", new Object[0]));
			} else if (PermissionManager.checkPermission(playerEntity, PermissionManager.PERM_COMMANDBLOCK)) // &&
																												// playerEntity.capabilities.isCreativeMode)
			{
				packetbuffer = packetIn.getBufferData();

				try {
					byte b0 = packetbuffer.readByte();
					CommandBlockLogic commandblocklogic = null;

					if (b0 == 0) {
						TileEntity tileentity = playerEntity.worldObj.getTileEntity(
								new BlockPos(packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt()));

						if (tileentity instanceof TileEntityCommandBlock) {
							commandblocklogic = ((TileEntityCommandBlock) tileentity).getCommandBlockLogic();
						}
					} else if (b0 == 1) {
						Entity entity = playerEntity.worldObj.getEntityByID(packetbuffer.readInt());

						if (entity instanceof EntityMinecartCommandBlock) {
							commandblocklogic = ((EntityMinecartCommandBlock) entity).getCommandBlockLogic();
						}
					}

					String s1 = packetbuffer.readStringFromBuffer(packetbuffer.readableBytes());
					boolean flag = packetbuffer.readBoolean();

					if (commandblocklogic != null) {
						commandblocklogic.setCommand(s1);
						commandblocklogic.setTrackOutput(flag);

						if (!flag) {
							commandblocklogic.setLastOutput((IChatComponent) null);
						}

						commandblocklogic.updateCommand();
						playerEntity.addChatMessage(
								new ChatComponentTranslation("advMode.setCommand.success", new Object[] { s1 }));
					}
				} catch (Exception exception1) {
					logger.error("Couldn\'t set command block", exception1);
				} finally {
					packetbuffer.release();
				}
			} else {
				playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notAllowed", new Object[0]));
			}
		} else if ("MC|Beacon".equals(packetIn.getChannelName())) {
			if (playerEntity.openContainer instanceof ContainerBeacon) {
				try {
					packetbuffer = packetIn.getBufferData();
					int j = packetbuffer.readInt();
					int k = packetbuffer.readInt();
					ContainerBeacon containerbeacon = (ContainerBeacon) playerEntity.openContainer;
					Slot slot = containerbeacon.getSlot(0);

					if (slot.getHasStack()) {
						slot.decrStackSize(1);
						IInventory iinventory = containerbeacon.func_180611_e();
						iinventory.setField(1, j);
						iinventory.setField(2, k);
						iinventory.markDirty();
					}
				} catch (Exception exception) {
					logger.error("Couldn\'t set beacon", exception);
				}
			}
		} else if ("MC|ItemName".equals(packetIn.getChannelName())
				&& (playerEntity.openContainer instanceof ContainerRepair)) {
			ContainerRepair containerrepair = (ContainerRepair) playerEntity.openContainer;

			if ((packetIn.getBufferData() != null) && (packetIn.getBufferData().readableBytes() >= 1)) {
				String s = ChatAllowedCharacters
						.filterAllowedCharacters(packetIn.getBufferData().readStringFromBuffer(32767));

				if (s.length() <= 30) {
					containerrepair.updateItemName(s);
				}
			} else {
				containerrepair.updateItemName("");
			}
		}
	}

}
