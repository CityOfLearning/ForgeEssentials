package com.forgeessentials.commons.network;

import com.forgeessentials.commons.selections.PlotArea;
import com.forgeessentials.commons.selections.Point;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class Packet4PlotsUpdate implements IMessage {
	private PlotArea plot;
	private int owner;
	private boolean add; // add or delete the plot

	public Packet4PlotsUpdate() {
	}

	public Packet4PlotsUpdate(PlotArea area, int owner, boolean addDel) {
		plot = area;
		this.owner = owner;
		add = addDel;
	}

	@Override
	public void fromBytes(ByteBuf byteBuf) {
		plot = new PlotArea(ByteBufUtils.readUTF8String(byteBuf), byteBuf.readInt(),
				new Point(byteBuf.readInt(), byteBuf.readInt(), byteBuf.readInt()),
				new Point(byteBuf.readInt(), byteBuf.readInt(), byteBuf.readInt()));

		owner = byteBuf.readInt();
		add = byteBuf.readBoolean();
	}

	public PlotArea getArea() {
		return plot;
	}

	public int getOwnership() {
		return owner;
	}

	public boolean shouldAdd() {
		// false is delete, true is add
		return add;
	}

	@Override
	public void toBytes(ByteBuf byteBuf) {
		ByteBufUtils.writeUTF8String(byteBuf, plot.getName());

		byteBuf.writeInt(plot.getDimension());

		byteBuf.writeInt(plot.getHighPoint().getX());
		byteBuf.writeInt(plot.getHighPoint().getY());
		byteBuf.writeInt(plot.getHighPoint().getZ());

		byteBuf.writeInt(plot.getLowPoint().getX());
		byteBuf.writeInt(plot.getLowPoint().getY());
		byteBuf.writeInt(plot.getLowPoint().getZ());

		byteBuf.writeInt(owner);

		byteBuf.writeBoolean(add);
	}
}
