package com.forgeessentials.core;

import java.io.File;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.NetworkUtils.NullMessageHandler;
import com.forgeessentials.commons.network.Packet0Handshake;
import com.forgeessentials.commons.network.Packet1SelectionUpdate;
import com.forgeessentials.commons.network.Packet2Reach;
import com.forgeessentials.commons.network.Packet3PlayerPermissions;
import com.forgeessentials.commons.network.Packet4PlotsUpdate;
import com.forgeessentials.commons.network.Packet5Noclip;
import com.forgeessentials.commons.network.Packet6SyncPlots;
import com.forgeessentials.commons.network.Packet7OpenQuestionerGui;
import com.forgeessentials.commons.output.LoggingHandler;
import com.forgeessentials.commons.selections.PlotArea;
import com.forgeessentials.compat.HelpFixer;
import com.forgeessentials.core.commands.CommandFEInfo;
import com.forgeessentials.core.commands.CommandFEWorldInfo;
import com.forgeessentials.core.commands.CommandFeReload;
import com.forgeessentials.core.commands.CommandFeSettings;
import com.forgeessentials.core.commands.CommandUuid;
import com.forgeessentials.core.environment.Environment;
import com.forgeessentials.core.mcstats.ConstantPlotter;
import com.forgeessentials.core.mcstats.Metrics;
import com.forgeessentials.core.mcstats.Metrics.Graph;
import com.forgeessentials.core.misc.BlockModListFile;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.RespawnHandler;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;
import com.forgeessentials.core.moduleLauncher.config.ConfigManager;
import com.forgeessentials.core.preloader.FELaunchHandler;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.economy.plots.Plot;
import com.forgeessentials.util.FEChunkLoader;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;
import com.forgeessentials.util.events.ForgeEssentialsEventFactory;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.selections.CommandDeselect;
import com.forgeessentials.util.selections.CommandExpand;
import com.forgeessentials.util.selections.CommandExpandY;
import com.forgeessentials.util.selections.CommandPos;
import com.forgeessentials.util.selections.CommandWand;
import com.forgeessentials.util.selections.SelectionHandler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

/**
 * Main mod class
 */

@Mod(modid = ForgeEssentials.MODID, name = "Forge Essentials", version = BuildInfo.BASE_VERSION, acceptableRemoteVersions = "*", dependencies = "required-after:Forge@[10.13.4.1448,);after:WorldEdit")
public class ForgeEssentials extends ConfigLoaderBase {

	public static final class CommandPermissionRegistrationHandler {
		@SubscribeEvent
		public void serverTickEvent(ServerTickEvent event) {
			PermissionManager.registerCommandPermissions();
			MinecraftForge.EVENT_BUS.unregister(this);
		}
	}

	public static final String MODID = "ForgeEssentials";

	@Instance(value = MODID)
	public static ForgeEssentials instance;

	/* ------------------------------------------------------------ */

	public static Random rnd = new Random();
	public static final String PERM = "fe";
	public static final String PERM_CORE = PERM + ".core";
	public static final String PERM_INFO = PERM_CORE + ".info";
	public static final String PERM_RELOAD = PERM_CORE + ".reload";

	/* ------------------------------------------------------------ */
	/* ForgeEssentials core submodules */

	public static final String PERM_VERSIONINFO = PERM_CORE + ".versioninfo";

	protected static ConfigManager configManager;

	protected static ModuleLauncher moduleLauncher;

	protected static TaskRegistry tasks = new TaskRegistry();

	protected static ForgeEssentialsEventFactory factory;

	protected static TeleportHelper teleportHelper;

	protected static Questioner questioner;

	protected static FECommandManager commandManager;

	protected static Metrics mcStats;

	/* ------------------------------------------------------------ */

	protected static Graph mcStatsGeneralGraph;

	protected static File configDirectory;

	protected static boolean debugMode = false;

	protected static boolean safeMode = false;

