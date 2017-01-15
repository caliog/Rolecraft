package org.caliog.myRPG.Spells;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.caliog.myRPG.Manager;
import org.caliog.myRPG.Entities.myClass;

public class InvisibleSpell extends Spell {
	public InvisibleSpell(myClass player) {
		super(player, "Tarnung");
	}

	public boolean execute() {
		if (!super.execute()) {
			return false;
		}
		if (getPlayer().isFighting()) {
			return false;
		}
		int time = Math.round(getPower() * 20L);
		activate(time);
		Manager.scheduleRepeatingTask(new Runnable() {
			public void run() {
				getPlayer().getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1200, 2));
			}
		}, 0L, 200L, time);
		Manager.scheduleTask(new Runnable() {
			public void run() {
				getPlayer().getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
			}
		}, time);

		return true;
	}

	public int getMinLevel() {
		return 5;
	}

	public int getFood() {
		return Math.round(getPower() / 90.0F * 12.0F);
	}

	public float getPower() {
		int level = getPlayer().getLevel();
		if (level <= 10) {
			return level * 5;
		}
		if (level <= 20) {
			return level * 2 + 10;
		}
		return 90;
	}

	public double getDamage() {
		return 0;
	}

	public double getDefense() {
		return 0;
	}
}
