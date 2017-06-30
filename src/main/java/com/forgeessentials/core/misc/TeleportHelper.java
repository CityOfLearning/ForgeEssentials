package com.forgeessentials.core.misc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.output.LoggingHandler;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fe.event.entity.EntityPortalEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TeleportHelper extends ServerEventHandler {

	public static class SimpleTeleporter extends Teleporter {

		public SimpleTeleporter(WorldServer world) {
			super(world);
		}

		@Override
		public boolean placeInExistingPortal(Entity entity, float yaw) {
			placeInPortal(entity, yaw);
			return true;
		}

		@Override
		public void placeInPortal(Entity entity, float yaw) {
			int i = MathHelper.floor_double(entity.posX);
			int j = MathHelper.floor_double(entity.posY) - 1;
			int k = MathHelper.floor_double(entity.posZ);
			entity.setLocationAndAngles(i, j, k, entity.rotationYaw, 0.0F);
		}

		@Override
		public boolean makePortal(Entity entityIn) {
			return true;
		}

		@Override
		public void removeStalePortalLocations(long totalWorldTime) {
			/* do nothing */
		}

	}

	public static class TeleportInfo {

		private EntityPlayerMP player;

		private long start;

		private int timeout;

		private WarpPoint point;

		private WarpPoint playerPos;

		public TeleportInfo(EntityPlayerMP player, WarpPoint point, int timeout) {
			this.point = point;
			this.timeout = timeout;
			start = System.currentTimeMillis();
			this.player = player;
			playerPos = new WarpPoint(player);
		}

		public boolean check() {
			if (playerPos.distance(new WarpPoint(player)) < 0.2) {
				ChatOutputHandler.chatWarning(player, "Teleport cancelled.");
				return true;
			}
			if ((System.currentTimeMillis() - start) < timeout) {
				return false;
			}
			checkedTeleport(player, point);
			ChatOutputHandler.chatConfirmation(player, "Teleported.");
			return true;
		}

	}

	public static final String TELEPORT_CROSSDIM_FROM = "fe.teleport.crossdim.from";
	public static final String TELEPORT_CROSSDIM_TO = "fe.teleport.crossdim.to";
	public static final String TELEPORT_CROSSDIM_PORTALFROM = "fe.teleport.crossdim.portalfrom";
	public static final String TELEPORT_CROSSDIM_PORTALTO = "fe.teleport.crossdim.portalto";
	public static final String TELEPORT_FROM = "fe.teleport.from";
	public static final String TELEPORT_TO = "fe.teleport.to";
	public static final String TELEPORT_PORTALFROM = "fe.teleport.portalfrom";
	public static final String TELEPORT_PORTALTO = "fe.teleport.portalto";

	private static Map<UUID, TeleportInfo> tpInfos = new HashMap<>();

	public static boolean canTeleportTo(WarpPoint point) {
		if (point.getY() < 0) {
			return false;
		}
		Block block1 = point.getWorld().getBlockState(point.getBlockPos()).getBlock();
		Block block2 = point.getWorld()
				.getBlockState(new BlockPos(point.getBlockX(), point.getBlockY() + 1, point.getBlockZ())).getBlock();
		boolean block1Free = !block1.getMaterial().isSolid() || (block1.getBlockBoundsMaxX() < 1)
				|| (block1.getBlockBoundsMaxY() > 0);
		boolean block2Free = !block2.getMaterial().isSolid() || (block2.getBlockBoundsMaxX() < 1)
				|| (block2.getBlockBoundsMaxY() > 0);
		return block1Free && block2Free;
	}

	public static void checkedTeleport(EntityPlayerMP player, WarpPoint point) {
		if (!canTeleportTo(point)) {
			ChatOutputHandler.chatError(player,
					Translator.translate("Unable to teleport! Target location obstructed."));
			return;
		}

		try {
			PlayerInfo pi = PlayerInfo.get(player);
			pi.setLastTeleportOrigin(new WarpPoint(player));
			pi.setLastTeleportTime(System.currentTimeMillis());
			pi.setLastDeathLocation(null);
		} catch (Exception ex) {
			LoggingHandler.felog.error("Error getting player Info");
		}
		doTeleport(player, point);
	}

	public static void doTeleport(EntityPlayerMP player, WarpPoint point) {
		if (point.getWorld() == null) {
			LoggingHandler.felog.error("Error teleporting player. Target world is NULL");
			return;
		}
		// TODO: Handle teleportation of mounted entity
		player.mountEntity(null);

		if (player.dimension != point.getDimension()) {
			SimpleTeleporter teleporter = new SimpleTeleporter(point.getWorld());
			transferPlayerToDimension(player, point.getDimension(), teleporter);
		}
		player.playerNetServerHandler.setPlayerLocation(point.getX(), point.getY(), point.getZ(), point.getYaw(),
				point.getPitch());
	}

	public static void doTeleportEntity(Entity entity, WarpPoint point) {
		if (entity instanceof EntityPlayerMP) {
			doTeleport((EntityPlayerMP) entity, point);
			return;
		}
		if (entity.dimension != point.getDimension()) {
			entity.travelToDimension(point.getDimension());
		}
		entity.setLocationAndAngles(point.getX(), point.getY(), point.getZ(), point.getYaw(), point.getPitch());
	}

	public static void teleport(EntityPlayerMP player, WarpPoint point) throws CommandException {
		if (point.getWorld() == null) {
			MinecraftServer.getServer().worldServerForDimension(point.getDimension());
			if (point.getWorld() == null) {
				ChatOutputHandler.chatError(player,
						Translator.translate("Unable to teleport! Target dimension does not exist"));
				return;
			}
		}

		// Check permissions
		UserIdent ident = UserIdent.get(player);
		if (!APIRegistry.perms.checkPermission(player, TELEPORT_FROM)) {
			throw new TranslatedCommandException("You are not allowed to teleport from here.");
		}
		if (!APIRegistry.perms.checkUserPermission(ident, point.toWorldPoint(), TELEPORT_TO)) {
			throw new TranslatedCommandException("You are not allowed to teleport to that location.");
		}
		if (player.dimension != point.getDimension()) {
			if (!APIRegistry.perms.checkPermission(player, TELEPORT_CROSSDIM_FROM)) {
				throw new TranslatedCommandException("You are not allowed to teleport from this dimension.");
			}
			if (!APIRegistry.perms.checkUserPermission(ident, point.toWorldPoint(), TELEPORT_CROSSDIM_TO)) {
				throw new TranslatedCommandException("You are not allowed to teleport to that dimension.");
			}
		}

		if (!canTeleportTo(point)) {
			ChatOutputHandler.chatError(player,
					Translator.translate("Unable to teleport! Target location obstructed."));
			return;
		}

		// Setup timed teleport
		// with no warmup or cooldown we dont need this
		// tpInfos.put(player.getPersistentID(), new TeleportInfo(player, point,
		// 0));

		checkedTeleport(player, point);
	}

	public static void transferEntityToWorld(Entity entity, int oldDim, WorldServer oldWorld, WorldServer newWorld,
			Teleporter teleporter) {
		WorldProvider pOld = oldWorld.provider;
		WorldProvider pNew = newWorld.provider;
		double moveFactor = pOld.getMovementFactor() / pNew.getMovementFactor();
		double d0 = entity.posX * moveFactor;
		double d1 = entity.posZ * moveFactor;
		float f = entity.rotationYaw;
		d0 = MathHelper.clamp_int((int) d0, -29999872, 29999872);
		d1 = MathHelper.clamp_int((int) d1, -29999872, 29999872);
		if (entity.isEntityAlive()) {
			entity.setLocationAndAngles(d0, entity.posY, d1, entity.rotationYaw, entity.rotationPitch);
			teleporter.placeInPortal(entity, f);
			newWorld.spawnEntityInWorld(entity);
			newWorld.updateEntityWithOptionalForce(entity, false);
		}
		entity.setWorld(newWorld);
	}

	public static void transferPlayerToDimension(EntityPlayerMP player, int dimension, Teleporter teleporter) {
		int oldDim = player.dimension;
		MinecraftServer mcServer = MinecraftServer.getServer();

		WorldServer oldWorld = mcServer.worldServerForDimension(player.dimension);
		player.dimension = dimension;
		WorldServer newWorld = mcServer.worldServerForDimension(player.dimension);
		player.playerNetServerHandler.sendPacket(new S07PacketRespawn(player.dimension, newWorld.getDifficulty(),
				newWorld.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType())); // Forge:
																										// Use
																										// new
																										// dimensions
																										// information
		oldWorld.removePlayerEntityDangerously(player);
		player.isDead = false;

		transferEntityToWorld(player, oldDim, oldWorld, newWorld, teleporter);

		mcServer.getConfigurationManager().preparePlayer(player, oldWorld);
		player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw,
				player.rotationPitch);
		player.theItemInWorldManager.setWorld(newWorld);
		mcServer.getConfigurationManager().updateTimeAndWeatherForPlayer(player, newWorld);
		mcServer.getConfigurationManager().syncPlayerInventory(player);
		Iterator<?> iterator = player.getActivePotionEffects().iterator();
		while (iterator.hasNext()) {
			PotionEffect potioneffect = (PotionEffect) iterator.next();
			player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), potioneffect));
		}
		FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, oldDim, dimension);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void entityPortalEvent(EntityPortalEvent e) {
		UserIdent ident = null;
		if (e.entity instanceof EntityPlayer) {
			ident = UserIdent.get((EntityPlayer) e.entity);
		} else if (e.entity instanceof EntityLiving) {
			ident = APIRegistry.IDENT_NPC;
		}
		WorldPoint pointFrom = new WorldPoint(e.world, e.pos);
		WorldPoint pointTo = new WorldPoint(e.targetDimension, e.target);
		if (!APIRegistry.perms.checkUserPermission(ident, pointFrom, TELEPORT_PORTALFROM)) {
			e.setCanceled(true);
		}
		if (!APIRegistry.perms.checkUserPermission(ident, pointTo, TELEPORT_PORTALTO)) {
			e.setCanceled(true);
		}
		if (e.world.provider.getDimensionId() != e.targetDimension) {
			if (!APIRegistry.perms.checkUserPermission(ident, pointFrom, TELEPORT_CROSSDIM_PORTALFROM)) {
				e.setCanceled(true);
			}
			if (!APIRegistry.perms.checkUserPermission(ident, pointTo, TELEPORT_CROSSDIM_PORTALTO)) {
				e.setCanceled(true);
			}
		}
	}

	// @SubscribeEvent
	// public void serverTickEvent(TickEvent.ServerTickEvent e) {
	// if (e.phase == TickEvent.Phase.START) {
	// for (Iterator<TeleportInfo> it = tpInfos.values().iterator();
	// it.hasNext();) {
	// TeleportInfo tpInfo = it.next();
	// if (tpInfo.check()) {
	// it.remove();
	// }
	// }
	// }
	// }

}
