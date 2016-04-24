package com.forgeessentials.remote.handler.command;

import java.util.Arrays;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.remote.RemoteCommandSender;
import com.forgeessentials.remote.RemoteMessageID;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

@FERemoteHandler(id = RemoteMessageID.COMMAND)
public class CommandHandler extends GenericRemoteHandler<String> {

	public static final String PERM = PERM_REMOTE + ".command";

	public CommandHandler() {
		super(PERM, String.class);
		APIRegistry.perms.registerPermission(PERM, PermissionLevel.TRUE, "Allows to run commands remotely");
	}

	@Override
	protected RemoteResponse<?> handleData(final RemoteSession session, final RemoteRequest<String> request) {
		if (request.data == null) {
			error("Missing command");
		}

		String[] cmdLine = request.data.split(" ");
		String commandName = cmdLine[0];
		final String[] args = Arrays.copyOfRange(cmdLine, 1, cmdLine.length);

		final ICommand command = MinecraftServer.getServer().getCommandManager().getCommands().get(commandName);
		if (command == null) {
			error(String.format("Command \"/%s\" not found", commandName));
		}

		checkPermission(session, PermissionManager.getCommandPermission(command));

		TaskRegistry.runLater(() -> {
			try {
				ICommandSender sender;
				if ((session.getUserIdent() != null) && session.getUserIdent().hasPlayer()) {
					sender = session.getUserIdent().getPlayer();
				} else {
					sender = RemoteCommandSender.get(session);
				}
				command.processCommand(sender, args);
				session.trySendMessage(RemoteResponse.success(request));
			} catch (CommandException e1) {
				session.trySendMessage(RemoteResponse.error(request, e1.getMessage()));
			} catch (Exception e2) {
				e2.printStackTrace();
				session.trySendMessage(RemoteResponse.error(request, "Exception: " + e2.getMessage()));
			}
		});
		return new RemoteResponse.Ignore();
	}

}
