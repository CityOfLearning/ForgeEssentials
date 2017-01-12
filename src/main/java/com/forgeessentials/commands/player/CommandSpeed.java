package com.forgeessentials.commands.player;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraftforge.permission.PermissionLevel;

public class CommandSpeed extends ForgeEssentialsCommandBase {
	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public String getCommandName() {
		return "fespeed";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/speed <speed> <player> Set or change the player's speed.";
	}

	@Override
	public String[] getDefaultAliases() {
		return new String[] { "speed" };
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.OP;
	}

	@Override
	public String getPermissionNode() {
		return ModuleCommands.PERM + ".speed";
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) throws CommandException {
		if (args.length >= 2) {
			EntityPlayer player = UserIdent.getPlayerByMatchOrUsername(sender, args[1]);
			if (args[0].equals("reset")) {
				ChatOutputHandler.chatNotification(player, "Resetting speed to regular walking speed.");
				NBTTagCompound tagCompound = new NBTTagCompound();
				player.capabilities.writeCapabilitiesToNBT(tagCompound);
				tagCompound.getCompoundTag("abilities").setTag("flySpeed", new NBTTagFloat(0.05F));
				tagCompound.getCompoundTag("abilities").setTag("walkSpeed", new NBTTagFloat(0.1F));
				player.capabilities.readCapabilitiesFromNBT(tagCompound);
				player.sendPlayerAbilities();
				return;
			}

			float speed = 0.05F;

			int multiplier = parseInt(args[0]);

			if (multiplier >= 10) {
				ChatOutputHandler.chatWarning(player,
						"Multiplier set too high. Bad things may happen, so we're throttling your speed to 10x walking speed.");
				multiplier = 10;
			}
			speed = speed * multiplier;
			NBTTagCompound tagCompound = new NBTTagCompound();
			player.capabilities.writeCapabilitiesToNBT(tagCompound);
			tagCompound.getCompoundTag("abilities").setTag("flySpeed", new NBTTagFloat(speed));
			tagCompound.getCompoundTag("abilities").setTag("walkSpeed", new NBTTagFloat(speed));
			player.capabilities.readCapabilitiesFromNBT(tagCompound);
			player.sendPlayerAbilities();

			ChatOutputHandler.chatNotification(player, "Walk/fly speed set to " + speed);
		}
	}

	@Override
	public void processCommandPlayer(EntityPlayerMP player, String[] args) throws CommandException {
		if (args.length >= 1) {
			if (args.length < 2) {
				if (args[0].equals("reset")) {
					ChatOutputHandler.chatNotification(player, "Resetting speed to regular walking speed.");
					// NetworkUtils.netHandler.sendTo(new Packet6Speed(0.0F),
					// player);
					NBTTagCompound tagCompound = new NBTTagCompound();
					player.capabilities.writeCapabilitiesToNBT(tagCompound);
					tagCompound.getCompoundTag("abilities").setTag("flySpeed", new NBTTagFloat(0.05F));
					tagCompound.getCompoundTag("abilities").setTag("walkSpeed", new NBTTagFloat(0.1F));
					player.capabilities.readCapabilitiesFromNBT(tagCompound);
					player.sendPlayerAbilities();
					return;
				}

				float speed = 0.05F;

				int multiplier = parseInt(args[0]);

				if (multiplier >= 10) {
					ChatOutputHandler.chatWarning(player,
							"Multiplier set too high. Bad things may happen, so we're throttling your speed to 10x walking speed.");
					multiplier = 10;
				}
				speed = speed * multiplier;
				NBTTagCompound tagCompound = new NBTTagCompound();
				player.capabilities.writeCapabilitiesToNBT(tagCompound);
				tagCompound.getCompoundTag("abilities").setTag("flySpeed", new NBTTagFloat(speed));
				tagCompound.getCompoundTag("abilities").setTag("walkSpeed", new NBTTagFloat(speed));
				player.capabilities.readCapabilitiesFromNBT(tagCompound);
				player.sendPlayerAbilities();

				ChatOutputHandler.chatNotification(player, "Walk/fly speed set to " + speed);
			} else {
				EntityPlayer oplayer = UserIdent.getPlayerByMatchOrUsername(player, args[1]);
				if (args[0].equals("reset")) {
					ChatOutputHandler.chatNotification(oplayer, "Resetting speed to regular walking speed.");
					NBTTagCompound tagCompound = new NBTTagCompound();
					oplayer.capabilities.writeCapabilitiesToNBT(tagCompound);
					tagCompound.getCompoundTag("abilities").setTag("flySpeed", new NBTTagFloat(0.05F));
					tagCompound.getCompoundTag("abilities").setTag("walkSpeed", new NBTTagFloat(0.1F));
					oplayer.capabilities.readCapabilitiesFromNBT(tagCompound);
					oplayer.sendPlayerAbilities();
					return;
				}

				float speed = 0.05F;

				int multiplier = parseInt(args[0]);

				if (multiplier >= 10) {
					ChatOutputHandler.chatWarning(oplayer,
							"Multiplier set too high. Bad things may happen, so we're throttling your speed to 10x walking speed.");
					multiplier = 10;
				}
				speed = speed * multiplier;
				NBTTagCompound tagCompound = new NBTTagCompound();
				oplayer.capabilities.writeCapabilitiesToNBT(tagCompound);
				tagCompound.getCompoundTag("abilities").setTag("flySpeed", new NBTTagFloat(speed));
				tagCompound.getCompoundTag("abilities").setTag("walkSpeed", new NBTTagFloat(speed));
				oplayer.capabilities.readCapabilitiesFromNBT(tagCompound);
				oplayer.sendPlayerAbilities();

				ChatOutputHandler.chatNotification(oplayer, "Walk/fly speed set to " + speed);
			}

		}
	}
}
