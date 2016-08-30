package com.forgeessentials.jscripting.wrapper;

import java.util.List;

import com.forgeessentials.util.MappedList;

import net.minecraft.entity.player.EntityPlayer;

public class JsEntityPlayerList extends MappedList<EntityPlayer, JsEntityPlayer> {

	public JsEntityPlayerList(List<EntityPlayer> list) {
		super(list);
	}

	@Override
	protected JsEntityPlayer map(EntityPlayer in) {
		return new JsEntityPlayer(in);
	}

	@Override
	protected EntityPlayer unmap(JsEntityPlayer in) {
		return in.getThat();
	}

}