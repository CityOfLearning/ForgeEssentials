package com.forgeessentials.jscripting.wrapper;

import java.util.UUID;

import com.forgeessentials.api.UserIdent;

import net.minecraft.world.WorldServer;

public class JsUserIdent extends JsWrapper<UserIdent> {

	public JsUserIdent(UserIdent that) {
		super(that);
	}

	public boolean checkPermission(String permissionNode) {
		return that.checkPermission(permissionNode);
	}

	public JsEntityPlayer getFakePlayer() {
		return new JsEntityPlayer(that.getFakePlayer());
	}

	public JsEntityPlayer getFakePlayer(WorldServer world) {
		return new JsEntityPlayer(that.getFakePlayer(world));
	}

	public String getPermissionProperty(String permissionNode) {
		return that.getPermissionProperty(permissionNode);
	}

	public JsEntityPlayer getPlayer() {
		return new JsEntityPlayer(that.getPlayer());
	}

	public String getUsername() {
		return that.getUsername();
	}

	public String getUsernameOrUuid() {
		return that.getUsernameOrUuid();
	}

	public UUID getUuid() {
		return that.getUuid();
	}

	@Override
	public int hashCode() {
		return that.hashCode();
	}

	public boolean hasPlayer() {
		return that.hasPlayer();
	}

	public boolean hasUsername() {
		return that.hasUsername();
	}

	public boolean hasUuid() {
		return that.hasUuid();
	}

	public boolean isFakePlayer() {
		return that.isFakePlayer();
	}

	public boolean isNpc() {
		return that.isNpc();
	}

	public boolean isPlayer() {
		return that.isPlayer();
	}

	public String toSerializeString() {
		return that.toSerializeString();
	}

	@Override
	public String toString() {
		return that.toString();
	}

}
