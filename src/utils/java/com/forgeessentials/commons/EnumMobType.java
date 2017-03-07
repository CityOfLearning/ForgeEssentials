package com.forgeessentials.commons;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public enum EnumMobType {
	BOSS, GOLEM, HOSTILE, PASSIVE, VILLAGER, TAMEABLE, PLAYER;

	@Retention(RetentionPolicy.RUNTIME)
	public @interface FEMob {
		@Retention(RetentionPolicy.RUNTIME)
		@Target({ ElementType.METHOD, ElementType.FIELD })
		public @interface IsTamed {
		}

		EnumMobType type() default EnumMobType.HOSTILE;
	}

	public static EnumMobType fromName(String type) {
		for (EnumMobType val : EnumMobType.values()) {
			if (val.name().toUpperCase().equals(type.toUpperCase())) {
				return val;
			}
		}
		return null;
	}

	public static boolean isMobType(String type) {
		try {
			EnumMobType.valueOf(type.toUpperCase());
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
}
