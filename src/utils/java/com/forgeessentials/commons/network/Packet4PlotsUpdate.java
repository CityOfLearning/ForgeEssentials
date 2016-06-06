package com.forgeessentials.commons.network;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WorldArea;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class Packet4PlotsUpdate implements IMessage {
	private WorldArea area;
	private int owner;
	private boolean add; // add or delete the plot

	public Packet4PlotsUpdate() {
	}

	public Packet4PlotsUpdate(WorldArea area, int owner, boolean addDel) {
		this.area = area;
		this.owner = owner;
		this.add = addDel;
	}

	@Override
	public void fromBytes(ByteBuf byteBuf) {
		area = new WorldArea(byteBuf.readInt(),
				new Point(byteBuf.readInt(), byteBuf.readInt(), byteBuf.readInt()),
				new Point(byteBuf.readInt(), byteBuf.readInt(), byteBuf.readInt()));

		owner = byteBuf.readInt();
		add = byteBuf.readBoolean();
	}

	public WorldArea getArea() {
		return area;
	}

	public int getOwnership() {
		return owner;
	}

	public boolean shouldAdd() {
		//false is delete, true is add
		return add;
	}

	@Override
	public void toBytes(ByteBuf byteBuf) {
		byteBuf.writeInt(area.getDimension());

		byteBuf.writeInt(area.getHighPoint().getX());
		byteBuf.writeInt(area.getHighPoint().getY());
		byteBuf.writeInt(area.getHighPoint().getZ());

		byteBuf.writeInt(area.getLowPoint().getX());
		byteBuf.writeInt(area.getLowPoint().getY());
		byteBuf.writeInt(area.getLowPoint().getZ());

		byteBuf.writeInt(owner);

		byteBuf.writeBoolean(add);
	}
}
