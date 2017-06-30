package com.forgeessentials.multiworld;

import java.io.File;

import com.forgeessentials.core.misc.TeleportHelper.SimpleTeleporter;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;

public class WorldServerMultiworld extends WorldServer {

	private WorldServer delegate;
	private SimpleTeleporter worldTeleporter;

	public WorldServerMultiworld(MinecraftServer mcServer, ISaveHandler saveHandler, WorldInfo info, int dimensionId,
			WorldServer worldServer, Profiler profilerIn, Multiworld world) {
		super(mcServer, saveHandler, info, dimensionId, profilerIn);
		this.delegate = worldServer;
		worldTeleporter = new SimpleTeleporter(this);
	}

	@Override
	public File getChunkSaveLocation() {
		return saveHandler.getWorldDirectory();
	}

	@Override
	public Teleporter getDefaultTeleporter() {
		return worldTeleporter;
	}

	public World init()
    {
        this.mapStorage = this.delegate.getMapStorage();
        this.worldScoreboard = this.delegate.getScoreboard();
        String s = VillageCollection.fileNameForProvider(this.provider);
        VillageCollection villagecollection = (VillageCollection)this.perWorldStorage.loadData(VillageCollection.class, s);

        if (villagecollection == null)
        {
            this.villageCollectionObj = new VillageCollection(this);
            this.perWorldStorage.setData(s, this.villageCollectionObj);
        }
        else
        {
            this.villageCollectionObj = villagecollection;
            this.villageCollectionObj.setWorldsForAll(this);
        }

        return this;
    }
	
	@Override
	protected void saveLevel() throws MinecraftException {
		perWorldStorage.saveAllData();
		saveHandler.saveWorldInfo(worldInfo);
	}

//	public void syncScoreboard() {
//		worldScoreboard = DimensionManager.getWorld(0).getScoreboard();
//	}

}