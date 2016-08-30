package com.forgeessentials.jscripting.wrapper;

import java.util.Collection;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.CommandParserArgs;

import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.item.Item;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldServer;

public class JsCommandArgs {

	private CommandParserArgs that;

	public final JsCommandSender sender;

	public final JsEntityPlayer player;

	public final JsUserIdent ident;

	public final boolean isTabCompletion;

	public JsCommandArgs(CommandParserArgs args) {
		that = args;
		sender = new JsCommandSender(args.sender);
		player = args.senderPlayer == null ? null : new JsEntityPlayer(args.senderPlayer);
		ident = args.ident == null ? null : new JsUserIdent(args.ident);
		isTabCompletion = args.isTabCompletion;
	}

	public void checkPermission(String perm) {
		that.checkPermission(perm);
	}

	public void checkTabCompletion() {
		that.checkTabCompletion();
	}

	public void confirm(String message, Object... args) {
		that.confirm(message, args);
	}

	public void error(String message, Object... args) {
		that.error(message, args);
	}

	public WorldPoint getSenderPoint() {
		return that.getSenderPoint();
	}

	public WorldZone getWorldZone() {
		return that.getWorldZone();
	}

	public boolean hasPermission(String perm) {
		return that.hasPermission(perm);
	}

	public boolean hasPlayer() {
		return that.hasPlayer();
	}

	public boolean isEmpty() {
		return that.isEmpty();
	}

	public void needsPlayer() {
		that.needsPlayer();
	}

	public void notify(String message, Object... args) {
		that.notify(message, args);
	}

	public Block parseBlock() {
		return that.parseBlock();
	}

	public boolean parseBoolean() {
		return that.parseBoolean();
	}

	public double parseDouble() {
		return that.parseDouble();
	}

	public int parseInt() {
		return that.parseInt();
	}

	public int parseInt(int min, int max) throws CommandException {
		return that.parseInt(min, max);
	}

	public Item parseItem() {
		return that.parseItem();
	}

	public long parseLong() {
		return that.parseLong();
	}

	public String parsePermission() {
		return that.parsePermission();
	}

	public UserIdent parsePlayer() {
		return that.parsePlayer(true, false);
	}

	public UserIdent parsePlayer(boolean mustExist) {
		return that.parsePlayer(mustExist, false);
	}

	public UserIdent parsePlayer(boolean mustExist, boolean mustBeOnline) {
		return that.parsePlayer(mustExist, mustBeOnline);
	}

	public long parseTimeReadable() {
		return that.parseTimeReadable();
	}

	public WorldServer parseWorld() {
		return that.parseWorld();
	}

	public String peek() {
		return that.peek();
	}

	public String remove() {
		return that.remove();
	}

	public void requirePlayer() {
		that.requirePlayer();
	}

	public void sendMessage(IChatComponent message) {
		that.sendMessage(message);
	}

	public int size() {
		return that.size();
	}

	public void tabComplete(Collection<String> completionList) // tsgen ignore
	{
		that.tabComplete(completionList);
	}

	public void tabComplete(String... completionList) {
		that.tabComplete(completionList);
	}

	public void tabCompleteWord(String completion) {
		that.tabCompleteWord(completion);
	}

	public String[] toArray() {
		return that.toArray();
	}

	@Override
	public String toString() {
		return that.toString();
	}

	public void warn(String message, Object... args) {
		that.warn(message, args);
	}

}
