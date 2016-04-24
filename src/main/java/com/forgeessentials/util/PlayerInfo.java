package com.forgeessentials.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.data.v2.Loadable;
import com.forgeessentials.util.events.FEPlayerEvent.ClientHandshakeEstablished;
import com.forgeessentials.util.events.FEPlayerEvent.NoPlayerInfoEvent;
import com.forgeessentials.util.output.LoggingHandler;
import com.google.gson.annotations.Expose;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class PlayerInfo implements Loadable {

	private static HashMap<UUID, PlayerInfo> playerInfoMap = new HashMap<UUID, PlayerInfo>();

	/* ------------------------------------------------------------ */
	/* General */

	/**
	 * Unload PlayerInfo and save to disk
	 */
	public static void discard(UUID uuid) {
		PlayerInfo info = playerInfoMap.remove(uuid);
		if (info != null) {
			info.save();
		}
	}

	/**
	 * Discard all PlayerInfo
	 */
	public static void discardAll() {
		for (PlayerInfo info : playerInfoMap.values()) {
			info.save();
		}
		playerInfoMap.clear();
	}

	/* ------------------------------------------------------------ */
	/* Teleport */

	public static boolean exists(UUID uuid) {
		if (playerInfoMap.containsKey(uuid)) {
			return true;
		}
		if (DataManager.getInstance().exists(PlayerInfo.class, uuid.toString())) {
			return true;
		}
		return false;
	}

	public static PlayerInfo get(EntityPlayer player) throws Exception {
		return get(player.getPersistentID());
	}

	public static PlayerInfo get(UserIdent ident) throws Exception {
		if (!ident.hasUuid()) {
			throw new NullPointerException();
		}
		return get(ident.getUuid());
	}

	public static PlayerInfo get(UUID uuid) throws Exception {
		PlayerInfo info = playerInfoMap.get(uuid);
		if (info != null) {
			return info;
		}

		// Attempt to populate this info with some data from our storage
		info = DataManager.getInstance().load(PlayerInfo.class, uuid.toString());
		if (info != null) {
			playerInfoMap.put(uuid, info);
			return info;
		}

		// Create new player info data
		EntityPlayerMP player = UserIdent.getPlayerByUuid(uuid);
		info = new PlayerInfo(uuid);
		playerInfoMap.put(uuid, info);
		if (player != null) {
			APIRegistry.getFEEventBus().post(new NoPlayerInfoEvent(player));
		}
		if (info != null) {
			return info;
		} else {
			throw new NullPointerException();
		}
	}

	/* ------------------------------------------------------------ */
	/* Selection */

	public static Collection<PlayerInfo> getAll() {
		return playerInfoMap.values();
	}

	public static void login(UUID uuid) {
		PlayerInfo pi;
		try {
			pi = get(uuid);

			pi.lastActivity = System.currentTimeMillis();
			pi.timePlayedRef = System.currentTimeMillis();
			pi.lastLogin = new Date();
		} catch (Exception e) {
			LoggingHandler.felog.error("Error getting player Info");
		}
	}

	public static void logout(UUID uuid) {
		if (!playerInfoMap.containsKey(uuid)) {
			return;
		}
		PlayerInfo pi = playerInfoMap.remove(uuid);
		pi.getTimePlayed();
		pi.lastLogout = new Date();
		pi.timePlayedRef = 0;
		pi.save();
	}

	/* ------------------------------------------------------------ */
	/* Selection wand */

	public final UserIdent ident;

	@Expose(serialize = false)
	private boolean hasFEClient = false;

	private WarpPoint home;

	/* ------------------------------------------------------------ */
	/* Inventory groups */

	private WarpPoint lastTeleportOrigin;

	private WarpPoint lastDeathLocation;

	/* ------------------------------------------------------------ */
	/* Stats / time */

	private long lastTeleportTime = 0;

	private Point sel1;

	private Point sel2;

	private int selDim;

	@Expose(serialize = false)
	private boolean wandEnabled = false;

	@Expose(serialize = false)
	private String wandID;

	@Expose(serialize = false)
	private int wandDmg;

	/* ------------------------------------------------------------ */

	private Map<String, List<ItemStack>> inventoryGroups = new HashMap<>();

	private String activeInventoryGroup = "default";

	private long timePlayed = 0;

	@Expose(serialize = false)
	private long timePlayedRef = 0;

	/* ------------------------------------------------------------ */

	private Date firstLogin = new Date();

	private Date lastLogin = new Date();

	private Date lastLogout;

	@Expose(serialize = false)
	private long lastActivity = System.currentTimeMillis();

	private HashMap<String, Date> namedTimeout = new HashMap<String, Date>();

	private PlayerInfo(UUID uuid) {
		ident = UserIdent.get(uuid);
	}

	@Override
	public void afterLoad() {
		if (namedTimeout == null) {
			namedTimeout = new HashMap<String, Date>();
		}
		lastActivity = System.currentTimeMillis();
		if ((activeInventoryGroup == null) || activeInventoryGroup.isEmpty()) {
			activeInventoryGroup = "default";
		}
	}

	/**
	 * Check, if a timeout passed
	 * 
	 * @param name
	 * @return true, if the timeout passed
	 */
	public boolean checkTimeout(String name) {
		Date timeout = namedTimeout.get(name);
		if (timeout == null) {
			return true;
		}
		if (timeout.after(new Date())) {
			return false;
		}
		namedTimeout.remove(name);
		return true;
	}

	public Date getFirstLogin() {
		return firstLogin;
	}

	/* ------------------------------------------------------------ */

	public boolean getHasFEClient() {
		return hasFEClient;
	}

	public WarpPoint getHome() {
		return home;
	}

	public long getInactiveTime() {
		return System.currentTimeMillis() - lastActivity;
	}

	public String getInventoryGroup() {
		return activeInventoryGroup;
	}

	public List<ItemStack> getInventoryGroupItems(String name) {
		return inventoryGroups.get(name);
	}

	public Map<String, List<ItemStack>> getInventoryGroups() {
		return inventoryGroups;
	}

	public WarpPoint getLastDeathLocation() {
		return lastDeathLocation;
	}

	/* ------------------------------------------------------------ */
	/* Timeouts */

	public Date getLastLogin() {
		return lastLogin;
	}

	public Date getLastLogout() {
		return lastLogout;
	}

	public WarpPoint getLastTeleportOrigin() {
		return lastTeleportOrigin;
	}

	/* ------------------------------------------------------------ */
	/* Wand */

	public long getLastTeleportTime() {
		return lastTeleportTime;
	}

	/**
	 * Get the remaining timeout in milliseconds
	 */
	public long getRemainingTimeout(String name) {
		Date timeout = namedTimeout.get(name);
		if (timeout == null) {
			return 0;
		}
		return timeout.getTime() - new Date().getTime();
	}

	public Point getSel1() {
		return sel1;
	}

	public Point getSel2() {
		return sel2;
	}

	public int getSelDim() {
		return selDim;
	}

	public long getTimePlayed() {
		if (isLoggedIn() && (timePlayedRef != 0)) {
			timePlayed += System.currentTimeMillis() - timePlayedRef;
			timePlayedRef = System.currentTimeMillis();
		}
		return timePlayed;
	}

	/* ------------------------------------------------------------ */
	/* Selection */

	public int getWandDmg() {
		return wandDmg;
	}

	public String getWandID() {
		return wandID;
	}

	public boolean isLoggedIn() {
		return ident.hasPlayer();
	}

	public boolean isWandEnabled() {
		return wandEnabled;
	}

	/**
	 * Notifies the PlayerInfo to save itself to the Data store.
	 */
	public void save() {
		DataManager.getInstance().save(this, ident.getUuid().toString());
	}

	public void setActive() {
		lastActivity = System.currentTimeMillis();
	}

	/* ------------------------------------------------------------ */
	/* Inventory groups */

	public void setActive(long delta) {
		lastActivity = System.currentTimeMillis() - delta;
	}

	public void setHasFEClient(boolean status) {
		hasFEClient = status;
		APIRegistry.getFEEventBus().post(new ClientHandshakeEstablished(ident.getPlayer()));
	}

	public void setHome(WarpPoint home) {
		this.home = home;
	}

	public void setInventoryGroup(String name) {
		if (!activeInventoryGroup.equals(name)) {
			// Get the new inventory
			List<ItemStack> newInventory = inventoryGroups.get(name);
			// Create empty inventory if it did not exist yet
			if (newInventory == null) {
				newInventory = new ArrayList<>();
			}

			// ChatOutputHandler.felog.info(String.format("Changing inventory
			// group for %s from %s to %s",
			// ident.getUsernameOrUUID(), activeInventoryGroup, name));
			/*
			 * ChatOutputHandler.felog.info("Items in old inventory:"); for (int
			 * i = 0; i < ident.getPlayer().inventory.getSizeInventory(); i++) {
			 * ItemStack itemStack =
			 * ident.getPlayer().inventory.getStackInSlot(i); if (itemStack !=
			 * null) ChatOutputHandler.felog.info("  " +
			 * itemStack.getDisplayName()); } ChatOutputHandler.felog.info(
			 * "Items in new inventory:"); for (ItemStack itemStack :
			 * newInventory) if (itemStack != null)
			 * ChatOutputHandler.felog.info("  " + itemStack.getDisplayName());
			 */

			// Swap player inventory and store the old one
			inventoryGroups.put(activeInventoryGroup, PlayerUtil.swapInventory(ident.getPlayerMP(), newInventory));
			// Clear the inventory-group that was assigned to the player
			// (optional)
			inventoryGroups.put(name, null);
			// Save the new active inventory-group
			activeInventoryGroup = name;
			save();
		}
	}

	/* ------------------------------------------------------------ */
	/* Teleportation */

	public void setLastDeathLocation(WarpPoint lastDeathLocation) {
		this.lastDeathLocation = lastDeathLocation;
	}

	public void setLastTeleportOrigin(WarpPoint lastTeleportStart) {
		lastTeleportOrigin = lastTeleportStart;
	}

	public void setLastTeleportTime(long currentTimeMillis) {
		lastTeleportTime = currentTimeMillis;
	}

	public void setSel1(Point point) {
		sel1 = point;
	}

	public void setSel2(Point point) {
		sel2 = point;
	}

	public void setSelDim(int dimension) {
		selDim = dimension;
	}

	public void setWandDmg(int wandDmg) {
		this.wandDmg = wandDmg;
	}

	public void setWandEnabled(boolean wandEnabled) {
		this.wandEnabled = wandEnabled;
	}

	/* ------------------------------------------------------------ */
	/* Other */

	public void setWandID(String wandID) {
		this.wandID = wandID;
	}

	/**
	 * Start a named timeout. Use {@link #checkTimeout(String)} to check if the
	 * timeout has passed.
	 * 
	 * @param name
	 *            Unique name of the timeout
	 * @param milliseconds
	 *            Timeout in milliseconds
	 */
	public void startTimeout(String name, long milliseconds) {
		Date date = new Date();
		date.setTime(date.getTime() + milliseconds);
		namedTimeout.put(name, date);
	}

}
