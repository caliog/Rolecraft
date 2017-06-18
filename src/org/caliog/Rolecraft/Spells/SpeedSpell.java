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
		final int power = getPower();
		float p = 0.75F;
		if (power < 10)
			p = power / 30F;
		if (power < 20)
			p = power / 60F + 1 / 6F;
		if (power < 30)
			p = power / 80F + 2 / 8F;
		if (power < 50)
			p = power / 100F + (5 / 8F - 3 / 10F);
		final float speed = getPlayer().getPlayer().getWalkSpeed();
		getPlayer().getPlayer().setWalkSpeed((1 + p) * speed);

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

	public double getDamage() {
		return 0;
	}

	public double getDefense() {
		return 0;
	}
}
