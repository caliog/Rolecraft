package org.caliog.myRPG.Spells;

import org.caliog.myRPG.Manager;
import org.caliog.myRPG.Entities.myClass;
import org.caliog.myRPG.Utils.ParticleEffect;

public class SpeedSpell extends Spell {
	public SpeedSpell(myClass player) {
		super(player, "Speed");
	}

	public boolean execute() {
		if (!super.execute()) {
			return false;
		}
		float p = getPower() / 1000.0F;
		if (p > 5.0F) {
			p = 5.0F;
		}
		final int power = (int) p;
		final float speed = getPlayer().getPlayer().getWalkSpeed();
		getPlayer().getPlayer().setWalkSpeed(p);

		Manager.scheduleRepeatingTask(new Runnable() {

			@Override
			public void run() {
				ParticleEffect.VILLAGER_HAPPY.display(0.1F, 0.2F, 0.1F, 0.2F, power * 2, getPlayer().getPlayer().getLocation(), 20D);

			}
		}, 20L, 1L, 600L);

		Manager.scheduleTask(new Runnable() {
			public void run() {
				getPlayer().getPlayer().setWalkSpeed(speed);
			}
		}, 600L);
		activate(600L);
		return false;
	}

	public int getMinLevel() {
		return 1;
	}

	public int getFood() {
		return (int) (10.0F * (getPower() / 500.0F)) - 1;
	}

	public float getPower() {
		int p = Math.round(getPlayer().getLevel() / 40.0F * 200.0F);
		return p + 300;
	}

	public double getDamage() {
		return 0;
	}

	public double getDefense() {
		return 0;
	}
}
