package com.forgeessentials.client.core;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.handler.QuestionerKeyHandler;
import com.forgeessentials.client.handler.ReachDistanceHandler;
import com.forgeessentials.client.hud.CUIRenderer;
import com.forgeessentials.client.hud.PermissionOverlay;
import com.forgeessentials.client.hud.PlotsRenderer;
import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.NetworkUtils.NullMessageHandler;
import com.forgeessentials.commons.output.LoggingHandler;
import com.forgeessentials.commons.network.Packet0Handshake;
import com.forgeessentials.commons.network.Packet1SelectionUpdate;
import com.forgeessentials.commons.network.Packet2Reach;
import com.forgeessentials.commons.network.Packet3PlayerPermissions;
import com.forgeessentials.commons.network.Packet4PlotsUpdate;
import com.forgeessentials.commons.network.Packet5Noclip;
import com.forgeessentials.commons.network.Packet6SyncPlots;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {

	public static final String CONFIG_CAT = Configuration.CATEGORY_GENERAL;

	/* ------------------------------------------------------------ */

	private static Configuration config;

	private static int clientTimeTicked;

	private static boolean sentHandshake = true;

	/* ------------------------------------------------------------ */

	public static boolean allowCUI, allowPUI, allowPermissionRender, allowQuestionerShortcuts;

	public static float reachDistance;

	/* ------------------------------------------------------------ */

	private static PlotsRenderer plotRenderer = new PlotsRenderer();

	private static CUIRenderer cuiRenderer = new CUIRenderer();

	private static PermissionOverlay permissionOverlay = new PermissionOverlay();

	public static Configuration getConfig() {
		return config;
	}

	/* ------------------------------------------------------------ */

	public static void resendHandshake() {
		sentHandshake = false;
	}

	private ReachDistanceHandler reachDistanceHandler = new ReachDistanceHandler();

	public ClientProxy() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void clientTickEvent(TickEvent.ClientTickEvent event) {
		clientTimeTicked++;
		if (!sentHandshake && (clientTimeTicked > 20)) {
			sentHandshake = true;
			sendClientHandshake();
		}
	}

	/* ------------------------------------------------------------ */

	@SubscribeEvent
	public void connectionOpened(FMLNetworkEvent.ClientConnectedToServerEvent e) {
		clientTimeTicked = 0;
		sentHandshake = false;
	}

	@Override
	public void doPreInit(FMLPreInitializationEvent event) {
		BuildInfo.getBuildInfo(event.getSourceFile());
		LoggingHandler.felog.info(String.format("Running ForgeEssentials client %s (%s)", BuildInfo.getFullVersion(),
				BuildInfo.getBuildHash()));

		// Initialize configuration
		config = new Configuration(event.getSuggestedConfigurationFile());
		loadConfig();

		registerNetworkMessages();
	}

	@Override
	public void load(FMLInitializationEvent event) {
		super.load(event);
		ClientCommandHandler.instance.registerCommand(new FEClientCommand());
	}

	/* ------------------------------------------------------------ */

	private void loadConfig() {
		config.load();
		config.addCustomCategoryComment(CONFIG_CAT, "Configure ForgeEssentials Client addon features.");

		allowCUI = config
				.get(Configuration.CATEGORY_GENERAL, "allowCUI", true, "Set to false to disable rendering selections.")
				.getBoolean(true);

		allowPUI = config
				.get(Configuration.CATEGORY_GENERAL, "allowPUI", true, "Set to false to disable rendering plots.")
				.getBoolean(true);
		allowPermissionRender = config.get(Configuration.CATEGORY_GENERAL, "allowPermRender", true,
				"Set to false to disable visual indication of block/item permissions").getBoolean(true);
		allowQuestionerShortcuts = config
				.get(Configuration.CATEGORY_GENERAL, "allowQuestionerShortcuts", true,
						"Use shortcut buttons to answer questions. Defaults are F8 for yes and F9 for no, change in game options menu.")
				.getBoolean(true);

		if (allowCUI) {
			// the handshake seems to happen before this...
			MinecraftForge.EVENT_BUS.register(cuiRenderer);
		}
		if (allowPUI) {
			// the handshake seems to happen before this...
			MinecraftForge.EVENT_BUS.register(plotRenderer);
		}
		if (allowPermissionRender) {
			MinecraftForge.EVENT_BUS.register(permissionOverlay);
		}
		if (allowQuestionerShortcuts) {
			new QuestionerKeyHandler();
		}

		config.save();
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.modID.equals(ForgeEssentialsClient.MODID)) {
			loadConfig();
		}
	}

	private void registerNetworkMessages() {
		// Register network messages
		NetworkUtils.registerMessageProxy(Packet0Handshake.class, 0, Side.SERVER,
				new NullMessageHandler<Packet0Handshake>() {
					/* dummy */
				});
		NetworkUtils.registerMessage(cuiRenderer, Packet1SelectionUpdate.class, 1, Side.CLIENT);
		NetworkUtils.registerMessage(reachDistanceHandler, Packet2Reach.class, 2, Side.CLIENT);
		NetworkUtils.registerMessage(permissionOverlay, Packet3PlayerPermissions.class, 3, Side.CLIENT);
		NetworkUtils.registerMessage(plotRenderer, Packet4PlotsUpdate.class, 4, Side.CLIENT);

		NetworkUtils.registerMessage((message, ctx) -> {
			FMLClientHandler.instance().getClientPlayerEntity().noClip = message.getNoclip();
			return null;
		}, Packet5Noclip.class, 5, Side.CLIENT);
		NetworkUtils.registerMessageProxy(Packet6SyncPlots.class, 6, Side.SERVER,
				new NullMessageHandler<Packet6SyncPlots>() {
					/* dummy */
				});
	}

	public void sendClientHandshake() {
		if (ForgeEssentialsClient.serverHasFE()) {
			NetworkUtils.netHandler.sendToServer(new Packet0Handshake());
			NetworkUtils.netHandler.sendToServer(new Packet6SyncPlots());
		}
	}

}
