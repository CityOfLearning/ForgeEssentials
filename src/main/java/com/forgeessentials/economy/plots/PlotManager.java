package com.forgeessentials.economy.plots;

import java.util.Set;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.api.permissions.PermissionEvent;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.Packet4PlotsUpdate;
import com.forgeessentials.commons.selections.PlotArea;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.economy.plots.command.CommandPlot;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.PlayerChangedZone;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlotManager extends ServerEventHandler implements ConfigLoader {

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
				ChatOutputHandler.chatNotification(event.entityPlayer, message);
			} else if (groups.contains(Plot.GROUP_PLOT_MOD)) {
				message += " " + Translator.translate("with mod access");
				ChatOutputHandler.chatNotification(event.entityPlayer, message);
			} else if (groups.contains(Plot.GROUP_PLOT_USER)) {
				message += " " + Translator.translate("with user access");
				ChatOutputHandler.chatNotification(event.entityPlayer, message);
			} else if (groups.contains(Plot.GROUP_PLOT_GUEST)) {
				message += " " + Translator.translate("with guest access");
				ChatOutputHandler.chatNotification(event.entityPlayer, message);
			} else if (!plot.hasOwner()) {

				if (plot.isForSale()) {
					message = Translator.translate("You have entered neutral plot which is open for sale");
				} else {
					message = Translator.translate("You have entered a plot owned by the server");
				}
				ChatOutputHandler.chatNotification(event.entityPlayer, message);
			} else {
				if (plot.getFee() > 0) {
					QuestionerCallback handler = response -> {
						if (response == null) {
							ChatOutputHandler.chatError(event.entityPlayer, "Entry fee request timed out");
							return;
						}
						if (response == false) {
							ChatOutputHandler.chatError(event.entityPlayer, "Then you may not enter this area");
							WarpPoint p = new WarpPoint(event.beforePoint);
							((EntityPlayerMP) event.entityPlayer).playerNetServerHandler.setPlayerLocation(p.getX(),
									p.getY(), p.getZ(), event.entityPlayer.rotationYaw,
									event.entityPlayer.rotationPitch);
							return;
						}
						Wallet wallet = APIRegistry.economy.getWallet(UserIdent.get(event.entityPlayer));
						if (!wallet.covers(plot.getFee())) {
							WarpPoint p = new WarpPoint(event.beforePoint);
							((EntityPlayerMP) event.entityPlayer).playerNetServerHandler.setPlayerLocation(p.getX(),
									p.getY(), p.getZ(), event.entityPlayer.rotationYaw,
									event.entityPlayer.rotationPitch);
							throw new ModuleEconomy.CantAffordException();
						}

						wallet.withdraw(plot.getFee());
						ChatOutputHandler.chatConfirmation(event.entityPlayer,
								Translator.format("You may now enter %s plot", plot.getOwnerName()));

						plot.getZone().addPlayerToGroup(UserIdent.get(event.entityPlayer), Plot.GROUP_PLOT_GUEST);

					};
					String feeMessage = Translator.format("You must pay %s, to enter this area",
							APIRegistry.economy.toString(plot.getFee()));
					try {
						Questioner.addChecked(event.entityPlayer, feeMessage, handler, plot.getFeeTimeout());
					} catch (CommandException e) {
						// not really a big deal if we get an exception cuz it
						// pauses the game now so...
					}
					event.setCanceled(true);
				} else if (plot.getExclude()) {
					WarpPoint p = new WarpPoint(event.beforePoint);
					((EntityPlayerMP) event.entityPlayer).playerNetServerHandler.setPlayerLocation(p.getX(), p.getY(),
							p.getZ(), event.entityPlayer.rotationYaw, event.entityPlayer.rotationPitch);
					ChatOutputHandler.chatError(event.entityPlayer,
							Translator.format("Only approved users and guests can enter this plot"));
					event.setCanceled(true);
					return;
				}
				message += " " + Translator.format("owned by %s", plot.getOwnerName());
				ChatOutputHandler.chatNotification(event.entityPlayer, message);
			}

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

	}

	@Override
	public boolean supportsCanonicalConfig() {
		return true;
	}
}
