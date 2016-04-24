package net.minecraftforge.permission;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Class to hold all information regarding a permission check
 */
public class PermissionContext {

	private EntityPlayer player;

	private ICommandSender sender;

	private ICommand command;

	private int dimension;

	private Vec3 sourceLocationStart;

	private Vec3 sourceLocationEnd;

	private Vec3 targetLocationStart;

	private Vec3 targetLocationEnd;

	private Entity sourceEntity;

	private Entity targetEntity;

	public PermissionContext() {
	}

	public PermissionContext(ICommandSender sender) {
		this.sender = sender;
		if (sender instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) sender;
			this.player = player;
			dimension = player.dimension;
			sourceLocationStart = new Vec3(player.posX, player.posY, player.posZ);
		}
	}

	public PermissionContext(ICommandSender sender, ICommand command) {
		this(sender);
		this.command = command;
	}

	public ICommand getCommand() {
		return command;
	}

	public int getDimension() {
		return dimension;
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	public ICommandSender getSender() {
		return sender;
	}

	public Entity getSourceEntity() {
		return sourceEntity;
	}

	public Vec3 getSourceLocationEnd() {
		return sourceLocationEnd;
	}

	public Vec3 getSourceLocationStart() {
		return sourceLocationStart;
	}

	public Entity getTargetEntity() {
		return targetEntity;
	}

	public Vec3 getTargetLocationEnd() {
		return targetLocationEnd;
	}

	public Vec3 getTargetLocationStart() {
		return targetLocationStart;
	}

	public boolean isConsole() {
		return (player == null) && ((sender == null) || (sender instanceof MinecraftServer));
	}

	public boolean isPlayer() {
		return player instanceof EntityPlayer;
	}

	public boolean isRCon() {
		if (FMLCommonHandler.instance().getSide().isServer()) {
			return sender instanceof RConConsoleSource;
		} else {
			return false; // rcon doesn't exist on clients
		}
	}

	public PermissionContext setCommand(ICommand command) {
		this.command = command;
		return this;
	}

	public PermissionContext setDimension(int dimension) {
		this.dimension = dimension;
		return this;
	}

	public PermissionContext setPlayer(EntityPlayer player) {
		sender = this.player = player;
		return this;
	}

	public PermissionContext setSender(ICommandSender sender) {
		if (sender instanceof EntityPlayer) {
			return setPlayer((EntityPlayer) sender);
		}
		this.sender = sender;
		return this;
	}

	public PermissionContext setSource(Entity entity) {
		sourceEntity = entity;
		return this;
	}

	public PermissionContext setSourceEnd(Vec3 location) {
		sourceLocationEnd = location;
		return this;
	}

	public PermissionContext setSourceStart(Vec3 location) {
		sourceLocationStart = location;
		return this;
	}

	public PermissionContext setTarget(Entity entity) {
		targetEntity = entity;
		return this;
	}

	public PermissionContext setTargetEnd(Vec3 location) {
		targetLocationEnd = location;
		return this;
	}

	public PermissionContext setTargetStart(Vec3 location) {
		targetLocationStart = location;
		return this;
	}

}