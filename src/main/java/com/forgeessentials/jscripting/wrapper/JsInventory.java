package com.forgeessentials.jscripting.wrapper;

import net.minecraft.inventory.IInventory;

public class JsInventory<T extends IInventory> extends JsWrapper<T> {

	public JsInventory(T that) {
		super(that);
	}

	public String getName() {
		return that.getInventoryName();
	}

	public int getSize() {
		return that.getSizeInventory();
	}

	public JsItemStack getStackInSlot(int slot) {
		return new JsItemStack(that.getStackInSlot(slot));
	}

	public int getStackLimit() {
		return that.getInventoryStackLimit();
	}

	public boolean hasCustomName() {
		return that.hasCustomInventoryName();
	}

	public boolean isStackValidForSlot(int slot, JsItemStack stack) {
		return that.isItemValidForSlot(slot, stack.getThat());
	}

	public void setStackInSlot(int slot, JsItemStack stack) {
		that.setInventorySlotContents(slot, stack.getThat());
	}

}
