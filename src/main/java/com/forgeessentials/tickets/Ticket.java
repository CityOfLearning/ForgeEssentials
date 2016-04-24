package com.forgeessentials.tickets;

import com.forgeessentials.commons.selections.WarpPoint;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class Ticket {

	public int id;

	public String creator;

	public String category;

	public String message;

	public WarpPoint point;

	public Ticket(ICommandSender sender, String category, String message) {
		id = ModuleTickets.getNextID();
		creator = sender.getName();
		if (sender instanceof EntityPlayer) {
			point = new WarpPoint((EntityPlayer) sender);
		}
		this.category = category;
		this.message = message;
	}

}
