package com.forgeessentials.multiworld;

import com.forgeessentials.core.misc.TeleportHelper.SimpleTeleporter;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

/**
 *
 * @author Olee
 */
public class WorldServerMultiworld extends WorldServer {

	private Multiworld world;
	private SimpleTeleporter worldTeleporter;

	public WorldServerMultiworld(MinecraftServer mcServer, ISaveHandler saveHandler, WorldInfo info, int dimensionId,
			WorldServer worldServer, Profiler profilerIn, Multiworld world) {
		super(mcServer, saveHandler, info, dimensionId, profilerIn);
		mapStorage = worldServer.getMapStorage();
		worldScoreboard = worldServer.getScoreboard();
		worldTeleporter = new SimpleTeleporter(this);
		this.world = world;
	}

	@Override
	public Teleporter getDefaultTeleporter() {
		return worldTeleporter;
	}

	public Multiworld getMultiworld() {
		return world;
	}

	@Override
	protected void saveLevel() throws MinecraftException {
		perWorldStorage.saveAllData();
		saveHandler.saveWorldInfo(worldInfo);
	}

}