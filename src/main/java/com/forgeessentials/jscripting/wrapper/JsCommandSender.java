package com.forgeessentials.jscripting.wrapper;

import java.util.UUID;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class JsCommandSender extends JsWrapper<ICommandSender> {

	private JsEntityPlayer player;

	public JsCommandSender(EntityPlayer player, JsEntityPlayer jsPlayer) {
		super(player);
		this.player = jsPlayer;
	}

	public JsCommandSender(ICommandSender sender) {
		super(sender);
	}

	public void chatConfirm(String message) {
		ChatOutputHandler.chatConfirmation(that, message);
	}

	public void chatError(String message) {
		ChatOutputHandler.chatError(that, message);
	}

	public void chatNotification(String message) {
		ChatOutputHandler.chatNotification(that, message);
	}

	public void chatWarning(String message) {
		ChatOutputHandler.chatWarning(that, message);
	}

	public JsCommandSender doAs(Object userIdOrPlayer, boolean hideChatOutput) {
		UserIdent doAsUser = userIdOrPlayer instanceof UUID ? UserIdent.get((UUID) userIdOrPlayer)
				: userIdOrPlayer instanceof JsEntityPlayer ? UserIdent.get(((JsEntityPlayer) userIdOrPlayer).getThat())
						: APIRegistry.IDENT_SERVER;
		DoAsCommandSender result = new DoAsCommandSender(doAsUser, that);
		result.setHideChatMessages(hideChatOutput);
		return new JsCommandSender(result);
	}

	public String getName() {
		return that.getName();
	}

	public JsEntityPlayer getPlayer() {
		if ((player != null) || !(that instanceof EntityPlayer)) {
			return player;
		}
		return player = new JsEntityPlayer((EntityPlayer) that, this);
	}

}
