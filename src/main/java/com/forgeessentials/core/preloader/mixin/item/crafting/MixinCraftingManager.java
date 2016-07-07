package com.forgeessentials.core.preloader.mixin.item.crafting;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.forgeessentials.protection.ModuleProtection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

@Mixin(CraftingManager.class)
public abstract class MixinCraftingManager {
	@Shadow
	private List<IRecipe> recipes;

	/**
	 * Try to find a crafting result that the player is able to craft.
	 *
	 * @param inventory
	 *            the crafting inventory
	 * @param world
	 *            the world
	 */
	@Overwrite
	public ItemStack findMatchingRecipe(InventoryCrafting inventory, World world) {
		EntityPlayer player = ModuleProtection.getCraftingPlayer(inventory);
		for (IRecipe irecipe : recipes) {
			if (irecipe.matches(inventory, world)) {
				ItemStack result = irecipe.getCraftingResult(inventory);
				if (ModuleProtection.canCraft(player, result)) {
					return irecipe.getCraftingResult(inventory);
				}
			}
		}

		return null;
	}

}
