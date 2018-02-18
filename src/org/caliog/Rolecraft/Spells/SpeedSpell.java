package org.caliog.Rolecraft.Spells;

import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.XMechanics.Utils.ParticleEffect;

public class SpeedSpell extends Spell {
	public SpeedSpell(RolecraftPlayer player) {
		super(player, "Speed");
	}

	public boolean execute() {
		if (!super.execute()) {
			return false;
		}
		final float x = getPower() / (float) getMaxPower();
		float p = 0.75F;
		if (x < 0.2F)
			p = x + 0.1F;
		if (x < 0.4F)
			p = 1.2F * (x - 0.2F) + 0.3F;
		if (x < 0.6F)
			p = 1.3F * (x - 0.4F) + 0.54F;
		if (x < 0.8F)
			p = 0.7F * (x - 0.6F) + 0.8F;
		if (x == 1)
			p = 0.96F;
		final float speed = getPlayer().getPlayer().getWalkSpeed();
		getPlayer().getPlayer().setWalkSpeed((1 + p) * speed);

		Manager.scheduleRepeatingTask(new Runnable() {

			@Override
			public void run() {
				ParticleEffect.VILLAGER_HAPPY.display(0.1F, 0.2F, 0.1F, 0.2F, (int) (x * 50) + 50, getPlayer().getPlayer().getLocation(),
						20D);

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

	public double getDamage() {
		return 0;
	}

	public double getDefense() {
		return 0;
	}
}
