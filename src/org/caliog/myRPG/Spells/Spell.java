package org.caliog.myRPG.Spells;

import org.caliog.myRPG.Manager;
import org.caliog.myRPG.Entities.myClass;
import org.caliog.myRPG.Entities.myPlayer;
import org.caliog.myRPG.Messages.Msg;

public abstract class Spell {
	private final myPlayer player;
	private boolean active = false;
	private String name;

	public Spell(myClass player, String name) {
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
	 * while it is active {@link getDamage()},{@link getDefense()} will be added to player's damage,defense.<br>
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
			Msg.sendMessage(this.player.getPlayer(), "need-level-skill");
			return false;
		}
		if (this.player.getPlayer().getFoodLevel() - getFood() >= 0) {
			this.player.getPlayer().setFoodLevel(this.player.getPlayer().getFoodLevel() - getFood());
		} else {
			Msg.sendMessage(this.player.getPlayer(), "need-mana-skill");
			return false;
		}
		if (isActive()) {
			Msg.sendMessage(this.player.getPlayer(), "skill-active");
			return false;
		}
		return true;
	}

	public myPlayer getPlayer() {
		return this.player;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}