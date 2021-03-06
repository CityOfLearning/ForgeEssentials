package com.forgeessentials.client;

import java.util.Map;

import com.forgeessentials.client.core.CommonProxy;
import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.commons.MobTypeLoader;
import com.forgeessentials.commons.output.LoggingHandler;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ForgeEssentialsClient.MODID, name = "ForgeEssentials Client Addon", version = BuildInfo.BASE_VERSION, guiFactory = "com.forgeessentials.client.gui.forge.FEGUIFactory", useMetadata = true, dependencies = "required-after:Forge@[10.13.4.1448,)")
public class ForgeEssentialsClient {

	public static final String MODID = "ForgeEssentialsClient";

	@SidedProxy(clientSide = "com.forgeessentials.client.core.ClientProxy", serverSide = "com.forgeessentials.client.core.CommonProxy")
	protected static CommonProxy proxy;

	@Instance("ForgeEssentialsClient")
	protected static ForgeEssentialsClient instance;

	protected static boolean serverHasFE;

	/* ------------------------------------------------------------ */

	public static boolean serverHasFE() {
		return serverHasFE;
	}

	@NetworkCheckHandler
	public boolean getServerMods(Map<String, String> map, Side side) {
		if (side.equals(Side.SERVER)) {
			if (map.containsKey("ForgeEssentials")) {
				serverHasFE = true;
				LoggingHandler.felog.info("The server is running ForgeEssentials.");
			}
		}
		return true;
	}

	@EventHandler
	public void load(FMLInitializationEvent e) {
		proxy.load(e);
	}

	/* ------------------------------------------------------------ */

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		LoggingHandler.init(ForgeEssentialsClient.MODID);
		if (e.getSide() == Side.SERVER) {
			LoggingHandler.felog.error("ForgeEssentials client does nothing on servers. You should remove it!");
		}
		MobTypeLoader.preLoad(e);
		proxy.doPreInit(e);
	}

}