	protected static boolean logCommandsToConsole;

	public static ConfigManager getConfigManager() {
		return configManager;
	}

	/* ------------------------------------------------------------ */

	public static File getFEDirectory() {
		return configDirectory;
	}

	public static Metrics getMcStats() {
		return mcStats;
	}

	public static Graph getMcStatsGeneralGraph() {
		return mcStatsGeneralGraph;
	}

	public static boolean isDebug() {
		return debugMode;
	}

	/* ------------------------------------------------------------ */

	public static boolean isSafeMode() {
		return safeMode;
	}

	@SuppressWarnings("unused")
	private RespawnHandler respawnHandler;

	@SuppressWarnings("unused")
	private SelectionHandler selectionHandler;

	/* ------------------------------------------------------------ */

	public ForgeEssentials() {
		LoggingHandler.init(ForgeEssentials.MODID);
		initConfiguration();
		BuildInfo.getBuildInfo(FELaunchHandler.getJarLocation());
		Environment.check();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void commandEvent(CommandEvent event) {
		if (logCommandsToConsole) {
			LoggingHandler.felog.info(String.format("Player \"%s\" used command \"/%s %s\"", event.sender.getName(),
					event.command.getCommandName(), StringUtils.join(event.parameters, " ")));
		}
	}

	private void initConfiguration() {
		configDirectory = new File(ServerUtil.getBaseDir(), "/ForgeEssentials");
		configManager = new ConfigManager(configDirectory, "main");
		configManager.registerLoader(configManager.getMainConfigName(), this);
		configManager.registerLoader(configManager.getMainConfigName(), new FEConfig());
		configManager.registerLoader(configManager.getMainConfigName(), new ChatOutputHandler());
	}

	@Override
	public void load(Configuration config, boolean isReload) {
		if (isReload) {
			Translator.translations.clear();
		}
		Translator.load();
		if (!config
				.get(FEConfig.CONFIG_CAT, "versionCheck", true, "Check for newer versions of ForgeEssentials on load?")
				.getBoolean()) {
			BuildInfo.cancelVersionCheck();
		}
		configManager.setUseCanonicalConfig(config.get(FEConfig.CONFIG_CAT, "canonicalConfigs", false,
				"For modules that support it, place their configs in this file.").getBoolean());
		debugMode = config
				.get(FEConfig.CONFIG_CAT, "debug", false, "Activates developer debug mode. Spams your FML logs.")
				.getBoolean();
		safeMode = config
				.get(FEConfig.CONFIG_CAT, "safeMode", false,
						"Activates safe mode with will ignore some errors which would normally crash the game. "
								+ "Please only enable this after being instructed to do so by FE team in response to an issue on GitHub!")
				.getBoolean();
		HelpFixer.hideWorldEditCommands = config.get(FEConfig.CONFIG_CAT, "hide_worldedit_help", true,
				"Hide WorldEdit commands from /help and only show them in //help command").getBoolean();
		logCommandsToConsole = config.get(FEConfig.CONFIG_CAT, "logCommands", false, "Log commands to console")
				.getBoolean();
	}

	@EventHandler
	public void load(FMLInitializationEvent e) {
		registerCommands();

		// Init McStats
		mcStats.createGraph("build_type").addPlotter(new ConstantPlotter(BuildInfo.getBuildType(), 1));
		mcStats.createGraph("server_type")
				.addPlotter(new ConstantPlotter(e.getSide() == Side.SERVER ? "server" : "client", 1));
		Graph gModules = mcStats.createGraph("modules");
		for (String module : ModuleLauncher.getModuleList()) {
			gModules.addPlotter(new ConstantPlotter(module, 1));
		}

		LoggingHandler.felog.info(String.format("Running ForgeEssentials %s-%s (%s)", BuildInfo.getFullVersion(),
				BuildInfo.getBuildType(), BuildInfo.getBuildHash()));
		if (BuildInfo.isOutdated()) {
			LoggingHandler.felog
					.warn("-------------------------------------------------------------------------------------");
			LoggingHandler.felog.warn(String.format("WARNING! Using ForgeEssentials build #%d, latest build is #%d", //
					BuildInfo.getBuildNumber(), BuildInfo.getBuildNumberLatest()));
			LoggingHandler.felog.warn("We highly recommend updating asap to get the latest security and bug fixes");
			LoggingHandler.felog
					.warn("-------------------------------------------------------------------------------------");
		}

		APIRegistry.getFEEventBus().post(new FEModuleEvent.FEModuleInitEvent(e));
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void playerLoggedInEvent(PlayerLoggedInEvent event) {
		if (event.player instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.player;
			UserIdent.login(player);
			PlayerInfo.login(player.getPersistentID());

			if (FEConfig.checkSpacesInNames) {
				Pattern pattern = Pattern.compile("\\s");
				Matcher matcher = pattern.matcher(player.getGameProfile().getName());
				if (matcher.find()) {
					String msg = Translator.format("Invalid name \"%s\" containing spaces. Please change your name!",
							event.player.getName());
					((EntityPlayerMP) event.player).playerNetServerHandler.kickPlayerFromServer(msg);
				}
			}

			// Show version notification
			if (BuildInfo.isOutdated() && UserIdent.get(player).checkPermission(PERM_VERSIONINFO)) {
				ChatOutputHandler.chatWarning(player, String.format(
						"ForgeEssentials build #%d outdated. Current build is #%d. Consider updating to get latest security and bug fixes.", //
						BuildInfo.getBuildNumber(), BuildInfo.getBuildNumberLatest()));
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void playerLoggedOutEvent(PlayerLoggedOutEvent event) {
		if (event.player instanceof EntityPlayerMP) {
			PlayerInfo.logout(event.player.getPersistentID());
			UserIdent.logout((EntityPlayerMP) event.player);
		}
	}

	/* ------------------------------------------------------------ */

	@SubscribeEvent
	public void playerRespawnEvent(PlayerRespawnEvent event) {
		if (event.player instanceof EntityPlayerMP) {
			UserIdent.get((EntityPlayerMP) event.player);
		}
	}

	@EventHandler
	public void postLoad(FMLPostInitializationEvent e) {
		APIRegistry.getFEEventBus().post(new FEModuleEvent.FEModulePostInitEvent(e));
		commandManager = new FECommandManager();
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		LoggingHandler.felog.info(
				String.format("Running ForgeEssentials %s (%s)", BuildInfo.getFullVersion(), BuildInfo.getBuildHash()));
		if (safeMode) {
			LoggingHandler.felog.warn(
					"You are running FE in safe mode. Please only do so if requested to by the ForgeEssentials team.");
		}

		registerNetworkMessages();

		// Init McStats
		mcStats = new Metrics(MODID + "New", BuildInfo.BASE_VERSION);
		mcStatsGeneralGraph = mcStats.createGraph("general");

		// Set up logger level
		if (debugMode) {
			((Logger) LoggingHandler.felog).setLevel(Level.DEBUG);
		} else {
			((Logger) LoggingHandler.felog).setLevel(Level.INFO);
		}

		// Register core submodules
		factory = new ForgeEssentialsEventFactory();
		teleportHelper = new TeleportHelper();
		questioner = new Questioner();
		respawnHandler = new RespawnHandler();
		selectionHandler = new SelectionHandler();

		// Load submodules
		moduleLauncher = new ModuleLauncher();
		moduleLauncher.preLoad(event);
	}

	/* ------------------------------------------------------------ */

	private void registerCommands() {
		FECommandManager.registerCommand(new CommandFEInfo());
		FECommandManager.registerCommand(new CommandFeReload());
		FECommandManager.registerCommand(new CommandFeSettings());
		FECommandManager.registerCommand(new CommandWand());
		FECommandManager.registerCommand(new CommandUuid());
		FECommandManager.registerCommand(new CommandFEWorldInfo());
		if (!ModuleLauncher.getModuleList().contains("WEIntegrationTools")) {
			FECommandManager.registerCommand(new CommandPos(1));
			FECommandManager.registerCommand(new CommandPos(2));
			FECommandManager.registerCommand(new CommandDeselect());
			FECommandManager.registerCommand(new CommandExpand());
			FECommandManager.registerCommand(new CommandExpandY());
		}
	}

	/* ------------------------------------------------------------ */

	private void registerNetworkMessages() {
		// Load network packages
		NetworkUtils.registerMessage((message, ctx) -> {
			try {
				PlayerInfo.get(ctx.getServerHandler().playerEntity).setHasFEClient(true);
			} catch (Exception e) {
				LoggingHandler.felog.error("Error getting player Info");
			}
			return null;
		}, Packet0Handshake.class, 0, Side.SERVER);
		NetworkUtils.registerMessageProxy(Packet1SelectionUpdate.class, 1, Side.CLIENT,
				new NullMessageHandler<Packet1SelectionUpdate>() {
					/* dummy */
				});

		NetworkUtils.registerMessageProxy(Packet2Reach.class, 2, Side.CLIENT, new NullMessageHandler<Packet2Reach>() {
			/* dummy */
		});
		NetworkUtils.registerMessageProxy(Packet3PlayerPermissions.class, 3, Side.CLIENT,
				new NullMessageHandler<Packet3PlayerPermissions>() {
					/* dummy */
				});
		NetworkUtils.registerMessageProxy(Packet4PlotsUpdate.class, 4, Side.CLIENT,
				new NullMessageHandler<Packet4PlotsUpdate>() {
					/* dummy */
				});
		NetworkUtils.registerMessageProxy(Packet5Noclip.class, 5, Side.CLIENT, new NullMessageHandler<Packet5Noclip>() {
			/* dummy */
		});
		NetworkUtils.registerMessage((message, ctx) -> {
			for (Plot p : Plot.getPlots()) {
				if (p.hasOwner() && p.getOwner().isPlayer() && !p.isForSale()) {
					if (p.getOwner().equals(UserIdent.get(ctx.getServerHandler().playerEntity))) {
						// players plot
						NetworkUtils.netHandler.sendTo(
								new Packet4PlotsUpdate(
										new PlotArea(p.getName(), p.getDimension(), p.getZone().getArea()), 1, true),
								ctx.getServerHandler().playerEntity);
					} else if ((p.getOwner().getPlayer() != null)
							&& ctx.getServerHandler().playerEntity.isOnSameTeam(p.getOwner().getPlayer())) {
						// teams plot, why is player coming up null?
						NetworkUtils.netHandler.sendTo(
								new Packet4PlotsUpdate(
										new PlotArea(p.getName(), p.getDimension(), p.getZone().getArea()), 2, true),
								ctx.getServerHandler().playerEntity);
					} else { // someone elses plot
						NetworkUtils.netHandler.sendTo(
								new Packet4PlotsUpdate(
										new PlotArea(p.getName(), p.getDimension(), p.getZone().getArea()), 3, true),
								ctx.getServerHandler().playerEntity);
					}
				} else { // ownerless plot
					NetworkUtils.netHandler.sendTo(
							new Packet4PlotsUpdate(new PlotArea(p.getName(), p.getDimension(), p.getZone().getArea()),
									0, true),
							ctx.getServerHandler().playerEntity);
				}
			}
			return null;
		}, Packet6SyncPlots.class, 6, Side.SERVER);

		NetworkUtils.registerMessageProxy(Packet7OpenQuestionerGui.class, 7, Side.CLIENT,
				new NullMessageHandler<Packet7OpenQuestionerGui>() {
					/* dummy */
				});

	}

	/* ------------------------------------------------------------ */

	protected void registerPermissions() {
		APIRegistry.perms.registerPermission(PERM_VERSIONINFO, PermissionLevel.OP,
				"Shows notification to the player if FE version is outdated");

		APIRegistry.perms.registerPermission("mc.help", PermissionLevel.TRUE, "Help command");

		// Teleport
		APIRegistry.perms.registerPermission(TeleportHelper.TELEPORT_CROSSDIM_FROM, PermissionLevel.TRUE,
				"Allow teleporting cross-dimensionally from a dimension");
		APIRegistry.perms.registerPermission(TeleportHelper.TELEPORT_CROSSDIM_TO, PermissionLevel.TRUE,
				"Allow teleporting cross-dimensionally to a dimension");
		APIRegistry.perms.registerPermission(TeleportHelper.TELEPORT_CROSSDIM_PORTALFROM, PermissionLevel.TRUE,
				"Allow teleporting cross-dimensionally from a dimension via a portal");
		APIRegistry.perms.registerPermission(TeleportHelper.TELEPORT_CROSSDIM_PORTALTO, PermissionLevel.TRUE,
				"Allow teleporting cross-dimensionally to a dimension via a portal (target coordinates are origin for vanilla portals)");
		APIRegistry.perms.registerPermission(TeleportHelper.TELEPORT_FROM, PermissionLevel.TRUE,
				"Allow being teleported from a certain location / dimension");
		APIRegistry.perms.registerPermission(TeleportHelper.TELEPORT_TO, PermissionLevel.TRUE,
				"Allow being teleported to a certain location / dimension");
		APIRegistry.perms.registerPermission(TeleportHelper.TELEPORT_PORTALFROM, PermissionLevel.TRUE,
				"Allow being teleported from a certain location / dimension via a portal");
		APIRegistry.perms.registerPermission(TeleportHelper.TELEPORT_PORTALTO, PermissionLevel.TRUE,
				"Allow being teleported to a certain location / dimension via a portal");

	}

	@EventHandler
	public void serverPreInit(FMLServerAboutToStartEvent e) {
		// Initialize data manager once server begins to start
		DataManager.setInstance(new DataManager(new File(ServerUtil.getWorldPath(), "FEData/json")));
		APIRegistry.getFEEventBus().post(new FEModuleServerPreInitEvent(e));
	}

	@EventHandler
	public void serverStarted(FMLServerStartedEvent e) {
		APIRegistry.getFEEventBus().post(new FEModuleEvent.FEModuleServerPostInitEvent(e));

		// commands colliding with ours!
		// CommandSetChecker.remove();
		FECommandManager.registerCommands();

		// Do permission registration in first server tick.
		// TODO This can be removed if the Permission API gets accepted!
		MinecraftForge.EVENT_BUS.register(new CommandPermissionRegistrationHandler());
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent e) {
		mcStats.start();
		BlockModListFile.makeModList();
		BlockModListFile.dumpFMLRegistries();
		ForgeChunkManager.setForcedChunkLoadingCallback(this, new FEChunkLoader());

		ServerUtil.replaceCommand("help", new HelpFixer()); // Will be
															// overwritten again
															// by commands
															// module

		registerPermissions();

		APIRegistry.getFEEventBus().post(new FEModuleEvent.FEModuleServerInitEvent(e));
	}

	@EventHandler
	public void serverStopped(FMLServerStoppedEvent e) {
		mcStats.stop();
		APIRegistry.getFEEventBus().post(new FEModuleServerStoppedEvent(e));
		FECommandManager.clearRegisteredCommands();
		Translator.save();
	}

	@EventHandler
	public void serverStopping(FMLServerStoppingEvent e) {
		APIRegistry.getFEEventBus().post(new FEModuleEvent.FEModuleServerStopEvent(e));
		PlayerInfo.discardAll();
	}

}
