package com.forgeessentials.client.handler;

import com.forgeessentials.commons.network.Packet7OpenQuestionerGui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Just a utility class. Pressing the buttons while there is no question asked
 * will only give you an error message.
 */
public class QuestionerHandler implements GuiYesNoCallback, IMessageHandler<Packet7OpenQuestionerGui, IMessage> {

	private boolean clicked;

	public QuestionerHandler() {
	}

	@Override
	public void confirmClicked(boolean result, int id) {
		if (result) {
			FMLClientHandler.instance().getClientPlayerEntity().sendChatMessage("/yes");
		} else {
			FMLClientHandler.instance().getClientPlayerEntity().sendChatMessage("/no");
		}
		clicked = true;
		Minecraft.getMinecraft().setIngameFocus();
	}

	@Override
	public IMessage onMessage(Packet7OpenQuestionerGui message, MessageContext ctx) {
		clicked = false;
		Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo(this, "Warning", message.getMessage(), 1) {
			@Override
			public void onGuiClosed() {
				if (!clicked) {
					FMLClientHandler.instance().getClientPlayerEntity().sendChatMessage("/no");
				}
			}
		});
		return null;
	}
}
