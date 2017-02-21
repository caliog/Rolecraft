package org.caliog.Rolecraft.XMechanics.Messages;

public enum MessageKey {

	// @formatter:off
	// GENERAL
	DEAD_MESSAGE,
	LEVEL_REACHED,
	//CLASS
	CLASS_CHANGED,
	CLASS_CHANGE_OFFER,
	//SKILLS
	FULL_STR,
	FULL_VIT,
	FULL_INT,
	FULL_DEX,
	SKILL_NEED_LEVEL,
	SKILL_NEED_MANA,
	SKILL_ACTIVE,
	//ITEMS
	NEED_CLASS1,
	NEED_CLASS2,
	NEED_EXP1,
	NEED_EXP2,
	WEAPON_LEVEL,
	//GROUP
	GROUP_CREATED,
	GROUP_CREATE_FAIL,
	GROUP_LEFT,
	GROUP_NOT_A_MEMBER,
	GROUP_FAIL,
	GROUP_JOIN_FAIL,
	GROUP_INVITED,
	GROUP_CANNOT_KICK_PLAYER;
	// @formatter:on

	public String getMessage() {
		return Msg.file.getString(this.name().toLowerCase().replaceAll("_", "-"));
	}

}
