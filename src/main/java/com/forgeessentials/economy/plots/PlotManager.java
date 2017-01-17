package com.forgeessentials.economy.plots;

import java.util.Set;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.PermissionEvent;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.Packet4PlotsUpdate;
import com.forgeessentials.commons.selections.PlotArea;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader;
import com.forgeessentials.economy.plots.command.CommandPlot;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.PlayerChangedZone;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlotManager extends ServerEventHandler implements ConfigLoader {

	// public static final String CONFIG_FILE = "Plots";
	//
	// public static final String GROUP_ALL = Zone.GROUP_DEFAULT;
	// public static final String GROUP_PLOT_OWNER = "PLOT_OWNER";
	// public static final String GROUP_PLOT_MOD = "PLOT_MOD";
	//
	// public static final String GROUP_PLOT_USER = "PLOT_USER";
	//
	// public static final String SERVER_OWNER = "SERVER";
	//
	// public static final String CATEGORY = "Plots";
	//
	// // Internal data permission properties (should NEVER be edited by hand)
	// public static final String PERM_OWNER = FEPermissions.FE_INTERNAL +
	// ".plot.owner";
	// // Basic plot permissions
	// public static final String PERM = ModuleEconomy.PERM + ".plot";
	// public static final String PERM_PRICE = PERM + ".price";
	//
	// public static final String PERM_COLUMN = PERM + ".column";
	// // public static final String PERM_ADMIN = PERM + ".admin";
	// public static final String PERM_COMMAND = PERM + ".command";
	// public static final String PERM_DEFINE = PERM_COMMAND + ".define";
	// public static final String PERM_CLAIM = PERM_COMMAND + ".claim";
	// public static final String PERM_DELETE = PERM_COMMAND + ".delete";
	// public static final String PERM_BUY = PERM_COMMAND + ".buy";
	// public static final String PERM_SELL = PERM_COMMAND + ".sell";
	//
	// public static final String PERM_MODS = PERM_COMMAND + ".mods";
	// public static final String PERM_SET = PERM_COMMAND + ".set";
	// public static final String PERM_SET_PRICE = PERM_SET + ".price";
	// public static final String PERM_SET_FEE = PERM_SET + ".fee";
	// public static final String PERM_SET_NAME = PERM_SET + ".name";
	//
	// public static final String PERM_SET_OWNER = PERM_SET + ".owner";
	// public static final String PERM_PERMS = PERM_COMMAND + ".perms";
	// public static final String PERM_PERMS_BUILD = PERM_SET + ".build";
	// public static final String PERM_PERMS_USE = PERM_SET + ".use";
	// public static final String PERM_PERMS_INTERACT = PERM_SET + ".interact";
	//
	// public static final String PERM_PERMS_CHEST = PERM_SET + ".chest";
	// public static final String PERM_LIST = PERM_COMMAND + ".list";
	// public static final String PERM_LIST_OWN = PERM_LIST + ".own";
	// public static final String PERM_LIST_ALL = PERM_LIST + ".all";
	//
	// public static final String PERM_LIST_SALE = PERM_LIST + ".sale";
	// // Maximum number and total size of plots a user is allowed to claim
	// public static final String PERM_LIMIT = PERM + ".limit";
	// public static final String PERM_LIMIT_COUNT = PERM_LIMIT + ".count";
	//
	// public static final String PERM_LIMIT_SIZE = PERM_LIMIT + ".size";
	// // Maximum and minimum size a plot can be
	// public static final String PERM_SIZE = PERM + ".size";
	// public static final String PERM_SIZE_MIN = PERM_SIZE + ".min";
	//
	// public static final String PERM_SIZE_MAX = PERM_SIZE + ".max";
	// // User editable plot data permissions
	// public static final String PERM_DATA = PERM + ".data";
	// public static final String PERM_NAME = PERM_DATA + ".name";
	// public static final String PERM_FEE = PERM_DATA + ".fee";
	// public static final String PERM_FEE_TIMEOUT = PERM_DATA + ".fee.timeout";
	//
	// public static final String PERM_SELL_PRICE = PERM_DATA + ".price";

	public static void handleDeletePlot(Plot plot) {
		NetworkUtils.netHandler.sendToAll(new Packet4PlotsUpdate(
				new PlotArea(plot.getName(), plot.getDimension(), plot.getZone().getArea()), 0, false));
	}

	public static void handleNewPlot(Plot plot) {
		for (EntityPlayerMP user : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			if (plot.hasOwner() && plot.getOwner().isPlayer()) {
				if (plot.getOwner().equals(UserIdent.get(user))) { // players
																	// plot
					NetworkUtils.netHandler.sendTo(new Packet4PlotsUpdate(
							new PlotArea(plot.getName(), plot.getDimension(), plot.getZone().getArea()), 1, true),
							user);
				} else if (user.isOnSameTeam(plot.getOwner().getPlayer())) { // teams
																				// plot
					NetworkUtils.netHandler.sendTo(new Packet4PlotsUpdate(
							new PlotArea(plot.getName(), plot.getDimension(), plot.getZone().getArea()), 2, true),
							user);
				} else { // someone elses plot
					NetworkUtils.netHandler.sendTo(new Packet4PlotsUpdate(
							new PlotArea(plot.getName(), plot.getDimension(), plot.getZone().getArea()), 3, true),
							user);
				}
			} else { // ownerless plot
				NetworkUtils.netHandler.sendTo(
						new Packet4PlotsUpdate(
								new PlotArea(plot.getName(), plot.getDimension(), plot.getZone().getArea()), 0, true),
						user);
			}
		}
	}

	/* ------------------------------------------------------------ */

	public static void handlePlotOwnershipChange(Plot plot) {
		for (EntityPlayerMP user : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			if (plot.hasOwner() && plot.getOwner().isPlayer()) {
				if (plot.getOwner().equals(UserIdent.get(user))) { // players
																	// plot
					NetworkUtils.netHandler.sendTo(new Packet4PlotsUpdate(
							new PlotArea(plot.getName(), plot.getDimension(), plot.getZone().getArea()), 1, true),
							user);
				} else if (user.isOnSameTeam(plot.getOwner().getPlayer())) { // teams
																				// plot
					NetworkUtils.netHandler.sendTo(new Packet4PlotsUpdate(
							new PlotArea(plot.getName(), plot.getDimension(), plot.getZone().getArea()), 2, true),
							user);
				} else { // someone elses plot
					NetworkUtils.netHandler.sendTo(new Packet4PlotsUpdate(
							new PlotArea(plot.getName(), plot.getDimension(), plot.getZone().getArea()), 3, true),
							user);
				}
			} else { // ownerless plot
				NetworkUtils.netHandler.sendTo(
						new Packet4PlotsUpdate(
								new PlotArea(plot.getName(), plot.getDimension(), plot.getZone().getArea()), 0, true),
						user);
			}
		}
	}

	public static void serverStarting() {
		Plot.registerPermissions();
	}

	public PlotManager() {
		FECommandManager.registerCommand(new CommandPlot());
	}

	@Override
	public void load(Configuration config, boolean isReload) {
		// useStock = config.getBoolean(CONFIG_FILE, "use_stock", false,
		// STOCK_HELP);
		// String[] tags = config.get(CONFIG_FILE, "shopTags",
		// shopTags.toArray(new String[shopTags.size()]))
		// .getStringList();
		// shopTags.clear();
		// for (String tag : tags) {
		// shopTags.add(tag);
		// }
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onZoneChange(PlayerChangedZone event) {
		Plot oldPlot = Plot.getPlot(event.beforePoint.toWorldPoint());
		Plot plot = Plot.getPlot(event.afterPoint.toWorldPoint());
		// TODO: This could fail, another (non-plot) zone starts on the same
		// plane as the plot!!!
		// Plot plot = Plot.getPlot(event.afterZone.getId());
		if ((oldPlot != plot) && (plot != null)) {
			String message = Translator.format("You entered \"%s\"", plot.getNameNotNull());

			UserIdent ident = UserIdent.get(event.entityPlayer);
			Set<String> groups = plot.getZone().getStoredPlayerGroups(ident);
			if (groups.contains(Plot.GROUP_PLOT_OWNER)) {
				message += " " + Translator.translate("as owner");
				ChatOutputHandler.chatConfirmation(event.entityPlayer, message);
			} else if (groups.contains(Plot.GROUP_PLOT_USER)) {
				message += " " + Translator.translate("with user access");
				ChatOutputHandler.chatConfirmation(event.entityPlayer, message);
			} else if (!plot.hasOwner()) {
				if (plot.isForSale()) {
					message = Translator.translate("You have entered neutral plot which is open for sale");
				} else {
					message = Translator.translate("You have entered a plot owned by the server");
				}
				ChatOutputHandler.chatConfirmation(event.entityPlayer, message);
			} else {
				message += " " + Translator.format("owned by %s", plot.getOwnerName());
				ChatOutputHandler.chatConfirmation(event.entityPlayer, message);
			}

			// TODO: fee check

			long price = plot.getPrice();
			if (price == 0) {
				ChatOutputHandler.chatNotification(event.entityPlayer,
						Translator.translate("You can buy this plot for free"));
			} else if (price > 0) {
				ChatOutputHandler.chatNotification(event.entityPlayer,
						Translator.format("You can buy this plot for %s", APIRegistry.economy.toString(price)));
			}
		}
	}

	@SubscribeEvent
	public void permissionAfterLoadEvent(PermissionEvent.AfterLoad event) {
		Plot.loadPlots();
	}

	@SubscribeEvent
	public void serverStarting(FEModuleServerInitEvent event) {
		// load();
		// APIRegistry.perms.registerPermissionDescription(PERM_BASE, "Shop
		// permissions");
		// APIRegistry.perms.registerPermission(PERM_USE, PermissionLevel.TRUE,
		// "Allow usage of shops");
		// APIRegistry.perms.registerPermission(PERM_CREATE, PermissionLevel.OP,
		// "Allow creating shops");
		// APIRegistry.perms.registerPermission(PERM_DESTROY,
		// PermissionLevel.OP, "Allow destroying shops");
	}

	@Override
	public boolean supportsCanonicalConfig() {
		return true;
	}
}
