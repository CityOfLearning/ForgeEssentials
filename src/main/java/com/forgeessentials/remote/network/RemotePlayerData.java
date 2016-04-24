package com.forgeessentials.remote.network;

import com.forgeessentials.playerlogger.entity.PlayerData;

public class RemotePlayerData {

	public long id;

	public String uuid;

	public String username;

	public RemotePlayerData(PlayerData playerData) {
		id = playerData.id;
		uuid = playerData.uuid;
		username = playerData.username;
	}

}