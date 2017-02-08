package org.caliog.Rolecraft.Entities;

import java.util.UUID;

import org.caliog.Rolecraft.Manager;

public abstract class Fighter {
	private boolean fighting;
	protected int task;
	private double health;
	private UUID killerId;

	public void delete() {
		if (this.task != -1) {
			Manager.cancelTask(Integer.valueOf(this.task));
		}
	}

	public abstract double getDefense();

	public abstract double getDamage();

	public abstract UUID getUniqueId();

	public boolean isFighting() {
		return this.fighting;
	}

	public double getHealth() {
		if (this.health < 0.0D) {
			return 1.0D;
		}
		return this.health;
	}

	public void setHealth(double hp) {
		this.health = hp;
	}

	public boolean damage(double d) {
		if (d <= 0.0D) {
			return this.health <= 0.0D;
		}
		this.health -= d;
		return this.health <= 0.0D;
	}

	public void fight() {
		if (this.task != -1) {
			Manager.cancelTask(Integer.valueOf(this.task));
		}
		fighting = true;
		Manager.scheduleTask(new Runnable() {
			public void run() {
				fighting = false;
			}
		}, 240L);
	}

	public UUID getKillerId() {
		return this.killerId;
	}

	public void setKiller(UUID entityId) {
		this.killerId = entityId;
	}
}
