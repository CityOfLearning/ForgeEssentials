package com.forgeessentials.playerlogger.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@DiscriminatorValue(value = "3")
public class Action03PlayerEvent extends Action {

	public static enum PlayerEventType {
		LOGIN, LOGOUT, RESPAWN, CHANGEDIM
	}

	@Column(name = "type")
	@Enumerated(EnumType.ORDINAL)
	public PlayerEventType type;

}
