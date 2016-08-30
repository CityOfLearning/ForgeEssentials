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

	public void checkPermission(String perm) throws CommandException {
		that.checkPermission(perm);
	}

	public void checkTabCompletion() throws CommandException {
		that.checkTabCompletion();
	}

	public void confirm(String message, Object... args) {
		that.confirm(message, args);
	}

	public void error(String message, Object... args) {
		that.error(message, args);
	}

	public WorldPoint getSenderPoint() throws CommandException {
		return that.getSenderPoint();
	}

	public WorldZone getWorldZone() throws CommandException {
		return that.getWorldZone();
	}

	public boolean hasPermission(String perm) throws CommandException {
		return that.hasPermission(perm);
	}

	public boolean hasPlayer() {
		return that.hasPlayer();
	}

	public boolean isEmpty() {
		return that.isEmpty();
	}

	public void needsPlayer() throws CommandException {
		that.needsPlayer();
	}

	public void notify(String message, Object... args) {
		that.notify(message, args);
	}

	public Block parseBlock() throws CommandException {
		return that.parseBlock();
	}

	public boolean parseBoolean() throws CommandException {
		return that.parseBoolean();
	}

	public double parseDouble() throws CommandException {
		return that.parseDouble();
	}

	public int parseInt() throws CommandException {
		return that.parseInt();
	}

	public int parseInt(int min, int max) throws CommandException {
		return that.parseInt(min, max);
	}

	public Item parseItem() throws CommandException {
		return that.parseItem();
	}

	public long parseLong() throws CommandException {
		return that.parseLong();
	}

	public String parsePermission() throws CommandException {
		return that.parsePermission();
	}

	public UserIdent parsePlayer() throws CommandException {
		return that.parsePlayer(true, false);
	}

	public UserIdent parsePlayer(boolean mustExist) throws CommandException {
		return that.parsePlayer(mustExist, false);
	}

	public UserIdent parsePlayer(boolean mustExist, boolean mustBeOnline) throws CommandException {
		return that.parsePlayer(mustExist, mustBeOnline);
	}

	public long parseTimeReadable() throws CommandException {
		return that.parseTimeReadable();
	}

	public WorldServer parseWorld() throws CommandException {
		return that.parseWorld();
	}

	public String peek() {
		return that.peek();
	}

	public String remove() {
		return that.remove();
	}

	public void requirePlayer() throws CommandException {
		that.requirePlayer();
	}

	public void sendMessage(IChatComponent message) {
		that.sendMessage(message);
	}

	public int size() {
		return that.size();
	}

	public void tabComplete(Collection<String> completionList) throws CommandException // tsgen
																						// ignore
	{
		that.tabComplete(completionList);
	}

	public void tabComplete(String... completionList) throws CommandException {
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
