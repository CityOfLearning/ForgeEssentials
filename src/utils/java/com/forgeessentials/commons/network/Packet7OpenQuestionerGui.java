package com.forgeessentials.commons.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class Packet7OpenQuestionerGui implements IMessage {
	private String message;

	public Packet7OpenQuestionerGui() {

	}

	public Packet7OpenQuestionerGui(String message) {
		this.message = message;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		message = ByteBufUtils.readUTF8String(buf);
	}

	public String getMessage() {
		return message;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, message);
	}
}
