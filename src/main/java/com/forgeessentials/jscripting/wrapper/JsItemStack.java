package com.forgeessentials.jscripting.wrapper;

import com.forgeessentials.data.v2.DataManager;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class JsItemStack extends JsWrapper<ItemStack> // ItemStack is final
{

	protected JsItem item;

	public JsItemStack(ItemStack that) {
		super(that);
		item = JsItem.get(that.getItem());
	}

	public String _getNbt() // tsgen ignore
	{
		return that.getTagCompound() == null ? null : DataManager.toJson(that.getTagCompound());
	}

	public void _setNbt(String value) // tsgen ignore
	{
		that.setTagCompound(value == null ? null : DataManager.fromJson(value, NBTTagCompound.class));
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof JsItemStack ? that.isItemEqual(((JsItemStack) obj).getThat()) : false;
	}

	public int getDamage() {
		return that.getItemDamage();
	}

	public String getDisplayName() {
		return that.getDisplayName();
	}

	public JsItem getItem() {
		return item;
	}

	public int getMaxDamage() {
		return that.getMaxDamage();
	}

	public int getMaxStackSize() {
		return that.getMaxStackSize();
	}

	public int getRepairCost() {
		return that.getRepairCost();
	}

	public int getStackSize() {
		return that.stackSize;
	}

	public boolean hasDisplayName() {
		return that.hasDisplayName();
	}

	public boolean isDamageable() {
		return that.isItemStackDamageable();
	}

	public boolean isDamaged() {
		return that.isItemDamaged();
	}

	public boolean isItemEnchanted() {
		return that.isItemEnchanted();
	}

	public boolean isStackable() {
		return that.isStackable();
	}

	public void setDamage(int damage) {
		that.setItemDamage(damage);
	}

	public void setDisplayName(String name) {
		that.setStackDisplayName(name);
	}

	public void setRepairCost(int cost) {
		that.setRepairCost(cost);
	}

	public void setStackSize(int size) {
		that.stackSize = size;
	}

}
