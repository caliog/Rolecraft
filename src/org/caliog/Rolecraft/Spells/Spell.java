package org.caliog.Rolecraft.Spells;

import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.Player.RolecraftAbstrPlayer;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.XMechanics.Messages.MessageKey;
import org.caliog.Rolecraft.XMechanics.Messages.Msg;

public abstract class Spell {
	private final RolecraftAbstrPlayer player;
	private boolean active = false;
	private String name;

	public Spell(RolecraftPlayer player, String name) {
		this.player = player;
		this.setName(name);
	}

	public abstract int getMinLevel();

	public abstract int getFood();

	public abstract float getPower();

	public abstract double getDamage();

	public abstract double getDefense();

	public boolean isActive() {
		return this.active;
	}

	/**
	 * This is an optional method to use in the execute override.<br>
	 * It will tell the plugin that the spell is still active,<br>
	 * while it is active {@link getDamage()},{@link getDefense()} will be added
	 * to player's damage,defense.<br>
	 * 
	 * @param time
	 *            The time (in ticks = 20 * seconds) the spell will be active
	 * 
	 * @return false, if spell is already active
	 */

	public boolean activate(long time) {
		if (this.active) {
			return false;
		}
		this.active = true;

		SpellBarManager.timer(player.getPlayer(), name, time);

		Manager.scheduleTask(new Runnable() {

			@Override
			public void run() {
				active = false;
			}
		}, time);
		return true;
	}

	/**
	 * 
	 * If you override this method, make sure to call super.execute() first.<br>
	 * It will return true if and only if:<br>
	 * <ol>
	 * <li>Player level is greater than or equal to {@link getMinLevel()}</li>
	 * <li>Player food is greater than or equal to {@link getFood()}</li>
	 * <li>the spell is not already active</li>
	 * </ol>
	 * 
	 * @return true, if player could cast this spell
	 */
	public boolean execute() {
		if (this.player.getLevel() < getMinLevel()) {
			Msg.sendMessage(this.player.getPlayer(), MessageKey.SKILL_NEED_LEVEL);
			return false;
		}
		if (this.player.getPlayer().getFoodLevel() - getFood() >= 0) {
			this.player.getPlayer().setFoodLevel(this.player.getPlayer().getFoodLevel() - getFood());
		} else {
			Msg.sendMessage(this.player.getPlayer(), MessageKey.SKILL_NEED_MANA);
			return false;
		}
		if (isActive()) {
			Msg.sendMessage(this.player.getPlayer(), MessageKey.SKILL_ACTIVE);
			return false;
		}
		return true;
	}

	public RolecraftAbstrPlayer getPlayer() {
		return this.player;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}