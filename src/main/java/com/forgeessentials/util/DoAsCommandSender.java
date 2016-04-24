package com.forgeessentials.util;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;

import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class DoAsCommandSender implements ICommandSender {

	protected ICommandSender sender;

	protected UserIdent ident;

	protected boolean hideChatMessages;

	public DoAsCommandSender() {
		ident = APIRegistry.IDENT_SERVER;
		sender = MinecraftServer.getServer();
	}

	public DoAsCommandSender(UserIdent ident) {
		this.ident = ident;
		sender = MinecraftServer.getServer();
	}

	public DoAsCommandSender(UserIdent ident, ICommandSender sender) {
		this.ident = ident;
		this.sender = sender;
	}

	@Override
	public void addChatMessage(IChatComponent message) {
		if (!hideChatMessages) {
			sender.addChatMessage(message);
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(int level, String command) {
		return true;
	}

	@Override
	public Entity getCommandSenderEntity() {
		return sender.getCommandSenderEntity();
	}

	@Override
	public IChatComponent getDisplayName() {
		return sender.getDisplayName();
	}

	@Override
	public World getEntityWorld() {
		return sender.getEntityWorld();
	}

	public UserIdent getIdent() {
		return ident;
	}

	@Override
	public String getName() {
		return sender.getName();
	}

	public ICommandSender getOriginalSender() {
		return sender;
	}

	@Override
	public BlockPos getPosition() {
		return sender.getPosition();
	}

	@Override
	public Vec3 getPositionVector() {
		return sender.getPositionVector();
	}

	public UserIdent getUserIdent() {
		return ident;
	}

	public boolean isHideChatMessages() {
		return hideChatMessages;
	}

	@Override
	public boolean sendCommandFeedback() {
		return sender.sendCommandFeedback();
	}

	@Override
	public void setCommandStat(Type p_174794_1_, int p_174794_2_) {
		sender.setCommandStat(p_174794_1_, p_174794_2_);
	}

	public void setHideChatMessages(boolean hideChatMessages) {
		this.hideChatMessages = hideChatMessages;
	}

	public void setIdent(UserIdent ident) {
		this.ident = ident;
	}

}