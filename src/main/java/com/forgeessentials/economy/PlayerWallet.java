package com.forgeessentials.economy;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.economy.Wallet;

public class PlayerWallet implements Wallet {

	private long amount;

	private double fraction;

	public PlayerWallet(long amount) {
		this.amount = amount;
	}

	@Override
	public void add(double amount) {
		fraction += amount;
		long rest = (long) fraction;
		fraction -= rest;
		this.amount += rest;
	}

	@Override
	public void add(long amount) {
		this.amount += amount;
	}

	@Override
	public boolean covers(long value) {
		if (amount < value) {
			return false;
		}
		return true;
	}

	@Override
	public long get() {
		return amount;
	}

	@Override
	public void set(long value) {
		amount = value;
	}

	@Override
	public String toString() {
		return APIRegistry.economy.toString(amount);
	}

	@Override
	public boolean withdraw(long value) {
		if (amount < value) {
			return false;
		}
		amount -= value;
		return true;
	}

}
