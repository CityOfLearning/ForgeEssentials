package com.forgeessentials.remote.network;

import com.forgeessentials.playerlogger.entity.BlockData;

public class RemoteBlockData {

	public long id;

	public String name;

	public RemoteBlockData(BlockData blockData) {
		id = blockData.id;
		name = blockData.name;
	}

}